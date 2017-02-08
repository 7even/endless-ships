(ns endless-ships.core
  (:require [camel-snake-kebab
             [core :refer [->camelCaseKeyword]]
             [extras :refer [transform-keys]]]
            [cheshire.core :refer [generate-string]]
            [endless-ships.parser :refer [data]]))

(def ships-data
  (->> data
       (filter #(= (count (:ship-name %)) 1)) ; remove modifications
       (map #(transform-keys ->camelCaseKeyword %))
       (map (fn [ship]
              (merge {:name (first (:shipName ship))
                      :licenses (:licenses ship)
                      :race (:race ship)}
                     (-> ship :attributes (select-keys [:cost :category :hull :shields :mass
                                                        :engineCapacity :weaponCapacity :fuelCapacity
                                                        :outfitSpace :cargoSpace
                                                        :requiredCrew :bunks])))))))

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
                      (->> ships-data (filter #(= (:race %) "quarg"))))
        json (generate-string ships {:pretty true})]
    (spit "frontend/public/data.json" (str json "\n")))
  ;; get a list of all possible attribute names
  (->> ships-data
       (map keys)
       (apply concat)
       (into #{})))
