(ns endless-ships.views
  (:require [re-frame.core :as rf]
            [endless-ships.subs :as subs]
            [endless-ships.views.navigation :refer [navigation]]
            [endless-ships.views.ships-list :refer [ships-list]]
            [endless-ships.views.ship-page :refer [ship-page]]
            [endless-ships.views.outfits :refer [outfits]]))

(defn current-page []
  (let [[route params] @(rf/subscribe [::subs/route])]
    (case route
      :ships [ships-list]
      :ship [ship-page (:ship/name params) nil]
      :ship-modification [ship-page (:ship/name params) (:ship/modification params)]
      :outfits [outfits]
      [:div (str "Route unknown: " route)])))

(defn interface []
  [:div.container
   [:div.row
    [:div.col-lg-12
     (if @(rf/subscribe [::subs/loading?])
       [:div.app "Loading..."]
       (if @(rf/subscribe [::subs/loading-failed?])
         [:div.app "Failed to load data"]
         [:div.app
          [navigation]
          [current-page]]))]]])
