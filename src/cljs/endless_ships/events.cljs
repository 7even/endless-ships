(ns endless-ships.events
  (:require [ajax.edn :as ajax]
            [day8.re-frame.http-fx]
            [endless-ships.utils.configurator :as configurator]
            [endless-ships.utils.outfits :as outfits]
            [endless-ships.views.utils :refer [kebabize]]
            [re-frame.core :as rf]))

(def initial-outfit-settings
  (reduce (fn [settings [name {:keys [initial-ordering]}]]
            (assoc settings
                   name
                   {:ordering initial-ordering}))
          {}
          outfits/types))

;; saved configurations live in the browser's localStorage, which may be unavailable
;; (e.g. disabled cookies), so every access is guarded and the site works without it
(def ^:private saved-configurations-key
  "saved-configurations")

(defn- saved-configuration-valid? [saved]
  (and (map? saved)
       (every? string?
               ((juxt :id :name :ship :url :saved-at) saved))))

(defn- parse-saved-configurations [json]
  (let [saved (try
                (js/JSON.parse json)
                (catch :default _
                  nil))]
    (if (array? saved)
      (->> (js->clj saved :keywordize-keys true)
           (filter saved-configuration-valid?)
           vec)
      [])))

(rf/reg-cofx ::saved-configurations
  (fn [cofx _]
    (assoc cofx
           :saved-configurations
           (try
             (parse-saved-configurations (.getItem js/localStorage saved-configurations-key))
             (catch :default _
               [])))))

;; without a writable localStorage the configurator doesn't offer saving at all;
;; the only way to find out whether it's writable (e.g. Safari's private mode can read
;; but not write) is to try, so this writes and removes a test item; it's injected
;; only into ::initialize, so the check runs once at startup and the result is kept in db
(rf/reg-cofx ::storage-available?
  (fn [cofx _]
    (assoc cofx
           :storage-available?
           (try
             (.setItem js/localStorage "storage-test" "")
             (.removeItem js/localStorage "storage-test")
             true
             (catch :default _
               false)))))

(rf/reg-fx ::store-saved-configurations
  (fn [saved]
    (try
      (.setItem js/localStorage
                saved-configurations-key
                (js/JSON.stringify (clj->js saved)))
      (catch :default _
        nil))))

(rf/reg-event-fx ::initialize
  [(rf/inject-cofx ::saved-configurations)
   (rf/inject-cofx ::storage-available?)]
  (fn [{:keys [saved-configurations storage-available?]} _]
    {:db {:loading? true
          :loading-failed? false
          :route [:ships {}]
          :ships {}
          :ship-modifications {}
          :outfits {}
          :outfitters []
          :attribute-minimums {}
          :outfit-categories []
          :version {}
          :saved-configurations saved-configurations
          :storage-available? storage-available?
          :settings (merge {:ships {:ordering {:column-name "Name"
                                               :order :asc}
                                    :filters-collapsed? true
                                    :race-filter {}
                                    :category-filter {}
                                    :license-filter {}}
                            :configurator {:search ""
                                           :only-sold? true
                                           :open-category nil}}
                           initial-outfit-settings)}
     :http-xhrio {:method :get
                  :uri "/data.edn"
                  :response-format (ajax/edn-response-format)
                  :on-success [::data-loaded]
                  :on-failure [::data-failed-to-load]}}))

(defn- index-by-name [coll]
  (reduce (fn [indexed {:keys [name]
                        :as item}]
            (assoc indexed (kebabize name) item))
          {}
          coll))

(defn- group-modifications [modifications]
  (reduce (fn [grouped {:keys [name modification]
                        :as mod}]
            (assoc-in grouped [(kebabize name) (kebabize modification)] mod))
          {}
          modifications))

(defn- process-outfitters [outfitters]
  (map (fn [outfitter]
         (-> outfitter
             (dissoc :name)
             (update :outfits set)))
       outfitters))

(defn- toggle-filter [filter value]
  (update filter value not))

(defn- initial-filter [values]
  (->> values
       (into #{})
       (reduce toggle-filter (sorted-map))))

(rf/reg-event-fx ::data-loaded
  (fn [{:keys [db]} [_ data]]
    {:db (-> db
             (assoc :loading? false
                    :ships (index-by-name (:ships data))
                    :ship-modifications (group-modifications (:ship-modifications data))
                    :outfits (index-by-name (:outfits data))
                    :outfitters (process-outfitters (:outfitters data))
                    :attribute-minimums (:attribute-minimums data)
                    :outfit-categories (:outfit-categories data)
                    :version (:version data))
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
                                              initial-filter)}))
     :endless-ships.routes/start! nil}))

(rf/reg-event-db ::data-failed-to-load
  (fn [db _]
    (assoc db
           :loading? false
           :loading-failed? true)))

(defn- page-title [db [handler route-params]]
  (case handler
    :ships "Ships"
    :ship (let [ship (get-in db
                             [:ships
                              (-> route-params
                                  :ship/name
                                  kebabize)])]
            (:name ship))
    :ship-modification (let [ship-modification (get-in db
                                                       [:ship-modifications
                                                        (:ship/name route-params)
                                                        (:ship/modification route-params)])]
                         (:modification ship-modification))
    :outfits "Outfits"
    :configurator "Configurator"
    :configurator-ship (let [{:keys [name modification]}
                             (-> db
                                 (assoc :route [handler route-params])
                                 configurator/configuration
                                 :ship)]
                         (if (some? name)
                           (str (or modification name) " configurator")
                           "Configurator"))
    :outfit (let [outfit (get-in db
                                 [:outfits
                                  (-> route-params
                                      :outfit/name
                                      kebabize)])]
              (:name outfit))))

(rf/reg-fx ::set-page-title
  (fn [title]
    (set! js/document.title title)))

(rf/reg-event-fx ::navigate-to
  (fn [{:keys [db]} [_ route]]
    {:db (assoc db :route route)
     ::set-page-title (str (page-title db route) " | Endless Sky encyclopedia")}))

(defn- toggle-ordering [db entity-type column]
  (update-in db
             [:settings entity-type :ordering]
             (fn [{:keys [column-name order]}]
               (cond
                 (not= column-name column) {:column-name column
                                            :order :desc}
                 (= order :asc) {:column-name nil}
                 :else {:column-name column
                        :order :asc}))))

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

(defn- configurator-url
  "Returns the URL of the current configurator ship with the given outfits."
  [db outfits]
  (let [[_ {ship-slug :ship/name
            modification-slug :ship/modification}] (:route db)]
    (configurator/url ship-slug modification-slug outfits)))

(rf/reg-event-fx ::change-configurator-outfit
  (fn [{:keys [db]} [_ outfit-name delta]]
    (let [{:keys [outfits]} (configurator/configuration db)]
      {:endless-ships.routes/set-url
       (configurator-url db
                         (configurator/change-quantity outfits outfit-name delta))})))

(rf/reg-event-fx ::reset-configurator-outfits
  (fn [{:keys [db]} _]
    {:endless-ships.routes/set-url (configurator-url db nil)}))

(rf/reg-event-fx ::remove-configurator-outfits
  (fn [{:keys [db]} _]
    {:endless-ships.routes/set-url (configurator-url db [])}))

(defn- store-saved-configurations [db saved]
  {:db (assoc db :saved-configurations saved)
   ::store-saved-configurations saved})

;; changes start from the stored list rather than the one in db: another tab may have saved
;; something since this one was opened

;; the saved URL always lists the outfits, even stock ones: a saved configuration
;; stays the same when a game update changes the ship's stock outfits
(rf/reg-event-fx ::save-configuration
  [(rf/inject-cofx ::saved-configurations)]
  (fn [{:keys [db saved-configurations]} [_ configuration-name]]
    (let [{:keys [ship]} (configurator/configuration db)]
      (store-saved-configurations db
                                  (conj saved-configurations
                                        {:id (str (random-uuid))
                                         :name configuration-name
                                         :ship (or (:modification ship)
                                                   (:name ship))
                                         :url (configurator/explicit-url db)
                                         :saved-at (.toISOString (js/Date.))})))))

(rf/reg-event-fx ::delete-saved-configuration
  [(rf/inject-cofx ::saved-configurations)]
  (fn [{:keys [db saved-configurations]} [_ id]]
    (store-saved-configurations db
                                (->> saved-configurations
                                     (remove #(= (:id %) id))
                                     vec))))

(rf/reg-event-db ::set-configurator-search
  (fn [db [_ search]]
    (assoc-in db [:settings :configurator :search] search)))

(rf/reg-event-db ::toggle-configurator-category
  (fn [db [_ category]]
    (update-in db
               [:settings :configurator]
               (fn [{:keys [open-category]
                     :as settings}]
                 (assoc settings
                        :open-category (when (not= open-category category)
                                         category)
                        :search "")))))

(rf/reg-event-db ::toggle-configurator-only-sold
  (fn [db]
    (update-in db [:settings :configurator :only-sold?] not)))
