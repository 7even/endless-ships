(ns endless-ships.views.ship-page
  (:require [re-frame.core :as rf]
            [endless-ships.subs :as subs]
            [endless-ships.views.utils :refer [render-attribute render-percentage]]
            [endless-ships.utils.ships :refer [total-cost or-zero]]))

(defn ship-page [ship-name ship-modification]
  (let [ship @(rf/subscribe [::subs/ship ship-name])]
    [:div.app
     [:div.row
      [:div.col-md-6
       [:div.panel.panel-default
        [:div.panel-heading (:name ship)]
        [:div.panel-body
         [:div.media
          [:div.media-body
           [:ul
            (render-attribute ship total-cost "cost")
            (render-attribute ship :shields "shields")
            (render-attribute ship :hull "hull")
            (render-attribute ship :mass "mass")
            (render-attribute ship :cargo-space "cargo space")
            (render-attribute ship :required-crew "required crew")
            (render-attribute ship :bunks "bunks")
            (render-attribute ship :fuel-capacity "fuel capacity")
            (render-attribute ship :outfit-space "outfit space")
            (render-attribute ship :weapon-capacity "weapon capacity")
            (render-attribute ship :engine-capacity "engine capacity")
            (render-attribute ship (or-zero :guns) "guns")
            (render-attribute ship (or-zero :turrets) "turrets")
            (when (pos? (:drones ship))
              (render-attribute ship :drones "drones"))
            (when (pos? (:fighters ship))
              (render-attribute ship :fighters "fighters"))
            (render-attribute ship :ramscoop "ramscoop")
            (render-attribute ship :cloak "cloak")
            (render-percentage ship :self-destruct "self-destruct")]]]]]]]]))
