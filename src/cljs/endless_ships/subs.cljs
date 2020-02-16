(ns endless-ships.subs
  (:require [re-frame.core :as rf]
            [endless-ships.utils.ships :as ships]
            [endless-ships.utils.outfits :as outfits]
            [endless-ships.views.utils :refer [kebabize]]))

(rf/reg-sub ::loading?
            (fn [db]
              (:loading? db)))

(rf/reg-sub ::loading-failed?
            (fn [db]
              (:loading-failed? db)))

(rf/reg-sub ::route
            (fn [db]
              (:route db)))

(rf/reg-sub ::ships
            (fn [db]
              (-> db :ships vals)))

(rf/reg-sub ::ships-ordering
            (fn [db]
              (get-in db [:settings :ships :ordering])))

(rf/reg-sub ::ship-filters-collapsed?
            (fn [db]
              (get-in db [:settings :ships :filters-collapsed?])))

(rf/reg-sub ::ships-race-filter
            (fn [db]
              (get-in db [:settings :ships :race-filter])))

(rf/reg-sub ::ships-category-filter
            (fn [db]
              (get-in db [:settings :ships :category-filter])))

(rf/reg-sub ::ships-license-filter
            (fn [db]
              (get-in db [:settings :ships :license-filter])))

(defn- sort-with-settings [columns ordering coll]
  (let [ordering-prop (get columns (:column-name ordering))]
    (sort (if (some? (:column-name ordering))
            (fn [item1 item2]
              (let [item1-prop (ordering-prop item1)
                    item2-prop (ordering-prop item2)]
                (if (= (:order ordering) :asc)
                  (compare item1-prop item2-prop)
                  (compare item2-prop item1-prop))))
            (constantly 0))
          coll)))

(rf/reg-sub ::ship-names
            (fn []
              [(rf/subscribe [::ships])
               (rf/subscribe [::ships-ordering])
               (rf/subscribe [::ships-race-filter])
               (rf/subscribe [::ships-category-filter])
               (rf/subscribe [::ships-license-filter])])
            (fn [[all-ships ordering race-filter category-filter license-filter]]
              (->> all-ships
                   (filter (fn [ship]
                             (and (get race-filter (:race ship))
                                  (get category-filter (:category ship))
                                  (not-any? (fn [license]
                                              (not (get license-filter license)))
                                            (get ship :licenses [])))))
                   (sort-with-settings ships/columns ordering)
                   (map :name))))

(rf/reg-sub ::ship
            (fn [db [_ name]]
              (get-in db [:ships (kebabize name)])))

(rf/reg-sub ::outfits
            (fn [db]
              (:outfits db)))

(rf/reg-sub ::ship-modifications-names
            (fn [db [_ ship-name]]
              (->> (get-in db [:ship-modifications ship-name])
                   vals
                   (map :modification))))

(rf/reg-sub ::ship-modification
            (fn [db [_ ship-name modification-name]]
              (get-in db [:ship-modifications ship-name modification-name])))

(rf/reg-sub ::outfit
            (fn [db [_ name]]
              (get-in db [:outfits (kebabize name)])))

(rf/reg-sub ::outfit-installations
            (fn [db [_ name]]
              (->> (concat (-> db :ships vals)
                           (->> (:ship-modifications db)
                                vals
                                (mapcat vals)))
                   (reduce (fn [installations ship]
                             (if-let [ship-outfit (->> (:outfits ship)
                                                       (filter #(= (:name %) name))
                                                       first)]
                               (conj installations
                                     {:ship-name (:name ship)
                                      :ship-modification (:modification ship)
                                      :quantity (:quantity ship-outfit)})
                               installations))
                           [])
                   (sort-by (juxt (fn [{:keys [quantity]}]
                                    (- quantity))
                                  (fn [{:keys [ship-name ship-modification]}]
                                    (or ship-modification ship-name)))))))

(rf/reg-sub ::outfit-planets
            (fn [db [_ name]]
              (->> (:outfitters db)
                   (filter (fn [{:keys [outfits]}]
                             (outfits name)))
                   (mapcat :planets)
                   (into #{})
                   (sort-by :name))))

(rf/reg-sub ::thrusters-ordering
            (fn [db]
              (get-in db [:settings :thrusters :ordering])))

(rf/reg-sub ::thruster-names
            (fn []
              [(rf/subscribe [::outfits])
               (rf/subscribe [::thrusters-ordering])])
            (fn [[outfits ordering]]
              (->> (vals outfits)
                   (filter #(contains? % :thrust))
                   (sort-with-settings (:thrusters outfits/columns) ordering)
                   (map :name))))
