(ns endless-ships.core
  (:require [camel-snake-kebab
             [core :refer [->camelCaseKeyword]]
             [extras :refer [transform-keys]]]
            [cheshire.core :refer [generate-string]]
            [endless-ships
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
                 (select-keys [:name :licenses :file
                               :cost :category :hull :shields :mass
                               :engine-capacity :weapon-capacity :fuel-capacity
                               :outfit-space :cargo-space
                               :required-crew :bunks])
                 (assoc :race (get file->race (:file %) :other))
                 (dissoc :file)))
       (map #(transform-keys ->camelCaseKeyword %))))

(defn generate-json [& {:keys [pretty] :or {pretty true}}]
  (let [json (generate-string ships-data {:pretty pretty})]
    (spit "build/data.json" (str json "\n"))))

(comment
  ;; generate sample data for frontend development
  (let [ships (concat (->> ships-data (filter #(= (:race %) "human")) (take 3))
                      (->> ships-data (filter #(= (:race %) "hai")) (take 2))
                      (->> ships-data (filter #(= (:race %) "korath")) (take 2))
                      (->> ships-data (filter #(= (:race %) "wanderer")) (take 2))
                      (->> ships-data (filter #(= (:race %) "coalition")) (take 2))
                      (->> ships-data (filter #(= (:race %) "pug")) (take 2))
                      (->> ships-data (filter #(= (:race %) "quarg"))))
        json (generate-string ships {:pretty true})]
    (spit "frontend/public/data.json" (str json "\n")))
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
       (sort-by last >)))
