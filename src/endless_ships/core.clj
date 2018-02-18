(ns endless-ships.core
  (:require [camel-snake-kebab
             [core :refer [->camelCaseKeyword]]
             [extras :refer [transform-keys]]]
            [cheshire.core :refer [generate-string]]
            [endless-ships
             [outfits :refer [outfits]]
             [parser :refer [data]]
             [ships :refer [ships]]]))

(def file->race
  {"kestrel.txt" :human
   "hai ships.txt" :hai
   "pug.txt" :pug
   "wanderer ships.txt" :wanderer
   "quarg ships.txt" :quarg
   "remnant ships.txt" :remnant
   "korath ships.txt" :korath
   "marauders.txt" :pirate
   "coalition ships.txt" :coalition
   "drak.txt" :drak
   "ships.txt" :human})

(def ships-data
  (->> ships
       (filter #(some? (file->race (:file %))))
       (map #(-> %
                 (select-keys [:name :sprite :licenses :file
                               :cost :category :hull :shields :mass
                               :engine-capacity :weapon-capacity :fuel-capacity
                               :outfit-space :cargo-space
                               :required-crew :bunks :description
                               :guns :turrets :drones :fighters])
                 (assoc :outfits (->> (:outfits %)
                                      (map (fn [[name quantity]]
                                             {:name name
                                              :quantity quantity}))
                                      vec))
                 (assoc :race (get file->race (:file %) :other))
                 (dissoc :file)))
       (map #(transform-keys ->camelCaseKeyword %))))

(def outfits-data
  (->> outfits
       (remove #(= (:file %) "deprecated outfits.txt"))
       (map #(dissoc % :file))
       (map #(transform-keys ->camelCaseKeyword %))))

(defn generate-json
  ([]
   (generate-json "build/data.json"))
  ([path]
   (let [data {:ships ships-data
               :outfits outfits-data}
         json (generate-string data {:pretty true})]
     (spit path (str json "\n")))))

(comment
  ;; generate data for frontend development
  (generate-json "frontend/public/data.json")
  ;; get a list of all possible attribute names
  (->> ships-data
       (map keys)
       (apply concat)
       (into #{}))
  ;; get ship counts by race
  (->> ships-data
       (map :race)
       (reduce (fn [counts object]
                 (update counts object #(inc (or % 0))))
               {})
       (sort-by last >))
  ;; get government colors in CSS format
  (->> data
       (filter #(= (first %) "government"))
       (map (fn [[_ [name] {[[colors]] "color"}]]
              [name colors]))
       (filter #(some? (second %)))
       (map (fn [[government colors]]
              [government (->> colors
                               (map (partial * 255))
                               (map int)
                               (map (partial format "%02x"))
                               clojure.string/join
                               (str "#"))]))
       (into {})))
