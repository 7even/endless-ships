(ns endless-ships.configurator
  "Checks whether outfits fit into a ship the same way the game does
  (see Outfit::CanAdd in the game sources).")

(def ^:private attribute-precision
  "The game stores attributes as integers multiplied by this number."
  10000)

(defn- precise [value]
  ;; the parser reads decimals as single-precision floats (0.42 becomes 0.41999998),
  ;; while the game uses doubles, so on the JVM floats go through their decimal form
  (let [value #?(:clj (if (instance? Float value)
                        (Double/parseDouble (str value))
                        value)
                 :cljs value)]
    (long (* value attribute-precision))))

(defn- from-precise [value]
  (/ value
     (double attribute-precision)))

(defn value
  "Returns the value of an attribute in normal units from precise attributes."
  [attributes attr-name]
  (from-precise (get attributes attr-name 0)))

(defn add-outfit
  "Adds `quantity` instances of an outfit's attributes to precise ship attributes."
  [ship-attributes outfit-attributes quantity]
  (reduce-kv (fn [attributes attr-name value]
               (update attributes
                       attr-name
                       (fnil + 0)
                       (* (precise value)
                          quantity)))
             ship-attributes
             outfit-attributes))

(defn hull-attributes
  "Returns precise attributes of an empty ship: its own attributes plus gun ports
  and turret mounts which the game counts from hardpoints. Drones are always
  automatons (see Ship::FinishLoading in the game sources)."
  [{:keys [attributes guns turrets category]}]
  (cond-> (add-outfit {} attributes 1)
    true (assoc "gun ports" (precise (or guns 0))
                "turret mounts" (precise (or turrets 0)))
    (and (= category "Drone")
         (zero? (get attributes "automaton" 0))) (assoc "automaton" (precise 1))))

(defn ship-attributes
  "Returns precise attributes of a ship with the given outfits installed.
  `outfits` is a sequence of {:name :quantity} and `outfit-attributes` is a function
  returning raw attributes of an outfit by its name."
  [ship outfits outfit-attributes]
  (reduce (fn [attributes {:keys [name quantity]}]
            (add-outfit attributes
                        (outfit-attributes name)
                        quantity))
          (hull-attributes ship)
          outfits))

(defn- minimum
  "Returns the precise minimum value of an attribute or nil if any value is allowed."
  [minimums ship-attributes outfit-attributes attr-name]
  (let [default (if (contains? minimums attr-name)
                  (some-> (get minimums attr-name)
                          precise)
                  0)]
    (when (some? default)
      (if (= attr-name "required crew")
        ;; only automatons may have a required crew of 0
        (if (or (not (zero? (get ship-attributes "automaton" 0)))
                (not (zero? (get outfit-attributes "automaton" 0))))
          0
          1)
        default))))

(defn can-add
  "Returns how many of `quantity` instances of an outfit (given by its raw
  attributes) can be added to a ship with the given precise attributes."
  [minimums ship-attributes outfit-attributes quantity]
  (reduce-kv (fn [quantity attr-name value]
               (let [current (get ship-attributes attr-name 0)
                     added (precise value)
                     limit (minimum minimums ship-attributes outfit-attributes attr-name)]
                 (cond
                   (or (nil? limit)
                       (>= (+ current
                              (* added quantity))
                           limit)) quantity
                   (zero? added) 0
                   :else (quot (- current limit)
                               (- added)))))
             quantity
             outfit-attributes))

(defn violations
  "Returns ship attributes that are below their minimum, as a map from attribute
  name to {:value :minimum} (in normal units)."
  [minimums ship-attributes]
  (reduce-kv (fn [result attr-name value]
               (let [limit (minimum minimums ship-attributes {} attr-name)]
                 (if (and (some? limit)
                          (< value limit))
                   (assoc result
                          attr-name
                          {:value (from-precise value)
                           :minimum (from-precise limit)})
                   result)))
             {}
             ship-attributes))

(def ^:private maximum-temperature
  "Entity::MAXIMUM_TEMPERATURE in the game sources."
  100)

(defn- exp [x]
  #?(:clj (Math/exp x)
     :cljs (js/Math.exp x)))

(defn- cooling-efficiency
  "An S-curve of the cooling inefficiency attribute (see ShipAttributeCache.cpp)."
  [inefficiency]
  (if (zero? inefficiency)
    1
    (- (+ 2
          (/ 2
             (+ 1 (exp (/ inefficiency -2)))))
       (/ 4
          (+ 1 (exp (/ inefficiency -4)))))))

(defn- safe-divide
  "Division that works the same on both platforms (boxed numbers throw on division by zero
  on the JVM): x / 0 is infinity and 0 / 0 is 0."
  [x y]
  (cond
    (not (zero? y)) (/ x y)
    (zero? x) 0.0
    :else ##Inf))

(defn- mass-range
  "Returns [value with full cargo, value with no cargo] of (f mass)."
  [f empty-mass cargo-space]
  [(f (+ empty-mass cargo-space))
   (f empty-mass)])

(defn ship-stats
  "Returns characteristics of a ship the way the game shows them for a ship in a shipyard
  (see ShipInfoDisplay.cpp, Ship.cpp and Entity.cpp in the game sources).
  `attributes` are precise ship attributes, `mass` is the mass of the hull with all outfits
  and `firing` is {:energy :heat} that all weapons use per second.
  Energy and heat values are per second."
  [attributes mass firing]
  (let [a #(value attributes %)
        cargo-space (a "cargo space")
        ;; movement stats are influenced by inertia reduction
        inertia-reduction (+ 1 (a "inertia reduction"))
        thrust (a "thrust")
        afterburner-thrust (a "afterburner thrust")
        reverse-thrust (a "reverse thrust")
        main-thrust (if (zero? thrust)
                      afterburner-thrust
                      thrust)
        both-thrusts? (and (pos? thrust)
                           (pos? afterburner-thrust))
        ;; Ship::Drag: drag is limited by the inertial mass of the ship with no cargo
        drag (min (/ (a "drag")
                     (+ 1 (a "drag reduction")))
                  (/ mass inertia-reduction))
        speed #(* 60
                  (safe-divide % drag))
        acceleration (fn [thrust]
                       (mass-range #(safe-divide (* 3600
                                                    thrust
                                                    (+ 1 (a "acceleration multiplier")))
                                                 (/ % inertia-reduction))
                                   mass
                                   cargo-space))
        shield-regen (* (+ (a "shield generation")
                           (a "delayed shield generation"))
                        (+ 1 (a "shield generation multiplier")))
        hull-repair (* (+ (a "hull repair rate")
                          (a "delayed hull repair rate"))
                       (+ 1 (a "hull repair multiplier")))
        efficiency (cooling-efficiency (a "cooling inefficiency"))
        idle-energy (- (+ (a "energy generation")
                          (a "solar collection")
                          (a "fuel energy"))
                       (a "energy consumption")
                       (a "cooling energy"))
        idle-heat (- (+ (a "heat generation")
                        (a "solar heat")
                        (a "fuel heat"))
                     (* efficiency
                        (+ (a "cooling")
                           (a "active cooling"))))
        ;; the worst case of moving: thrusting (with afterburner) or reversing, plus turning
        thrusting-cost (fn [k]
                         (+ (a (str "thrusting " k))
                            (a (str "afterburner " k))))
        moving (fn [k]
                 (+ (a (str "turning " k))
                    (cond
                      (and (pos? main-thrust)
                           (pos? reverse-thrust)) (max (thrusting-cost k)
                                                       (a (str "reverse thrusting " k)))
                      (pos? main-thrust) (thrusting-cost k)
                      (pos? reverse-thrust) (a (str "reverse thrusting " k))
                      :else 0.0)))
        ;; energy or heat used by shield regeneration / hull repair (if the ship has it)
        regen-cost (fn [part regen k]
                     (if (pos? regen)
                       (* (+ (a (str part " " k))
                             (a (str "delayed " part " " k)))
                          (+ 1 (a (str part " " k " multiplier"))))
                       0.0))
        repairing (fn [k]
                    (+ (regen-cost "shield" shield-regen k)
                       (regen-cost "hull" hull-repair k)))
        repair-label (let [shield-energy (regen-cost "shield" shield-regen "energy")
                           hull-energy (regen-cost "hull" hull-repair "energy")]
                       (cond
                         (and (not (zero? shield-energy))
                              (not (zero? hull-energy))) "shields / hull"
                         (not (zero? hull-energy)) "repairing hull"
                         :else "charging shields"))
        max-heat (* maximum-temperature
                    (+ mass (a "heat capacity")))
        heat-dissipation (* 0.001 (a "heat dissipation"))
        ;; Ship::IdleHeat: the heat level where cooling and dissipation balance heat generation
        production (max 0
                        (- (a "heat generation")
                           (* efficiency (a "cooling"))))
        dissipation (+ heat-dissipation
                       (safe-divide (* efficiency (a "active cooling"))
                                    max-heat))
        overheating? (if (zero? dissipation)
                       (pos? production)
                       (>= (/ production dissipation)
                           max-heat))
        row (fn [label energy heat]
              {:label label
               :energy (* 60 energy)
               :heat (* 60 heat)})]
    {:shields (* (a "shields")
                 (+ 1 (a "shield multiplier")))
     :shield-regen (* 60 shield-regen)
     :hull (* (a "hull")
              (+ 1 (a "hull multiplier")))
     :hull-repair (* 60 hull-repair)
     :mass mass
     :cargo-space cargo-space
     :required-crew (+ (if (zero? (a "automaton"))
                         (max 1 (a "required crew"))
                         0)
                       (a "mandatory crew"))
     :bunks (a "bunks")
     :fuel-capacity (a "fuel capacity")
     :max-speed (speed main-thrust)
     :max-speed-afterburner (when both-thrusts?
                              (speed (+ thrust afterburner-thrust)))
     :acceleration (acceleration main-thrust)
     :acceleration-afterburner (when both-thrusts?
                                 (acceleration (+ thrust afterburner-thrust)))
     :turning (mass-range #(safe-divide (* 60
                                           (a "turn")
                                           (+ 1 (a "turn multiplier")))
                                        (/ % inertia-reduction))
                          mass
                          cargo-space)
     :energy-heat (concat [(row "idle" idle-energy idle-heat)
                           (row "thrusting"
                                (- (a "thrusting energy"))
                                (a "thrusting heat"))]
                          (when (pos? reverse-thrust)
                            [(row "reversing"
                                  (- (a "reverse thrusting energy"))
                                  (a "reverse thrusting heat"))])
                          (when (pos? afterburner-thrust)
                            [(row "afterburner"
                                  (- (a "afterburner energy"))
                                  (a "afterburner heat"))])
                          [(row "turning"
                                (- (a "turning energy"))
                                (a "turning heat"))
                           {:label "firing"
                            :energy (- (:energy firing))
                            :heat (:heat firing)}
                           (row repair-label
                                (- (repairing "energy"))
                                (repairing "heat"))])
     :net-change {:energy (- (* 60
                                (- idle-energy
                                   (moving "energy")
                                   (repairing "energy")))
                             (:energy firing))
                  :heat (+ (* 60
                              (+ idle-heat
                                 (moving "heat")
                                 (repairing "heat")))
                           (:heat firing))}
     :capacity {:energy (a "energy capacity")
                :heat (* 60 heat-dissipation max-heat)}
     :overheating? overheating?
     ;; Ship::FlightCheck "no energy!": generation and storage don't cover the idle consumption
     ;; (the game adds per-frame rates to the battery capacity, so it's only a rough check)
     :no-energy? (<= (+ (- (a "energy generation")
                           (a "energy consumption"))
                        (a "fuel energy")
                        (a "solar collection")
                        (a "energy capacity"))
                     0)}))

(defn configuration-stats
  "Returns ship-stats of a ship with the given outfits ({:name :quantity}). `attributes` are
  its precise attributes and `outfit` is a function returning an outfit by its name."
  [ship outfits attributes outfit]
  (let [total (fn [f]
                (->> outfits
                     (map (fn [{:keys [name quantity]}]
                            (* quantity
                               (or (f (outfit name))
                                   0))))
                     (reduce + 0)))]
    ;; mass is not an attribute in the game, outfits and hulls have it separately
    (assoc (ship-stats attributes
                       (+ (get ship :mass 0)
                          (total :mass))
                       {:energy (total #(get-in % [:weapon :firing-energy :per-second]))
                        :heat (total #(get-in % [:weapon :firing-heat :per-second]))})
           ;; damage of all weapons firing continuously (ignoring the ammo running out) by
           ;; type, without a combined number: shields are drained first, then hull
           :damage (->> [:shield-damage :hull-damage :heat-damage :ion-damage
                         :disruption-damage :slowing-damage]
                        (map (fn [damage-type]
                               [damage-type
                                (total #(get-in % [:weapon damage-type :per-second]))]))
                        (into {})))))
