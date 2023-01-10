(ns endless-ships.utils.outfits)

(defn- damage [damage-type gun]
  (get-in gun [:weapon damage-type :per-second]))

(def ^:private missile-strength-for-am
  16)

(defn- anti-missile-effectiveness
  "Chances to destroy a missile are determined with the following algorithm:
  (> (rand-int anti-missile-strength)
     (rand-int missile-strength))

  If the first number is higher, the missile will be destroyed.

  If anti-missile strength is 5 and missile strength is 3, then all possible combinations
  can be represented as a matrix (where \"+\" means a hit and \"-\" means a miss):

  |---+---+---+---+---+---|
  |   | 0 | 1 | 2 | 3 | 4 |
  |---+---+---+---+---+---|
  | 0 | - | + | + | + | + |
  | 1 | - | - | + | + | + |
  | 2 | - | - | - | + | + |
  |---+---+---+---+---+---|

  Then the square part of the matrix contains equal parts of hits and misses,
  except for the diagonal which goes to misses (since anti-missile random number must be
  strictly bigger than the missile random number).

  The remaining rectangle part to the right only contains hits (it's only present when
  anti-missile strength is higher than missile strength).

  Total percentage of hits can be calculated by adding hits from the square and hits
  from the rectangle, then dividing the result by total square of the matrix.

  Multiplying this percentage by the rate of fire gives us an average number of missiles destroyed
  per second.

  Note: \"continuous\" rate of fire means \"firing at every frame of the game\", so the actual
  rate of fire equals the framerate which is 60 fps."
  [{{a :anti-missile
     rate :shots-per-second} :weapon}]
  (let [m missile-strength-for-am
        square-side (min a m)
        square (Math/pow square-side 2)
        half-of-diagonal (/ square-side 2)
        remainder (max 0
                       (* (- a m) m))
        total (* a m)]
    (* (/ (+ (- (/ square 2) half-of-diagonal)
             remainder)
          total)
       (if (= rate "continuous")
         60
         rate))))

(def types
  (array-map :thrusters {:header "Thrusters"
                         :filter #(contains? % :thrust)
                         :initial-ordering {:column-name "Thrust per space"
                                            :order :desc}
                         :columns (array-map "Outfit sp."       {:value :outfit-space}
                                             "Thrust"           {:value :thrust}
                                             "Thrust per space" {:value #(/ (:thrust %)
                                                                            (:outfit-space %))}
                                             "Thr. energy"      {:value :thrusting-energy}
                                             "Thr. heat"        {:value :thrusting-heat})}
             :steerings {:header "Steerings"
                         :filter #(contains? % :turn)
                         :initial-ordering {:column-name "Turn per space"
                                            :order :desc}
                         :columns (array-map "Outfit sp."     {:value :outfit-space}
                                             "Turn"           {:value :turn}
                                             "Turn per space" {:value #(/ (:turn %)
                                                                          (:outfit-space %))}
                                             "Turn. energy"   {:value :turning-energy}
                                             "Turn. heat"     {:value :turning-heat})}
             :reversers {:header "Reverse Thrusters"
                         :filter #(contains? % :reverse-thrust)
                         :initial-ordering {:column-name "Reverse thr. per space"
                                            :order :desc}
                         :columns (array-map "Outfit sp."             {:value :outfit-space}
                                             "Reverse thrust"         {:value :reverse-thrust}
                                             "Reverse thr. per space" {:value #(/ (:reverse-thrust %)
                                                                                  (:outfit-space %))}
                                             "Reverse thr. energy"    {:value :reverse-thrusting-energy}
                                             "Reverse thr. heat"      {:value :reverse-thrusting-heat})}
             :afterburners {:header "Afterburners"
                            :filter #(contains? % :afterburner-thrust)
                            :initial-ordering {:column-name "Thrust per space"
                                               :order :desc}
                            :columns (array-map "Outfit sp."       {:value :outfit-space}
                                                "Thrust"           {:value :afterburner-thrust}
                                                "Thrust per space" {:value #(/ (:afterburner-thrust %)
                                                                               (:outfit-space %))}
                                                "Thr. fuel"        {:value :afterburner-fuel}
                                                "Thrust per fuel"  {:value #(/ (:afterburner-thrust %)
                                                                               (:afterburner-fuel %))}
                                                "Thr. energy"      {:value :afterburner-energy}
                                                "Thr. heat"        {:value :afterburner-heat})}
             :reactors {:header "Reactors"
                        :filter #(and (contains? % :energy-generation)
                                      (= (:category %) "Power"))
                        :initial-ordering {:column-name "Energy per space"
                                           :order :desc}
                        :columns (let [energy-generation #(+ (get % :energy-generation 0)
                                                             (get % :solar-collection 0))]
                                   (array-map "Outfit sp."        {:value :outfit-space}
                                              "Energy generation" {:value energy-generation}
                                              "Energy per space"  {:value #(/ (energy-generation %)
                                                                              (:outfit-space %))}
                                              "Heat generation"   {:value :heat-generation}))}
             :solar-panels {:header "Solar Panels"
                            :filter #(contains? % :solar-collection)
                            :initial-ordering {:column-name "Solar collection per space"
                                               :order :desc}
                            :columns (array-map "Outfit sp."                 {:value :outfit-space}
                                                "Solar collection"           {:value :solar-collection}
                                                "Solar collection per space" {:value #(/ (:solar-collection %)
                                                                                         (:outfit-space %))}
                                                "Solar heat"                 {:value :solar-heat})}
             :batteries {:header "Batteries"
                         :filter #(and (contains? % :energy-capacity)
                                       (#{"Power" "Systems"} (:category %)))
                         :initial-ordering {:column-name "Energy per space"
                                            :order :desc}
                         :columns (array-map "Outfit sp."       {:value :outfit-space}
                                             "Energy capacity"  {:value :energy-capacity}
                                             "Energy per space" {:value #(/ (:energy-capacity %)
                                                                            (:outfit-space %))})}
             :coolers {:header "Coolers"
                       :filter #(or (contains? % :cooling)
                                    (contains? % :active-cooling))
                       :initial-ordering {:column-name "Cooling per space"
                                          :order :desc}
                       :columns (let [total-cooling #(+ (get % :cooling 0)
                                                        (get % :active-cooling 0))]
                                  (array-map "Outfit sp."        {:value :outfit-space}
                                             "Cooling"           {:value total-cooling}
                                             "Cooling per space" {:value #(/ (total-cooling %)
                                                                             (:outfit-space %))}
                                             "Cooling energy"    {:value :cooling-energy}))}
             :shields {:header "Shield generators"
                       :filter #(contains? % :shield-generation)
                       :initial-ordering {:column-name "Shield per space"
                                          :order :desc}
                       :columns (array-map "Outfit sp."        {:value :outfit-space}
                                           "Shield generation" {:value :shield-generation}
                                           "Shield per space"  {:value #(/ (:shield-generation %)
                                                                           (:outfit-space %))}
                                           "Shield energy"     {:value :shield-energy}
                                           "Shield heat"       {:value :shield-heat})}
             :hull-repair {:header "Hull repair modules"
                           :filter #(contains? % :hull-repair-rate)
                           :initial-ordering {:column-name "Hull per space"
                                              :order :desc}
                           :columns (array-map "Outfit sp."       {:value :outfit-space}
                                               "Hull repair rate" {:value :hull-repair-rate}
                                               "Hull per space"   {:value #(/ (:hull-repair-rate %)
                                                                              (:outfit-space %))}
                                               "Hull energy"      {:value :hull-energy}
                                               "Hull heat"        {:value :hull-heat})}
             :outfit-scanners {:header "Outfit Scanners"
                               :filter #(contains? % :outfit-scan-power)
                               :initial-ordering {:column-name "Outfit scan power"
                                                  :order :desc}
                               :columns (array-map "Outfit sp."        {:value :outfit-space}
                                                   "Outfit scan power" {:value :outfit-scan-power}
                                                   "Outfit scan speed" {:value :outfit-scan-speed})}
             :cargo-scanners {:header "Cargo Scanners"
                              :filter #(contains? % :cargo-scan-power)
                              :initial-ordering {:column-name "Cargo scan power"
                                                 :order :desc}
                              :columns (array-map "Outfit sp."       {:value :outfit-space}
                                                  "Cargo scan power" {:value :cargo-scan-power}
                                                  "Cargo scan speed" {:value :cargo-scan-speed})}
             :tactical-scanners {:header "Tactical Scanners"
                                 :filter #(contains? % :tactical-scan-power)
                                 :initial-ordering {:column-name "Tactical scan power"
                                                    :order :desc}
                                 :columns (array-map "Outfit sp."          {:value :outfit-space}
                                                     "Tactical scan power" {:value :tactical-scan-power})}
             :ramscoops {:header "Ramscoops"
                         :filter #(contains? % :ramscoop)
                         :initial-ordering {:column-name "Ramscoop per space"
                                            :order :desc}
                         :columns (array-map "Outfit sp."         {:value :outfit-space}
                                             "Ramscoop"           {:value :ramscoop}
                                             "Ramscoop per space" {:value #(/ (:ramscoop %)
                                                                              (:outfit-space %))})}
             :fuel-tanks {:header "Fuel tanks"
                          :filter #(and (contains? % :fuel-capacity)
                                        (= (:category %) "Systems"))
                          :initial-ordering {:column-name "Fuel cap. per space"
                                             :order :desc}
                          :columns (array-map "Outfit sp."          {:value :outfit-space}
                                              "Fuel capacity"       {:value :fuel-capacity}
                                              "Fuel cap. per space" {:value #(/ (:fuel-capacity %)
                                                                                (:outfit-space %))})}
             :guns {:header "Guns"
                    :filter #(= (:category %) "Guns")
                    :initial-ordering {:column-name "Shield damage / space"
                                       :order :desc}
                    :columns (array-map "Outfit sp."            {:value :outfit-space}
                                        "Shield damage"         {:value (partial damage :shield-damage)}
                                        "Shield damage / space" {:value #(/ (damage :shield-damage %)
                                                                            (:outfit-space %))}
                                        "Hull damage"           {:value (partial damage :hull-damage)}
                                        "Hull damage / space"   {:value #(/ (damage :hull-damage %)
                                                                            (:outfit-space %))}
                                        "Range"                 {:value #(get-in % [:weapon :range])}
                                        "Fire rate"             {:value #(get-in % [:weapon :shots-per-second])
                                                                 :orderable? false})}
             :secondary {:header "Secondary weapons"
                         :filter #(= (:category %) "Secondary Weapons")
                         :initial-ordering {:column-name "Shield damage / space"
                                            :order :desc}
                         :columns (array-map "Outfit sp."            {:value :outfit-space}
                                             "Shield damage"         {:value (partial damage :shield-damage)}
                                             "Shield damage / space" {:value #(/ (damage :shield-damage %)
                                                                                 (:outfit-space %))}
                                             "Hull damage"           {:value (partial damage :hull-damage)}
                                             "Hull damage / space"   {:value #(/ (damage :hull-damage %)
                                                                                 (:outfit-space %))}
                                             "* Missile Strength"    {:value #(get-in % [:weapon :missile-strength])}
                                             "Range"                 {:value #(get-in % [:weapon :range])}
                                             "Fire rate"             {:value #(get-in % [:weapon :shots-per-second])
                                                                      :orderable? false})
                         :footer ["* Missile strength: how hard a projectile is for an anti-missile to destroy. If this is 0, the projectile cannot be destroyed by anti-missile."]}
             :turrets {:header "Turrets"
                       :filter #(and (= (:category %) "Turrets")
                                     (or (some? (damage :shield-damage %))
                                         (some? (damage :hull-damage %))))
                       :initial-ordering {:column-name "Shield damage / space"
                                          :order :desc}
                       :columns (array-map "Outfit sp."            {:value :outfit-space}
                                           "Shield damage"         {:value (partial damage :shield-damage)}
                                           "Shield damage / space" {:value #(/ (damage :shield-damage %)
                                                                               (:outfit-space %))}
                                           "Hull damage"           {:value (partial damage :hull-damage)}
                                           "Hull damage / space"   {:value #(/ (damage :hull-damage %)
                                                                               (:outfit-space %))}
                                           "Range"                 {:value #(get-in % [:weapon :range])}
                                           "Fire rate"             {:value #(get-in % [:weapon :shots-per-second])
                                                                    :orderable? false})}
             :anti-missile {:header "Anti-missile turrets"
                            :filter #(-> % :weapon (contains? :anti-missile))
                            :initial-ordering {:column-name "Effectiveness *"
                                               :order :desc}
                            :columns (array-map "Outfit sp."        {:value :outfit-space}
                                                "Anti-missile *"    {:value #(get-in % [:weapon :anti-missile])}
                                                "Effectiveness *"   {:value anti-missile-effectiveness}
                                                "Range"             {:value #(get-in % [:weapon :range])}
                                                "Fire rate"         {:value #(get-in % [:weapon :shots-per-second])
                                                                     :orderable? false})
                            :footer ["* Anti-missile: weapon's ability to shoot down missiles. The anti-missile succeeds if a random integer less than this value is greater than a random integer less than the missile's strength."
                                     "* Effectiveness: average number of missiles destroyed per second. Note: this number is calculated for cases when the turret is used against missiles with missile strength = 16 (e.g. Heavy Rockets); it may be drastically different against missiles with different strength, so this is not an absolute metric of turret's effectiveness."]}
             :hand-to-hand {:header "Hand to Hand"
                            :filter #(= (:category %) "Hand to Hand")
                            :initial-ordering {:column-name "Capture attack"
                                               :order :desc}
                            :columns (array-map "Capture attack"  {:value :capture-attack}
                                                "Capture defense" {:value :capture-defense}
                                                "Illegal"         {:value :illegal})}))

(defn columns-for [type]
  (let [custom-columns (get-in types [type :columns])
        all-columns (concat [["Name" {:value :name}]
                             ["Cost" {:value :cost}]]
                            custom-columns
                            [["Licenses" {:value :licenses
                                          :orderable? false}]])]
    (->> all-columns
         (apply concat)
         (apply array-map))))
