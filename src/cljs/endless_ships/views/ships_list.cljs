(ns endless-ships.views.ships-list
  (:require [re-frame.core :as rf]
            [endless-ships.subs :as subs]
            [endless-ships.views.table :refer [table left-cell right-cell]]
            [endless-ships.views.utils :refer [license-label nbsp nbspize]]
            [clojure.string :as str]))

(defn total-cost [{:keys [empty-hull-cost outfits-cost]}]
  (+ empty-hull-cost outfits-cost))

(defn or-zero [prop]
  #(or (prop %)
       0))

(def columns
  [["Name"         :name]
   ["Race"         nil]
   ["Cost"         total-cost]
   ["Category"     nil]
   ["Hull"         (or-zero :hull)]
   ["Shields"      (or-zero :shields)]
   ["Mass"         (or-zero :mass)]
   ["Engine cap."  (or-zero :engine-capacity)]
   ["Weapon cap."  (or-zero :weapon-capacity)]
   ["Fuel cap."    (or-zero :fuel-capacity)]
   ["Outfit sp."   (or-zero :outfit-space)]
   ["Cargo sp."    (or-zero :cargo-space)]
   ["Crew / bunks" (or-zero :bunks)]
   ["Licenses"     nil]])

(defn race-label [race]
  ^{:key race} [:span.label {:class (str "label-" (name race))} race])

(defn crew-and-bunks [{:keys [required-crew bunks]}]
  (if (pos? required-crew)
    [:td (str required-crew
              nbsp "/" nbsp
              bunks)]
    [:td]))

(defn license-labels [{:keys [licenses]}]
  (let [labels (map license-label licenses)]
    [:td (interpose " " labels)]))

(defn ship-row [name]
  (let [{:keys [race category hull shields mass
                engine-capacity weapon-capacity fuel-capacity
                outfit-space cargo-space] :as ship} @(rf/subscribe [::subs/ship name])]
    [:tr
     [left-cell (nbspize name)]
     [left-cell (race-label race)]
     [right-cell (total-cost ship)]
     [left-cell (nbspize category)]
     [right-cell hull]
     [right-cell shields]
     [right-cell mass]
     [right-cell engine-capacity]
     [right-cell weapon-capacity]
     [right-cell fuel-capacity]
     [right-cell outfit-space]
     [right-cell cargo-space]
     [crew-and-bunks ship]
     [license-labels ship]]))

(defn ships-list []
  [table :ships columns @(rf/subscribe [::subs/ships-ordering])
   (map (fn [name]
          ^{:key name} [ship-row name])
        @(rf/subscribe [::subs/ship-names]))])
