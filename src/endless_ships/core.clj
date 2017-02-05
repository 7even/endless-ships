(ns endless-ships.core
  (:require [endless-ships.parser :refer [data]]
            [cheshire.core :refer [generate-string]]))

(def ships-data
  (->> data
       (filter #(= (count (:ship-name %)) 1)) ; remove modifications
       (map (fn [ship]
              (merge {:ship-name (first (:ship-name ship))
                      :licenses (:licenses ship)
                      :race (:race ship)}
                     (dissoc (:attributes ship) :weapon))))))

(defn generate-json [& {:keys [pretty] :or {pretty true}}]
  (let [json (generate-string ships-data {:pretty pretty})]
    (spit "build/data.json" (str json "\n"))))
