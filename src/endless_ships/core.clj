(ns endless-ships.core
  (:require [camel-snake-kebab
             [core :refer [->camelCaseKeyword]]
             [extras :refer [transform-keys]]]
            [cheshire.core :refer [generate-string]]
            [endless-ships.parser :refer [data]]))

(def ships-data
  (->> data
       (filter #(= (count (:ship-name %)) 1)) ; remove modifications
       (map (fn [ship]
              (merge {:name (first (:ship-name ship))
                      :licenses (:licenses ship)
                      :race (:race ship)}
                     (-> ship :attributes (dissoc :weapon)))))
       (map #(transform-keys ->camelCaseKeyword %))))

(defn generate-json [& {:keys [pretty] :or {pretty true}}]
  (let [json (generate-string ships-data {:pretty pretty})]
    (spit "build/data.json" (str json "\n"))))
