(ns endless-ships.configurator
  "Checks whether outfits fit into a ship the same way the game does
  (see Outfit::CanAdd in the game sources).")

(def ^:private attribute-precision
  "The game stores attributes as integers multiplied by this number."
  10000)

(defn- precise [value]
  (long (* value attribute-precision)))

(defn- from-precise [value]
  (/ value
     (double attribute-precision)))

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
