(ns endless-ships.views.ship-page
  (:require [re-frame.core :as rf]
            [endless-ships.subs :as subs]
            [endless-ships.views.utils :refer [render-attribute render-percentage
                                               render-description nbspize kebabize]]
            [endless-ships.utils.ships :refer [total-cost or-zero]]
            [endless-ships.routes :as routes]))

(defn- render-licenses [[license1 license2]]
  (if (some? license2)
    [:p.italic (str "This ship requires " license1 " and " license2 " licenses.")]
    [:p.italic (str "This ship requires a " license1 " license.")]))

(defn- image-url [ship]
  (let [filename (cond
                   (= (:name ship) "Shuttle")
                   "ship/shuttle=0.png"

                   (and (= (:name ship) "Void Sprite")
                        (not (contains? ship :modification)))
                   "ship/void sprite adult-00.png"

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
      (routes/ship-link ship-name)]
     (for [modification-name modification-names]
       ^{:key modification-name}
       [:li {:role "presentation"
             :class (when (= (kebabize modification-name) selected-modification-slug) "active")}
        (routes/ship-modification-link ship-name modification-name)])]]])

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
  (let [link (routes/outfit-link name)]
    (if (= quantity 1)
      [:li.list-group-item link]
      [:li.list-group-item [:span.badge quantity] link])))

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
        modification-names @(rf/subscribe [::subs/ship-modifications-names ship-name])
        selected-modification (if (some? ship-modification)
                                @(rf/subscribe [::subs/ship-modification ship-name ship-modification])
                                {})
        ship-with-modification (merge ship selected-modification)
        ship-outfits (->> (:outfits ship-with-modification)
                          (map (fn [{:keys [name quantity]}]
                                 (let [outfit (get outfits (kebabize name))]
                                   {:outfit outfit
                                    :quantity quantity})))
                          (group-by #(get-in % [:outfit :category])))]
    [:div.app
     [:div.row
      [:div.col-md-6
       [:div.panel.panel-default
        [:div.panel-heading (:name ship)]
        [:div.panel-body
         [:div.media
          [:div.media-body
           [:ul
            (render-attribute ship-with-modification total-cost "cost")
            (render-attribute ship-with-modification :shields "shields")
            (render-attribute ship-with-modification :hull "hull")
            (render-attribute ship-with-modification :mass "mass")
            (render-attribute ship-with-modification :cargo-space "cargo space")
            (render-attribute ship-with-modification :required-crew "required crew")
            (render-attribute ship-with-modification :bunks "bunks")
            (render-attribute ship-with-modification :fuel-capacity "fuel capacity")
            (render-attribute ship-with-modification :outfit-space "outfit space")
            (render-attribute ship-with-modification :weapon-capacity "weapon capacity")
            (render-attribute ship-with-modification :engine-capacity "engine capacity")
            (render-attribute ship-with-modification (or-zero :guns) "guns")
            (render-attribute ship-with-modification (or-zero :turrets) "turrets")
            (when (pos? (:drones ship-with-modification))
              (render-attribute ship-with-modification :drones "drones"))
            (when (pos? (:fighters ship-with-modification))
              (render-attribute ship-with-modification :fighters "fighters"))
            (render-attribute ship-with-modification :ramscoop "ramscoop")
            (render-attribute ship-with-modification :cloak "cloak")
            (render-percentage ship-with-modification :self-destruct "self-destruct")]
           (when (some? (:licenses ship-with-modification))
             (render-licenses (:licenses ship-with-modification)))]
          [:div.media-right
           [:img.ship-sprite {:src (image-url ship-with-modification)}]]]]]
       (when (seq modification-names)
         (ship-modifications (:name ship) ship-modification modification-names))]
      [:div.col-md-6
       [:div.panel.panel-default
        [:div.panel-heading "Default outfits"]
        [:div.panel-body (outfits-list ship-outfits)]]]]
     (when (seq (:description ship-with-modification))
       [:div.row
        [:div.col-md-12
         [:div.well
          (render-description ship-with-modification)]]])]))
