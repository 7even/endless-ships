(ns endless-ships.views.outfits
  (:require [re-frame.core :as rf]
            [endless-ships.views.utils :refer [license-label kebabize nbspize format-number]]
            [endless-ships.views.table :refer [table left-cell right-cell]]
            [endless-ships.utils.outfits :as utils]
            [endless-ships.subs :as subs]
            [endless-ships.routes :as routes]))

(defn outfits []
  [:div.app
   (->> utils/types
        (map (fn [[type type-attrs]]
               (let [rows (->> @(rf/subscribe [::subs/outfit-names type])
                               (map (fn [name]
                                      (let [outfit @(rf/subscribe [::subs/outfit name])]
                                        ^{:key name}
                                        [:tr
                                         [left-cell ^{:key name} [routes/outfit-link name]]
                                         [right-cell (format-number (:cost outfit))]
                                         (map-indexed (fn [idx {:keys [value]}]
                                                        ^{:key [(or (value outfit) 0) idx]}
                                                        [right-cell (format-number (value outfit))])
                                                      (-> type-attrs :columns vals))
                                         [left-cell (->> (:licenses outfit)
                                                         (map license-label)
                                                         (interpose " "))]])))
                               doall)
                     ordering @(rf/subscribe [::subs/outfits-ordering type])]
                 ^{:key type} [:div
                               [:h2 (:header type-attrs)]
                               [table type (utils/columns-for type) ordering rows]
                               (doall
                                (map-indexed (fn [idx item]
                                               ^{:key idx} [:p item])
                                             (:footer type-attrs)))])))
        doall)])
