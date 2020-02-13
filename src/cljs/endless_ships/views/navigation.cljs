(ns endless-ships.views.navigation
  (:require [re-frame.core :as rf]
            [endless-ships.events :as events]
            [endless-ships.subs :as subs]
            [endless-ships.routes :as routes]))

(defn navigation []
  (let [[route] @(rf/subscribe [::subs/route])]
    [:ul.nav.nav-tabs
     [:li {:role :presentation
           :class (when (= route :ships) :active)}
      [:a {:href (routes/url-for :ships)}
       "Ships"]]
     [:li {:role :presentation
           :class (when (= route :outfits) :active)}
      [:a {:href (routes/url-for :outfits)}
       "Outfits"]]]))
