(ns endless-ships.outfitters
  (:require [endless-ships.parser :refer [->map data]]))

(def outfitters
  (->> data
       (filter #(= (first %) "outfitter"))
       (map (fn [[_ [name] outfits]]
              {:name name
               :outfits (->> outfits
                             keys
                             (remove #(= % "file"))
                             vec)}))))
