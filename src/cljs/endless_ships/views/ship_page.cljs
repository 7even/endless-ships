(ns endless-ships.views.ship-page
  (:require [re-frame.core :as rf]
            [endless-ships.subs :as subs]
            [endless-ships.views.utils :refer [render-attribute render-percentage nbspize kebabize]]
            [endless-ships.utils.ships :refer [total-cost or-zero]]))

(defn- render-licenses [[license1 license2]]
  (if (some? license2)
    [:p.italic (str "This ship requires " license1 " and " license2 " licenses.")]
    [:p.italic (str "This ship requires a " license1 " license.")]))

(defn- image-url [ship]
  (let [filename (cond
                   (= (:name ship) "Shuttle")
                   "ship/shuttle=0.png"

                   (last (:sprite ship))
                   (str (-> ship :sprite first (js/window.encodeURI)) "-0.png")

                   :else
                   (str (-> ship :sprite first (js/window.encodeURI)) ".png"))]
    (str "https://raw.githubusercontent.com/endless-sky/endless-sky/master/images/" filename)))

(defn ship-modifications [ship-name selected-modification-slug modification-names]
  [:div.panel.panel-default
   [:div.panel-heading "Modifications"]
   [:div.panel-body
    [:ul.nav.nav-pills.nav-stacked
     [:li {:role "presentation"
           :class (when (nil? selected-modification-slug) "active")}
      [:a {:href (str "/ships/" (kebabize ship-name))} ship-name]]
     (for [modification-name modification-names]
       ^{:key modification-name}
       [:li {:role "presentation"
             :class (when (= (kebabize modification-name) selected-modification-slug) "active")}
        [:a
         {:href (str "/ships/" (kebabize ship-name) "/" (kebabize modification-name))}
         modification-name]])]]])

(def outfit-categories
  ["Guns"
   "Turrets"
   "Secondary Weapons"
   "Ammunition"
   "Systems"
   "Power"
   "Engines"
   "Hand to Hand"
   "Special"])

(defn outfit-item [name quantity]
  (if (= quantity 1)
    [:li.list-group-item (nbspize name)]
    [:li.list-group-item
     [:span.badge quantity]
     (nbspize name)]))

(defn outfits-list [outfits]
  (let [items (->> outfit-categories
                   (map (fn [category]
                          (when (contains? outfits category)
                            (let [header ^{:key category} [:span.list-group-item.disabled category]
                                  items (->> (get outfits category)
                                             (sort-by #(get-in % [:outfit :name]))
                                             (map (fn [{:keys [outfit quantity]}]
                                                    ^{:key (:name outfit)}
                                                    [outfit-item (:name outfit) quantity])))]
                              (cons header items)))))
                   (keep identity))]
    [:ul.list-group items]))

(defn ship-page [ship-name ship-modification]
  (let [ship @(rf/subscribe [::subs/ship ship-name])
        outfits @(rf/subscribe [::subs/outfits])
        ship-outfits (->> (:outfits ship)
                          (map (fn [{:keys [name quantity]}]
                                 (let [outfit (get outfits (kebabize name))]
                                   {:outfit outfit
                                    :quantity quantity})))
                          (group-by #(get-in % [:outfit :category])))
        modification-names @(rf/subscribe [::subs/ship-modifications-names ship-name])]
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
            (render-percentage ship :self-destruct "self-destruct")]
           (when (some? (:licenses ship))
             (render-licenses (:licenses ship)))]
          [:div.media-right
           [:img.ship-sprite {:src (image-url ship)}]]]]]
       (when (seq modification-names)
         (ship-modifications (:name ship) ship-modification modification-names))]
      [:div.col-md-6
       [:div.panel.panel-default
        [:div.panel-heading "Default outfits"]
        [:div.panel-body (outfits-list ship-outfits)]]]]]))
