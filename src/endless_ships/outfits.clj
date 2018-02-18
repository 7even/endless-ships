(ns endless-ships.outfits
  (:require [endless-ships.parser :refer [->map data]]))

(defn- update-if-present [m k f]
  (if (contains? m k)
    (update m k f)
    m))

(defn- round-to-int [num]
  (-> num double Math/round))

(def outfits
  (->> data
       (filter #(= (first %) "outfit"))
       (map (fn [[_
                  [name]
                  {description-attrs "description"
                   license-attrs "licenses"
                   weapon-attrs "weapon"
                   file "file"
                   :as attrs}]]
              (merge (->map attrs)
                     {:name name
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
       (map (fn [outfit]
              (-> outfit
                  (update-if-present :outfit-space -)
                  (update-if-present :thrust #(round-to-int (* % 3600)))
                  (update-if-present :reverse-thrust #(round-to-int (* % 3600)))
                  (update-if-present :afterburner-thrust #(round-to-int (* % 3600)))
                  (update-if-present :turn #(round-to-int (* % 60)))
                  (update-if-present :thrusting-energy #(round-to-int (* % 60)))
                  (update-if-present :thrusting-heat #(round-to-int (* % 60)))
                  (update-if-present :reverse-thrusting-energy #(round-to-int (* % 60)))
                  (update-if-present :reverse-thrusting-heat #(round-to-int (* % 60)))
                  (update-if-present :turning-energy #(round-to-int (* % 60)))
                  (update-if-present :turning-heat #(round-to-int (* % 60)))
                  (update-if-present :afterburner-fuel #(round-to-int (* % 60)))
                  (update-if-present :afterburner-heat #(round-to-int (* % 60)))
                  (update-if-present :energy-generation #(round-to-int (* % 60)))
                  (update-if-present :heat-generation #(round-to-int (* % 60)))
                  (update-if-present :cooling #(round-to-int (* % 60)))
                  (update-if-present :active-cooling #(round-to-int (* % 60)))
                  (update-if-present :cooling-energy #(round-to-int (* % 60))))))))

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
       (filter #(some? (:energy-capacity %)))
       (map keys)
       (reduce (fn [counts outfit-attributes]
                 (merge-with +
                             counts
                             (reduce (fn [attribute-counts attr-name]
                                       (update attribute-counts attr-name #(inc (or % 0))))
                                     {}
                                     outfit-attributes)))
               {})
       (sort-by last >)))
