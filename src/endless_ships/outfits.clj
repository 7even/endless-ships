(ns endless-ships.outfits
  (:require [endless-ships.parser :refer [->map data]]))

(defn- update-if-present [m k f]
  (if (contains? m k)
    (update m k f)
    m))

(defn- round-to-int [num]
  (when (some? num)
    (-> num double Math/round)))

(def attribute-convertors
  (let [times-3600 (comp round-to-int (partial * 3600))
        times-60 (comp round-to-int (partial * 60))]
    {:outfit-space -
     :weapon-capacity -
     :engine-capacity -
     ;; thrusters
     :thrust times-3600
     :thrusting-energy times-60
     :thrusting-heat times-60
     ;; steerings
     :turn times-60
     :turning-energy times-60
     :turning-heat times-60
     ;; reverse thrusters
     :reverse-thrust times-3600
     :reverse-thrusting-energy times-60
     :reverse-thrusting-heat times-60
     ;; afterburners
     :afterburner-thrust times-3600
     :afterburner-energy times-60
     :afterburner-fuel times-60
     :afterburner-heat times-60
     ;; reactors & solar collectors
     :energy-generation times-60
     :heat-generation times-60
     :solar-collection times-60
     :energy-consumption times-60
     ;; coolers
     :cooling times-60
     :active-cooling times-60
     :cooling-energy times-60
     ;; shield generators
     :shield-generation times-60
     :shield-energy times-60
     :shield-heat times-60
     ;; hull repair modules
     :hull-repair-rate times-60
     :hull-energy times-60
     :hull-heat times-60}))

(def weapon-attribute-convertors
  (let [times-60 (comp round-to-int (partial * 60))
        times-100 (comp round-to-int (partial * 100))]
    {:ion-damage times-100
     :slowing-damage times-100
     :disruption-damage times-100
     :turret-turn times-60}))

(defn- calculate-damage [weapon-attrs submunition submunition-count damage-type]
  (let [per-shot (if (some? submunition)
                   (+ (get weapon-attrs damage-type 0)
                      (* (get-in submunition [:weapon damage-type] 0)
                         (or submunition-count 1)))
                   (get weapon-attrs damage-type))
        per-second (when (and (some? per-shot)
                              (not= per-shot 0))
                     (/ (* per-shot 60) (:reload weapon-attrs)))]
    (when (some? per-second)
      (merge {:per-second (round-to-int per-second)}
             (if (> (:reload weapon-attrs) 1)
               {:per-shot (round-to-int per-shot)}
               {})))))

(defn- normalize-weapon-attrs [outfits]
  (map
   (fn [{category :category
         {:keys [reload velocity lifetime shield-damage hull-damage]
          [submunition-name submunition-count] :submunition
          :as weapon-attrs} :weapon
         :as outfit}]
     (if (#{"Guns" "Secondary Weapons" "Turrets"} category)
       (let [shots-per-second (if (= reload 1)
                                "continuous"
                                (->> reload
                                     (/ 60M)
                                     (with-precision 5)
                                     float))
             submunition (when (some? submunition-name)
                           (first (filter #(= (:name %) submunition-name) outfits)))
             range (if (some? submunition)
                     (let [total-lifetime (+ lifetime (get-in submunition [:weapon :lifetime]))]
                       (* velocity total-lifetime))
                     (* velocity lifetime))
             converted-weapon-attrs (reduce (fn [attrs [attr-name convertor]]
                                              (update-if-present attrs attr-name convertor))
                                            weapon-attrs
                                            weapon-attribute-convertors)]
         (assoc outfit
                :weapon
                (merge converted-weapon-attrs
                       {:shots-per-second shots-per-second
                        :range range}
                       (reduce (fn [damage-attrs damage-type]
                                 (if-let [attr-value (calculate-damage converted-weapon-attrs
                                                                       submunition
                                                                       submunition-count
                                                                       damage-type)]
                                   (assoc damage-attrs damage-type attr-value)
                                   damage-attrs))
                               {}
                               [:shield-damage :hull-damage :heat-damage
                                :ion-damage :disruption-damage :slowing-damage
                                :firing-energy :firing-heat :firing-fuel]))))
       (dissoc outfit :weapon)))
   outfits))

(def outfits
  (->> data
       (filter #(= (first %) "outfit"))
       (map (fn [[_
                  [name]
                  {description-attrs "description"
                   license-attrs "licenses"
                   weapon-attrs "weapon"
                   [[[category]]] "category"
                   file "file"
                   :as attrs}]]
              (merge (->map attrs)
                     {:name name
                      :category category
                      :licenses (->> license-attrs
                                     (map #(-> % (get 1) keys))
                                     (apply concat)
                                     vec)
                      :weapon (-> weapon-attrs
                                  (get-in [0 1])
                                  ->map
                                  (assoc :submunition (get-in weapon-attrs [0 1 "submunition" 0 0])))
                      :description (->> description-attrs
                                        (map #(get-in % [0 0]))
                                        vec)
                      :file file})))
       normalize-weapon-attrs
       (map (fn [outfit]
              (reduce (fn [attrs [attr-name convertor]]
                        (update-if-present attrs attr-name convertor))
                      outfit
                      attribute-convertors)))))

(comment
  ;; outfit counts by category
  (->> outfits
       (map :category)
       (reduce (fn [counts object]
                 (update counts object #(inc (or % 0))))
               {})
       (sort-by last >))
  ;; attribute counts among outfits having a given attribute
  (->> outfits
       (filter #(= (:category %) "Turrets"))
       (map :weapon)
       (map keys)
       (reduce (fn [counts outfit-attributes]
                 (merge-with +
                             counts
                             (reduce (fn [attribute-counts attr-name]
                                       (update attribute-counts attr-name #(inc (or % 0))))
                                     {}
                                     outfit-attributes)))
               {})
       (sort-by last >))
  ;; some computed attributes of guns
  (->> outfits
       (filter #(= (:category %) "Guns"))
       (map (fn [{name :name
                  {:keys [reload submunition velocity lifetime]} :weapon}]
              {:name name
               :shots-per-second (if (= reload 1)
                                   "continuous"
                                   (float (/ 60 reload)))
               :range (if (some? submunition)
                        (let [submunition-outfit (first (filter #(= (:name %) submunition) outfits))
                              total-lifetime (+ lifetime (get-in submunition-outfit [:weapon :lifetime]))]
                          (* velocity total-lifetime))
                        (* velocity lifetime))}))
       (sort-by :name)))
