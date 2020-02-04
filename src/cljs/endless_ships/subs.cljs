(ns endless-ships.subs
  (:require [re-frame.core :as rf]
            [endless-ships.utils.ships :as ships]))

(rf/reg-sub ::loading?
            (fn [db]
              (:loading? db)))

(rf/reg-sub ::loading-failed?
            (fn [db]
              (:loading-failed? db)))

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

(rf/reg-sub ::ship-names
            (fn []
              [(rf/subscribe [::ships])
               (rf/subscribe [::ships-ordering])])
            (fn [[ships {:keys [column-name order]}]]
              (let [prop (get ships/columns column-name)
                    sorted (if (some? column-name)
                             (sort (fn [ship1 ship2]
                                     (let [ship1-prop (prop ship1)
                                           ship2-prop (prop ship2)]
                                       (if (= order "asc")
                                         (compare ship1-prop ship2-prop)
                                         (compare ship2-prop ship1-prop))))
                                   ships)
                             ships)]
                (map :name sorted))))

(rf/reg-sub ::ship
            (fn [db [_ name]]
              (get-in db [:ships name])))
