(ns endless-ships.outfits
  (:require [endless-ships.parser :refer [->map data]]))

(def outfits
  (->> data
       (filter #(= (first %) "outfit"))
       (map (fn [[_
                  [name]
                  {description-attrs "description"
                   file "file"
                   :as attrs}]]
              (merge (->map attrs)
                     {:name name
                      :description (->> description-attrs
                                        (map #(get-in % [0 0]))
                                        vec)
                      :file file})))))

(comment
  ;; outfit counts by category
  (->> outfits
       (map :category)
       (reduce (fn [counts object]
                 (update counts object #(inc (or % 0))))
               {})
       (sort-by last >)))
