(ns endless-ships.outfits
  (:require [endless-ships.parser :refer [->map data]]))

(defn- update-if-present [m k f]
  (if (contains? m k)
    (update m k f)
    m))

(defn- round-to-int [num]
  (-> num double Math/round))

(def attribute-convertors
  (let [times-3600 (comp round-to-int (partial * 3600))
        times-60 (comp round-to-int (partial * 60))]
    {:outfit-space -
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
     :afterburner-fuel times-60
     :afterburner-heat times-60
     ;; reactors & solar collectors
     :energy-generation times-60
     :heat-generation times-60
     :solar-collection times-60
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

(defn- normalize-weapon-attrs [outfits]
  (map
   (fn [{category :category
         {:keys [reload submunition velocity lifetime] :as weapon-attrs} :weapon
         :as outfit}]
     (if (#{"Guns" "Secondary Weapons" "Turrets"} category)
       (let [shots-per-second (if (= reload 1)
                                "continuous"
                                (->> reload
                                     (/ 60M)
                                     (with-precision 5)
                                     float))
             range (if (some? submunition)
                     (let [submunition-outfit (first (filter #(= (:name %) submunition) outfits))
                           total-lifetime (+ lifetime (get-in submunition-outfit [:weapon :lifetime]))]
                       (* velocity total-lifetime))
                     (* velocity lifetime))]
         (assoc outfit
                :weapon
                (-> weapon-attrs
                    (assoc :shots-per-second shots-per-second)
                    (assoc :range range))))
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
                                  ->map)
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
