(ns endless-ships.core
  (:require [camel-snake-kebab.core :refer [->camelCaseKeyword]]
            [camel-snake-kebab.extras :refer [transform-keys]]
            [cheshire.core :refer [generate-string]]
            [clojure.set :refer [rename-keys]]
            [endless-ships.outfits :refer [outfits]]
            [endless-ships.outfitters :refer [outfitters]]
            [endless-ships.ships :refer [modifications ships]]))

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
   "ships.txt" :human
   "indigenous.txt" :indigenous})

(def outfits-data
  (->> outfits
       (remove #(#{"deprecated outfits.txt"
                   "nanobots.txt"
                   "transport missions.txt"} (:file %)))
       (map #(dissoc % :file))
       (map #(transform-keys ->camelCaseKeyword %))))

(defn- assoc-outfits-cost [ship]
  (let [outfits (:outfits ship)]
    (if (empty? outfits)
      ship
      (assoc ship
             :outfits-cost
             (reduce (fn [cost {:keys [name quantity]}]
                       (let [outfit (->> outfits-data
                                         (filter #(= (:name %) name))
                                         first)]
                         (+ cost
                            (* (get outfit :cost 0)
                               quantity))))
                     0
                     outfits)))))

(def ships-data
  (->> ships
       (filter #(some? (file->race (:file %))))
       (map #(-> %
                 (select-keys [:name :sprite :licenses :file
                               :cost :category :hull :shields :mass
                               :engine-capacity :weapon-capacity :fuel-capacity
                               :outfits :outfit-space :cargo-space
                               :required-crew :bunks :description
                               :guns :turrets :drones :fighters
                               :self-destruct :ramscoop])
                 (assoc :race (get file->race (:file %) :other))
                 (dissoc :file)
                 (rename-keys {:cost :empty-hull-cost})
                 assoc-outfits-cost))
       (map #(transform-keys ->camelCaseKeyword %))))

(def modifications-data
  (->> modifications
       (map #(-> %
                 (dissoc :file)
                 (rename-keys {:cost :empty-hull-cost})
                 assoc-outfits-cost))
       (map #(transform-keys ->camelCaseKeyword %))))

(defn generate-json
  ([]
   (generate-json "build/data.json"))
  ([path]
   (let [data {:ships ships-data
               :shipModifications modifications-data
               :outfits outfits-data
               :outfitters outfitters}
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
  (->> endless-ships.parser/data
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
