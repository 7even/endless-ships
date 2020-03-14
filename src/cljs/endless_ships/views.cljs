(ns endless-ships.views
  (:require [re-frame.core :as rf]
            ["dayjs" :as dayjs]
            [endless-ships.subs :as subs]
            [endless-ships.views.navigation :refer [navigation]]
            [endless-ships.views.ships-list :refer [ships-list]]
            [endless-ships.views.ship-page :refer [ship-page]]
            [endless-ships.views.outfits :refer [outfits]]
            [endless-ships.views.outfit-page :refer [outfit-page]]))

(defn current-page []
  (let [[route params] @(rf/subscribe [::subs/route])]
    (case route
      :ships [ships-list]
      :ship [ship-page (:ship/name params) nil]
      :ship-modification [ship-page (:ship/name params) (:ship/modification params)]
      :outfits [outfits]
      :outfit [outfit-page (:outfit/name params)]
      [:div (str "Route unknown: " route)])))

(defn game-version []
  (let [{:keys [hash date]} @(rf/subscribe [::subs/game-version])]
    [:div.game-version
     [:a {:href (str "https://github.com/endless-sky/endless-sky/commit/" hash)
          :target :blank}
      "endless-sky@" (subs hash 0 7)]
     " ("
     (.format (dayjs date) "YYYY-MM-DD")
     ")"]))

(defn interface []
  [:div.container
   [:div.row
    [:div.col-lg-12
     (if @(rf/subscribe [::subs/loading?])
       [:div.app "Loading..."]
       (if @(rf/subscribe [::subs/loading-failed?])
         [:div.app "Failed to load data"]
         [:div.app
          [game-version]
          [navigation]
          [current-page]]))]]])
