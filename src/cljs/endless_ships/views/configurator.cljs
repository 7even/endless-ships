(ns endless-ships.views.configurator
  (:require [clojure.string :as str]
            [endless-ships.events :as events]
            [endless-ships.routes :as routes]
            [endless-ships.subs :as subs]
            [endless-ships.views.utils :refer [format-number game-image-url license-label]]
            [re-frame.core :as rf]))

(defn- violations-alert [violations stock?]
  (when (seq violations)
    [:div.alert.alert-danger
     [:strong "This configuration doesn't fit."]
     (when stock?
       " The ship comes with these outfits in the game nevertheless.")
     [:ul
      (for [[attr-name {:keys [value minimum]}] (sort violations)]
        ^{:key attr-name}
        [:li attr-name ": " (format-number value)
         (when (neg? minimum)
           (str " (minimum " (format-number minimum) ")"))])]]))

(defn- unknown-outfits-alert [slugs]
  (when (seq slugs)
    [:div.alert.alert-warning
     "Unknown outfits in the link were ignored: " (str/join ", " slugs)]))

(defn- summary [ship stock?]
  (let [{:keys [hull outfits total]} @(rf/subscribe [::subs/configurator-cost])
        game-commit (:hash @(rf/subscribe [::subs/game-version]))
        {:keys [name modification sprite]} ship]
    [:div.panel.panel-default
     [:div.panel-heading
      (if (some? modification)
        (routes/ship-modification-link name modification)
        (routes/ship-link name))]
     [:div.panel-body
      [:div.media
       [:div.media-body
        [:ul
         [:li "hull cost: " (format-number hull)]
         [:li "outfits cost: " (format-number outfits)]
         [:li [:strong "total cost: " (format-number total)]]]
        (when stock?
          [:p.italic "These are the stock outfits of the ship."])]
       [:div.media-right
        (when (some? sprite)
          [:img.ship-sprite {:src (game-image-url game-commit sprite)}])]]]]))

(defn- percentage [used capacity]
  (cond
    (pos? capacity) (min 100
                         (* 100
                            (/ used capacity)))
    (pos? used) 100
    :else 0))

(def ^:private outfit-space-parts
  "Resources that are parts of the outfit space; the game shows them indented under it."
  #{"weapon capacity" "engine capacity"})

(defn- resources [violations]
  (let [rows @(rf/subscribe [::subs/configurator-resources])]
    [:div.panel.panel-default
     [:div.panel-heading "Free capacity"]
     [:table.table.table-condensed.configurator-resources
      [:tbody
       (for [{:keys [attribute capacity used free]} rows]
         (let [overloaded? (contains? violations attribute)]
           ^{:key attribute}
           [:tr {:class (when overloaded?
                          "overloaded")}
            [:td {:class (when (contains? outfit-space-parts attribute)
                           "configurator-resource-part")}
             attribute]
            [:td.configurator-bar
             [:div.progress
              ;; the bar shows the used part, the label shows free / total like the game
              [:div.progress-bar {:style {:width (str (percentage used capacity) "%")}}]
              [:div.configurator-bar-label
               [:span.configurator-bar-free (format-number free)]
               [:span.configurator-bar-slash "/"]
               [:span.configurator-bar-total (format-number capacity)]]]]]))]]]))

(defn- quantity-buttons [outfit-name quantity can-add?]
  [:span.btn-group.btn-group-xs
   [:button.btn.btn-default.configurator-glyph-button
    {:title "Remove one"
     :on-click #(rf/dispatch [::events/change-configurator-outfit outfit-name -1])}
    "−"]
   [:button.btn.btn-default.configurator-glyph-button
    {:title "Add one more"
     :disabled (not can-add?)
     :on-click #(rf/dispatch [::events/change-configurator-outfit outfit-name 1])}
    "+"]
   [:button.btn.btn-default.configurator-glyph-button
    {:title "Remove all of them"
     :on-click #(rf/dispatch [::events/change-configurator-outfit
                              outfit-name
                              (- quantity)])}
    "×"]])

(defn- installed-outfit [{:keys [name quantity can-add?]}]
  [:li.list-group-item.configurator-installed
   [:span.configurator-quantity quantity]
   (routes/outfit-link name)
   [quantity-buttons name quantity can-add?]])

(defn- catalog-outfit [{:keys [name cost licenses stats max can-add?]}]
  [:li.list-group-item {:class (when-not can-add?
                                 "configurator-unavailable")}
   [:button.btn.btn-default.btn-xs.pull-right.configurator-glyph-button
    {:disabled (not can-add?)
     :on-click #(rf/dispatch [::events/change-configurator-outfit name 1])}
    "+"]
   (when (and (some? max)
              (pos? max))
     [:span.configurator-max "max " (format-number max)])
   (routes/outfit-link name)
   " "
   (for [license licenses]
     (license-label license))
   [:small.text-muted " " (format-number cost)]
   (when (seq stats)
     [:div.configurator-outfit-stats
      (for [[label value] stats]
        ^{:key label} [:span label " " (format-number value)])])])

(defn- catalog
  "List of outfits of a category that can be added to the ship."
  [category]
  (let [{:keys [search only-sold?]} @(rf/subscribe [::subs/configurator-settings])
        groups @(rf/subscribe [::subs/configurator-catalog category])]
    [:li.list-group-item.configurator-catalog
     [:div.configurator-catalog-filters
      [:input.form-control.input-sm
       {:type "text"
        :placeholder "Search outfits"
        :auto-focus true
        :value search
        :on-change #(rf/dispatch [::events/set-configurator-search (.. % -target -value)])}]
      [:div.checkbox
       [:label
        [:input {:type "checkbox"
                 :checked only-sold?
                 :on-change #(rf/dispatch [::events/toggle-configurator-only-sold])}]
        "Only sold in outfitters"]]]
     (if (empty? groups)
       [:p.text-muted.configurator-catalog-empty "No outfits found."]
       [:ul.list-group.configurator-catalog-list
        (mapcat (fn [{:keys [type header outfits]}]
                  (cons (when (> (count groups) 1)
                          ^{:key (str "type-" type)}
                          [:li.list-group-item.configurator-catalog-type (or header "Other")])
                        (map (fn [outfit]
                               ^{:key (:name outfit)} [catalog-outfit outfit])
                             outfits)))
                groups)])]))

(defn- category-header [category addable? open?]
  (let [toggle #(rf/dispatch [::events/toggle-configurator-category category])
        ;; names the category, so that screen readers can tell the headers apart
        label (if open?
                (str "Close the list of " category " outfits")
                (str "Add " category " outfits"))]
    [:li.list-group-item.configurator-category
     (when addable?
       {:class (if open?
                 "open"
                 "addable")
        :role "button"
        :tab-index 0
        :title label
        :aria-label label
        :on-click toggle
        :on-key-down #(when (contains? #{"Enter" " "} (.-key %))
                        (.preventDefault %)
                        (toggle))})
     (or category "Other")
     (when addable?
       [:span.configurator-category-icon (if open?
                                           "×"
                                           "+")])]))

(defn- outfits-panel []
  (let [categories @(rf/subscribe [::subs/configurator-categories])
        installed @(rf/subscribe [::subs/configurator-installed])
        {:keys [open-category]} @(rf/subscribe [::subs/configurator-settings])
        stock? @(rf/subscribe [::subs/configurator-stock?])
        no-outfits? (empty? (:outfits @(rf/subscribe [::subs/configuration])))]
    [:div.panel.panel-default
     [:div.panel-heading.panel-heading-with-button
      "Outfits"
      [:div.btn-group.btn-group-xs
       [:button.btn.btn-default
        {:disabled stock?
         :on-click #(rf/dispatch [::events/reset-configurator-outfits])}
        "Reset to stock"]
       [:button.btn.btn-default
        {:disabled no-outfits?
         :on-click #(rf/dispatch [::events/remove-configurator-outfits])}
        "Remove all"]]]
     [:ul.list-group
      (mapcat (fn [{:keys [category addable?]}]
                (let [open? (and addable?
                                 (= category open-category))]
                  (concat [^{:key (str "category-" category)}
                           [category-header category addable? open?]]
                          (when open?
                            [^{:key (str "catalog-" category)} [catalog category]])
                          (map (fn [outfit]
                                 ^{:key (:name outfit)} [installed-outfit outfit])
                               (get installed category)))))
              categories)]]))

(defn configurator-page [ship-slug]
  (let [{:keys [ship unknown-outfits]} @(rf/subscribe [::subs/configuration])
        violations @(rf/subscribe [::subs/configurator-violations])
        stock? @(rf/subscribe [::subs/configurator-stock?])]
    [:div.app
     (cond
       (nil? ship-slug) [:p
                         "To try out different outfits on a ship, open it from the "
                         [:a {:href (routes/url-for :ships)} "list of ships"]
                         " and click \"Open in configurator\"."]
       (nil? ship) [:p "Unknown ship."]
       :else [:div.row
              [:div.col-md-6
               [violations-alert violations stock?]
               [unknown-outfits-alert unknown-outfits]
               [summary ship stock?]
               [resources violations]]
              [:div.col-md-6
               [outfits-panel]]])]))
