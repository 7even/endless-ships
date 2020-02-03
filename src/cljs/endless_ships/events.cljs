(ns endless-ships.events
  (:require [re-frame.core :as rf]
            [day8.re-frame.http-fx]
            [ajax.edn :as ajax]))

(rf/reg-event-fx ::initialize
                 (fn [{db :db} _]
                   {:db {:loading? true
                         :loading-failed? false
                         :ships []}
                    :http-xhrio {:method :get
                                 :uri "/data.edn"
                                 :response-format (ajax/edn-response-format)
                                 :on-success [::data-loaded]
                                 :on-failure [::data-failed-to-load]}}))

(rf/reg-event-db ::data-loaded
                 (fn [db [_ data]]
                   (assoc db
                          :loading? false
                          :ships (:ships data))))

(rf/reg-event-db ::data-failed-to-load
                 (fn [db _]
                   (assoc db
                          :loading? false
                          :loading-failed? true)))
