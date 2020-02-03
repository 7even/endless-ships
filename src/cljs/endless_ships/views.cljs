(ns endless-ships.views
  (:require [re-frame.core :as rf]
            [endless-ships.subs :as subs]))

(defn interface []
  (if @(rf/subscribe [::subs/loading?])
    [:div "Loading..."]
    (if @(rf/subscribe [::subs/loading-failed?])
      [:div "Failed to load data"]
      [:div "Loaded"])))
