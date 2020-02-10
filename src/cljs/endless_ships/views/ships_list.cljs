(ns endless-ships.views.ships-list
  (:require [re-frame.core :as rf]
            [reagent.core :as ra]
            [endless-ships.events :as events]
            [endless-ships.subs :as subs]
            [endless-ships.views.table :refer [table left-cell right-cell]]
            [endless-ships.views.utils :refer [license-label nbsp nbspize kebabize format-number]]
            [endless-ships.utils.ships :refer [total-cost or-zero columns]]
            [endless-ships.routes :as routes]
            [clojure.string :as str]))

(defn checkbox-group [filter toggling-event]
  (for [[item checked?] filter]
    ^{:key item} [:div.checkbox
                  [:label
                   [:input {:type "checkbox"
                            :checked checked?
                            :on-change #(rf/dispatch [toggling-event item])}]
                   (str/capitalize (name item))]]))

(defn ships-filter []
  (let [height (ra/atom nil)]
    (fn []
      (let [collapsed? @(rf/subscribe [::subs/ship-filters-collapsed?])
            race-filter @(rf/subscribe [::subs/ships-race-filter])
            category-filter @(rf/subscribe [::subs/ships-category-filter])
            license-filter @(rf/subscribe [::subs/ships-license-filter])]
        [:div.filters-group
         [:div {:style {:overflow "hidden"
                        :transition "max-height 0.8s"
                        :max-height (if collapsed? 0 @height)}}
          [:div.container-fluid
           {:ref #(when % (reset! height (.-clientHeight %)))}
           [:div.row
            [:div.col-lg-2.col-md-3
             [:strong "Race"]
             (checkbox-group race-filter ::events/toggle-ships-race-filter)]
            [:div.col-lg-2.col-md-3
             [:strong "Category"]
             (checkbox-group category-filter ::events/toggle-ships-category-filter)]
            [:div.col-lg-2.col-md-3
             [:strong "License"]
             (checkbox-group license-filter ::events/toggle-ships-license-filter)]]]]
         [:button.btn.btn-default
          {:type "button"
           :on-click #(rf/dispatch [::events/toggle-ship-filters-visibility])}
          "Filters "
          (if collapsed?
            [:span.glyphicon.glyphicon-menu-down]
            [:span.glyphicon.glyphicon-menu-up])]]))))

(defn race-label [race]
  ^{:key race} [:span.label {:class (str "label-" (name race))} race])

(defn crew-and-bunks [{:keys [required-crew bunks]}]
  (if (pos? required-crew)
    [right-cell (str (format-number required-crew)
                     nbsp "/" nbsp
                     (format-number bunks))]
    [right-cell]))

(defn license-labels [{:keys [licenses]}]
  (let [labels (map license-label licenses)]
    [left-cell (interpose " " labels)]))

(defn ship-row [name]
  (let [{:keys [race category hull shields mass
                engine-capacity weapon-capacity fuel-capacity
                outfit-space cargo-space] :as ship} @(rf/subscribe [::subs/ship name])]
    [:tr
     [left-cell
      ^{:key name} [:a
                    {:href (routes/url-for :ship :ship/name (kebabize name))}
                    (nbspize name)]]
     [left-cell (race-label race)]
     [right-cell (format-number (total-cost ship))]
     [left-cell (nbspize category)]
     [right-cell (format-number hull)]
     [right-cell (format-number shields)]
     [right-cell (format-number mass)]
     [right-cell (format-number engine-capacity)]
     [right-cell (format-number weapon-capacity)]
     [right-cell (format-number fuel-capacity)]
     [right-cell (format-number outfit-space)]
     [right-cell (format-number cargo-space)]
     [crew-and-bunks ship]
     [license-labels ship]]))

(defn ships-list []
  [:div.app
   [ships-filter]
   [table :ships columns @(rf/subscribe [::subs/ships-ordering])
    (map (fn [name]
           ^{:key name} [ship-row name])
         @(rf/subscribe [::subs/ship-names]))]])
