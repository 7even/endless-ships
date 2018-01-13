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
       (filter #(and (= (first %) "ship")
                     (= (-> % second count) 1)
                     (not= (second %) ["Unknown Ship Type"])))
       (map (fn [[_
                  [ship-name]
                  {[[[sprite]]] "sprite"
                   [[_ {[[_ license-attrs]] "licenses"
                        [[_ weapon-attrs]] "weapon"
                        :as attrs}]] "attributes"
                   [[_ outfit-attrs]] "outfits"
                   description-attrs "description"
                   file "file"}]]
              (merge (->map attrs)
                     {:name ship-name
                      :sprite sprite
                      :licenses (-> license-attrs keys vec)
                      :weapon (->map weapon-attrs)
                      :outfits (reduce (fn [outfit-quantities [outfit-name [[[quantity]]]]]
                                         (assoc outfit-quantities
                                                outfit-name
                                                (or quantity 1)))
                                       {}
                                       outfit-attrs)
                      :description (->> description-attrs
                                        (map #(get-in % [0 0]))
                                        vec)
                      :file file})))))
