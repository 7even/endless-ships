(ns endless-ships.ships
  (:require [camel-snake-kebab
             [core :refer [->kebab-case-keyword]]
             [extras :refer [transform-keys]]]
            [endless-ships.parser :refer [data]]))

(defn- ->map [m]
  (reduce (fn [data [attr-name attr-value]]
            (assoc data
                   (->kebab-case-keyword attr-name)
                   (get-in attr-value [0 0 0])))
          {}
          m))

(def ships
  (->> data
       (filter #(= (-> % second count) 1))
       (map #(let [attrs (get-in % [2 "attributes" 0 1])
                   licenses (-> attrs
                                (get-in ["licenses" 0 1])
                                keys
                                vec)
                   weapon-attrs (get-in attrs ["weapon" 0 1])]
               (merge (->map attrs)
                      {:name (get-in % [1 0])
                       :licenses licenses
                       :weapon (->map weapon-attrs)})))))
