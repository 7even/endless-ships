(ns endless-ships.views
  (:require [re-frame.core :as rf]
            [endless-ships.subs :as subs]))

(defn interface []
  [:div.container
   [:div.row
    [:div.col-lg-12
     (if @(rf/subscribe [::subs/loading?])
       [:div.app "Loading..."]
       (if @(rf/subscribe [::subs/loading-failed?])
         [:div.app "Failed to load data"]
         [:div.app "Loaded"]))]]])
