(ns endless-ships.events
  (:require [re-frame.core :as rf]
            [day8.re-frame.http-fx]
            [ajax.edn :as ajax]))

(rf/reg-event-fx ::initialize
                 (fn [{db :db} _]
                   {:db {:loading? true
                         :loading-failed? false
                         :ships {}
                         :settings {:ships {:ordering {:column-name nil}}}}
                    :http-xhrio {:method :get
                                 :uri "/data.edn"
                                 :response-format (ajax/edn-response-format)
                                 :on-success [::data-loaded]
                                 :on-failure [::data-failed-to-load]}}))

(rf/reg-event-db ::data-loaded
                 (fn [db [_ data]]
                   (assoc db
                          :loading? false
                          :ships (reduce (fn [ships {:keys [name] :as ship}]
                                           (assoc ships name ship))
                                         {}
                                         (:ships data)))))

(rf/reg-event-db ::data-failed-to-load
                 (fn [db _]
                   (assoc db
                          :loading? false
                          :loading-failed? true)))

(defn- toggle-ordering [db entity-type column]
  (update-in db
             [:settings entity-type :ordering]
             (fn [{:keys [column-name order]}]
               (cond
                 (not= column-name column) {:column-name column
                                            :order "desc"}
                 (= order "asc") {:column-name nil}
                 :else {:column-name column
                        :order "asc"}))))

(rf/reg-event-db ::toggle-ordering
                 (fn [db [_ entity-type column]]
                   (toggle-ordering db entity-type column)))
