(ns endless-ships.views.ship-page
  (:require [re-frame.core :as rf]
            [endless-ships.subs :as subs]))

(defn ship-page [ship-name ship-modification]
  (let [ship @(rf/subscribe [::subs/ship ship-name])]
    [:div.app
     [:div.row
      [:div.col-md-6
       [:div.panel.panel-default
        [:div.panel-heading (:name ship)]
        [:div.panel-body "Ship details"]]]]]))
