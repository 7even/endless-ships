(ns endless-ships.views.configurator
  (:require [clojure.string :as str]
            [endless-ships.events :as events]
            [endless-ships.routes :as routes]
            [endless-ships.subs :as subs]
            [endless-ships.views.utils :refer [format-game-number format-number game-image-url
                                               license-label]]
            [re-frame.core :as rf]
            [reagent.core :as ra]))

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

(defn- copy-to-clipboard
  "Copies text to the clipboard, returning a promise of whether it worked. The Clipboard API
  needs HTTPS, so on plain HTTP it falls back to the deprecated execCommand."
  [text]
  (if (some? (.-clipboard js/navigator))
    (-> (.writeText (.-clipboard js/navigator) text)
        (.then (constantly true))
        (.catch (constantly false)))
    (let [textarea (js/document.createElement "textarea")]
      (set! (.-value textarea) text)
      ;; keeps the page from scrolling to the textarea
      (set! (.. textarea -style -position) "fixed")
      (set! (.. textarea -style -opacity) "0")
      (.appendChild js/document.body textarea)
      (.select textarea)
      (let [copied? (try
                      (js/document.execCommand "copy")
                      (catch :default _
                        false))]
        (.removeChild js/document.body textarea)
        (js/Promise.resolve copied?)))))

(defn- copy-link-button
  "Copies the current URL; the button shows the outcome for a couple of seconds since copying
  has no visible effect otherwise."
  []
  (let [status (ra/atom nil)
        timeout (atom nil)
        show-status (fn [copied?]
                      ;; a previous click's timeout would reset this status too early
                      (js/clearTimeout @timeout)
                      (reset! status
                              (if copied?
                                "Copied"
                                "Copy failed"))
                      (reset! timeout
                              (js/setTimeout #(reset! status nil)
                                             2000)))]
    (fn []
      [:button.btn.btn-default
       {:on-click #(-> (copy-to-clipboard js/location.href)
                       (.then show-status))}
       (or @status "Copy link")])))

(defn- save-form [default-name on-close]
  (let [configuration-name (ra/atom default-name)
        save #(let [trimmed (str/trim @configuration-name)]
                (when (seq trimmed)
                  (rf/dispatch [::events/save-configuration trimmed])
                  (on-close)))]
    (fn []
      [:div.configurator-save-form
       [:input.form-control.input-sm
        {:type "text"
         :value @configuration-name
         :placeholder "Configuration name"
         :auto-focus true
         :on-change #(reset! configuration-name (.. % -target -value))
         :on-key-down #(case (.-key %)
                         "Enter" (save)
                         "Escape" (on-close)
                         nil)}]
       [:span.btn-group.btn-group-sm
        [:button.btn.btn-primary
         {:disabled (str/blank? @configuration-name)
          :on-click save}
         "Save"]
        [:button.btn.btn-default
         {:on-click on-close}
         "Cancel"]]])))

(defn- save-button [saving?]
  (let [saved @(rf/subscribe [::subs/current-saved-configuration])]
    (if (some? saved)
      [:button.btn.btn-default.configurator-saved-as
       {:disabled true
        :title (str "Saved as " (:name saved))}
       "Saved as " (:name saved)]
      [:button.btn.btn-default
       {:disabled @saving?
        :on-click #(reset! saving? true)}
       "Save"])))

(defn- summary [ship stock?]
  (let [saving? (ra/atom false)]
    (fn [ship stock?]
      (let [{:keys [hull outfits total]} @(rf/subscribe [::subs/configurator-cost])
            game-commit (:hash @(rf/subscribe [::subs/game-version]))
            storage-available? @(rf/subscribe [::subs/storage-available?])
            {:keys [name modification sprite]} ship]
        [:div.panel.panel-default
         [:div.panel-heading.panel-heading-with-button
          (if (some? modification)
            (routes/ship-modification-link name modification)
            (routes/ship-link name))
          [:div.btn-group.btn-group-xs
           [copy-link-button]
           (when storage-available?
             [save-button saving?])]]
         (when @saving?
           [:div.panel-body
            [save-form
             (or modification name)
             #(reset! saving? false)]])
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
              [:img.ship-sprite {:src (game-image-url game-commit sprite)}])]]]]))))

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

(defn- format-range
  "Formats [value with full cargo, value with no cargo] like the game does."
  [[full empty]]
  (if (= (format-game-number full)
         (format-game-number empty))
    (format-game-number empty)
    (str (format-game-number full) " – " (format-game-number empty))))

(defn- with-rate [value rate]
  (if (pos? rate)
    (str (format-game-number value) " (" (format-game-number rate) "/s)")
    (format-game-number value)))

(defn- stat-row
  ([label value]
   (stat-row label value nil))
  ([label value class]
   [:tr {:class class}
    [:td label]
    [:td.text-right value]]))

(defn- flight-check-alert []
  (let [{:keys [overheating? no-energy?]} @(rf/subscribe [::subs/configurator-stats])]
    (when (or overheating? no-energy?)
      [:div.alert.alert-danger
       (when overheating?
         [:div
          [:strong "Overheating:"]
          " idle heat exceeds the maximum heat of the ship."])
       (when no-energy?
         [:div
          [:strong "No energy:"]
          " nothing generates or stores enough energy to power the ship."])])))

(def ^:private damage-types
  (array-map :shield-damage "shield damage"
             :hull-damage "hull damage"
             :heat-damage "heat damage"
             :ion-damage "ion damage"
             :disruption-damage "disruption damage"
             :slowing-damage "slowing damage"))

(defn- characteristics []
  (let [{:keys [shields shield-regen hull hull-repair mass cargo-space required-crew bunks
                fuel-capacity max-speed max-speed-afterburner acceleration
                acceleration-afterburner turning damage energy-heat net-change capacity
                overheating? no-energy?]}
        @(rf/subscribe [::subs/configurator-stats])]
    [:div.panel.panel-default
     [:div.panel-heading "Characteristics"]
     [:div.configurator-stats-columns
      [:div
       [:table.table.table-condensed.configurator-stats
        [:tbody
         (stat-row "shields" (with-rate shields shield-regen))
         (stat-row "hull" (with-rate hull hull-repair))
         (stat-row "mass" (str (format-game-number mass) " tons"))
         (stat-row "cargo space" (str (format-game-number cargo-space) " tons"))
         (stat-row "required crew / bunks"
                   (str (format-game-number required-crew) " / " (format-game-number bunks))
                   (when (> required-crew bunks)
                     "danger"))
         (stat-row "fuel capacity" (format-game-number fuel-capacity))]
        [:tbody
         [:tr.configurator-stats-header
          [:td {:col-span 2}
           (if (pos? cargo-space)
             "movement (full cargo – no cargo)"
             "movement")]]
         (stat-row "max speed" (format-game-number max-speed))
         (when (some? max-speed-afterburner)
           (stat-row "w/ afterburner" (format-game-number max-speed-afterburner)))
         (stat-row "acceleration" (format-range acceleration))
         (when (some? acceleration-afterburner)
           (stat-row "w/ afterburner" (format-range acceleration-afterburner)))
         (stat-row "turning" (format-range turning))]]]
      [:div
       [:table.table.table-condensed.configurator-stats
        [:thead
         [:tr
          [:th]
          [:th.text-right "energy"]
          [:th.text-right "heat"]]]
        [:tbody
         (for [{:keys [label energy heat]} energy-heat]
           ^{:key label}
           [:tr
            [:td label]
            [:td.text-right (format-game-number energy)]
            [:td.text-right (format-game-number heat)]])
         [:tr.configurator-stats-total
          [:td "net change"]
          [:td.text-right {:class (when (neg? (:energy net-change))
                                    "text-danger")}
           (format-game-number (:energy net-change))]
          [:td.text-right (format-game-number (:heat net-change))]]
         [:tr
          [:td "capacity"]
          [:td.text-right {:class (when no-energy?
                                    "text-danger")}
           (format-game-number (:energy capacity))]
          [:td.text-right {:class (when overheating?
                                    "text-danger")}
           (format-game-number (:heat capacity))]]]
        [:tbody
         [:tr.configurator-stats-header
          [:td {:col-span 3}
           "firepower (per second)"]]
         (for [[damage-type label] damage-types
               :let [value (get damage damage-type)]
               ;; shields and hull are always shown, the rarer types only when present
               :when (or (contains? #{:shield-damage :hull-damage} damage-type)
                         (pos? value))]
           ^{:key damage-type}
           [:tr
            [:td label]
            [:td.text-right {:col-span 2}
             (format-game-number value)]])]]]]]))

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
   (interpose " " (map license-label licenses))
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

(defn- not-found-message
  "A message about a ship or modification from the URL missing in the game data; it doesn't say
  it was removed, since the URL may just be mistyped."
  [{:keys [missing ship outfits? base-url]}]
  (case missing
    :ship [:p
           "Ship not found. Pick one from the "
           [:a {:href (routes/url-for :ships)} "list of ships"]
           "."]
    :modification [:p
                   "Modification not found. "
                   [:a {:href base-url}
                    "Open the base " (:name ship)
                    (when outfits?
                      " with the same outfits")]
                   "."]))

(defn- local-date
  "Formats an ISO timestamp (stored in UTC) as YYYY-MM-DD in the browser's time zone."
  [timestamp]
  (let [date (js/Date. timestamp)
        pad #(.padStart (str %) 2 "0")]
    (str (.getFullYear date)
         "-"
         (pad (inc (.getMonth date)))
         "-"
         (pad (.getDate date)))))

(defn- saved-configurations []
  (let [saved @(rf/subscribe [::subs/saved-configurations])]
    (when (seq saved)
      [:div.panel.panel-default
       [:div.panel-heading "Saved configurations"]
       [:table.table.table-condensed.configurator-saved
        [:tbody
         (for [{:keys [id name ship url saved-at]} saved]
           ^{:key id}
           [:tr
            [:td [:a {:href url} name]]
            [:td ship]
            [:td.text-muted (local-date saved-at)]
            [:td.text-right
             [:button.btn.btn-default.btn-xs.configurator-glyph-button
              {:title "Delete"
               :on-click #(rf/dispatch [::events/delete-saved-configuration id])}
              "×"]]])]]
       [:div.panel-footer.text-muted
        "Saved in this browser only. To share a configuration, copy its link."]])))

(defn configurator-page [ship-slug]
  (let [{:keys [ship unknown-outfits]} @(rf/subscribe [::subs/configuration])
        not-found @(rf/subscribe [::subs/configurator-not-found])
        violations @(rf/subscribe [::subs/configurator-violations])
        stock? @(rf/subscribe [::subs/configurator-stock?])]
    [:div.app
     (cond
       (nil? ship-slug) [:div.row
                         [:div.col-md-6
                          [:p
                           "To try out different outfits on a ship, open it from the "
                           [:a {:href (routes/url-for :ships)} "list of ships"]
                           " and click \"Open in configurator\"."]
                          [saved-configurations]]]
       (some? not-found) [not-found-message not-found]
       :else [:div.row
              [:div.col-md-6
               [violations-alert violations stock?]
               [unknown-outfits-alert unknown-outfits]
               [summary ship stock?]
               [flight-check-alert]
               [characteristics]
               [resources violations]]
              [:div.col-md-6
               [outfits-panel]]])]))
