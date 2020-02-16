(ns endless-ships.views.outfits
  (:require [re-frame.core :as rf]
            [endless-ships.views.utils :refer [license-label kebabize nbspize format-number]]
            [endless-ships.views.table :refer [table left-cell right-cell]]
            [endless-ships.utils.outfits :as utils]
            [endless-ships.subs :as subs]
            [endless-ships.routes :as routes]))

(defn thruster-row [name]
  (let [{:keys [cost outfit-space thrust
                thrusting-energy thrusting-heat licenses]
         :as thruster} @(rf/subscribe [::subs/outfit name])]
    [:tr
     [left-cell
      ^{:key name} [:a
                    {:href (routes/url-for :outfit :outfit/name (kebabize name))}
                    (nbspize name)]]
     [right-cell (format-number cost)]
     [right-cell (format-number outfit-space)]
     [right-cell (format-number thrust)]
     [right-cell (format-number (utils/thruster-effectiveness thruster))]
     [right-cell (format-number thrusting-energy)]
     [right-cell (format-number thrusting-heat)]
     [left-cell (->> licenses
                     (map license-label)
                     (interpose " "))]]))

(defn thrusters-table []
  [table :thrusters (:thrusters utils/columns) @(rf/subscribe [::subs/thrusters-ordering])
   (map (fn [name]
          ^{:key name} [thruster-row name])
        @(rf/subscribe [::subs/thruster-names]))])

(defn outfits []
  [:div.app
   [:h2 "Thrusters"]
   [thrusters-table]])
