(ns endless-ships.subs
  (:require [re-frame.core :as rf]))

(rf/reg-sub ::loading?
            (fn [db]
              (:loading? db)))

(rf/reg-sub ::loading-failed?
            (fn [db]
              (:loading-failed? db)))

(rf/reg-sub ::ships-ordering
            (fn [db]
              (get-in db [:settings :ships :ordering])))

(rf/reg-sub ::ship-names
            (fn [db]
              (keys (:ships db))))

(rf/reg-sub ::ship
            (fn [db [_ name]]
              (get-in db [:ships name])))
