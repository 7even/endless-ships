(ns endless-ships.views.table
  (:require [re-frame.core :as rf]
            [endless-ships.events :as events]))

(defn table-headers [entity-type columns ordering]
  [:tr (for [[text prop] columns]
         (let [title (if (some? prop)
                       [:a.table-header
                        {:on-click #(rf/dispatch [::events/toggle-ordering entity-type text])}
                        text]
                       text)
               icon (when (and (some? prop)
                               (= (:column-name ordering) text))
                      (if (= (:order ordering) :asc)
                        [:span.glyphicon.glyphicon-sort-by-attributes]
                        [:span.glyphicon.glyphicon-sort-by-attributes-alt]))]
           ^{:key text} [:th.text-center title " " icon]))])

(defn table [entity-type header-columns ordering rows]
  [:table.table-striped.table-bordered.table-condensed.table-hover
   [:thead [table-headers entity-type header-columns ordering]]
   [:tbody rows]])

(defn left-cell [& children]
  [:td.text-left children])

(defn right-cell [& children]
  [:td.text-right children])
