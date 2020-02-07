(ns endless-ships.events
  (:require [re-frame.core :as rf]
            [day8.re-frame.http-fx]
            [ajax.edn :as ajax]
            [endless-ships.views.utils :refer [kebabize]]))

(rf/reg-event-fx ::initialize
                 (fn [{db :db} _]
                   {:db {:loading? true
                         :loading-failed? false
                         :route [:ships {}]
                         :ships {}
                         :settings {:ships {:ordering {:column-name nil}
                                            :filters-collapsed? true
                                            :race-filter {}
                                            :category-filter {}
                                            :license-filter {}}}}
                    :http-xhrio {:method :get
                                 :uri "/data.edn"
                                 :response-format (ajax/edn-response-format)
                                 :on-success [::data-loaded]
                                 :on-failure [::data-failed-to-load]}}))

(defn- toggle-filter [filter value]
  (update filter value not))

(defn- initial-filter [values]
  (->> values
       (into #{})
       (reduce toggle-filter (sorted-map))))

(rf/reg-event-db ::data-loaded
                 (fn [db [_ data]]
                   (-> db
                       (assoc :loading? false
                              :ships (reduce (fn [ships {:keys [name] :as ship}]
                                               (assoc ships
                                                      (kebabize name)
                                                      ship))
                                             {}
                                             (:ships data)))
                       (update-in [:settings :ships]
                                  merge
                                  {:race-filter (->> (:ships data)
                                                     (map :race)
                                                     initial-filter)
                                   :category-filter (->> (:ships data)
                                                         (map :category)
                                                         initial-filter)
                                   :license-filter (->> (:ships data)
                                                        (map :licenses)
                                                        (apply concat)
                                                        (keep identity)
                                                        initial-filter)}))))

(rf/reg-event-db ::data-failed-to-load
                 (fn [db _]
                   (assoc db
                          :loading? false
                          :loading-failed? true)))

(rf/reg-event-db ::navigate-to
                 (fn [db [_ route]]
                   (assoc db :route route)))

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

(rf/reg-event-db ::toggle-ship-filters-visibility
                 (fn [db]
                   (update-in db
                              [:settings :ships :filters-collapsed?]
                              not)))

(rf/reg-event-db ::toggle-ships-race-filter
                 (fn [db [_ race]]
                   (update-in db
                              [:settings :ships :race-filter race]
                              not)))

(rf/reg-event-db ::toggle-ships-category-filter
                 (fn [db [_ category]]
                   (update-in db
                              [:settings :ships :category-filter category]
                              not)))

(rf/reg-event-db ::toggle-ships-license-filter
                 (fn [db [_ license]]
                   (update-in db
                              [:settings :ships :license-filter license]
                              not)))
