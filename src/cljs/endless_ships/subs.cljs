(ns endless-ships.subs
  (:require [clojure.string :as str]
            [endless-ships.configurator :as c]
            [endless-ships.utils.configurator :as configurator]
            [endless-ships.utils.outfits :as outfits]
            [endless-ships.utils.ships :as ships]
            [endless-ships.views.utils :refer [kebabize]]
            [re-frame.core :as rf]))

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
  (let [ordering-prop (get-in columns [(:column-name ordering) :value])]
    (sort (if (some? (:column-name ordering))
            (fn [item1 item2]
              (let [item1-prop (ordering-prop item1)
                    item2-prop (ordering-prop item2)]
                ;; NaN values (e.g. 0/0 per-space ratios) always go last
                (cond
                  (and (js/Number.isNaN item1-prop)
                       (js/Number.isNaN item2-prop)) 0
                  (js/Number.isNaN item1-prop) 1
                  (js/Number.isNaN item2-prop) -1
                  (= (:order ordering) :asc) (compare item1-prop item2-prop)
                  :else (compare item2-prop item1-prop))))
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

(rf/reg-sub ::outfits-ordering
  (fn [db [_ outfit-type]]
    (get-in db [:settings outfit-type :ordering])))

(rf/reg-sub ::outfit-names
  (fn [[_ outfit-type]]
    [(rf/subscribe [::outfits])
     (rf/subscribe [::outfits-ordering outfit-type])])
  (fn [[outfits ordering] [_ outfit-type]]
    (->> (vals outfits)
         (remove :deprecated?)
         (filter (get-in outfits/types [outfit-type :filter]))
         (sort-with-settings (outfits/columns-for outfit-type) ordering)
         (map :name))))

(rf/reg-sub ::game-version
  (fn [db]
    (:version db)))

(rf/reg-sub ::attribute-minimums
  (fn [db]
    (:attribute-minimums db)))

(rf/reg-sub ::configuration
  (fn [db]
    (configurator/configuration db)))

(rf/reg-sub ::saved-configurations
  (fn [db]
    (->> (:saved-configurations db)
         (sort-by :saved-at)
         reverse)))

(rf/reg-sub ::storage-available?
  (fn [db]
    (:storage-available? db)))

;; the latest saved configuration with the same ship and outfits as the open one
(rf/reg-sub ::current-saved-configuration
  (fn [db]
    (when (some? (configurator/configuration db))
      (let [current-url (configurator/explicit-url db)]
        (->> (:saved-configurations db)
             (filter #(configurator/same-configuration? (:url %) current-url))
             (sort-by :saved-at)
             last)))))

(rf/reg-sub ::configurator-not-found
  (fn [db]
    (configurator/not-found db)))

(rf/reg-sub ::configurator-settings
  (fn [db]
    (get-in db [:settings :configurator])))

(rf/reg-sub ::sold-outfit-names
  (fn [db]
    (->> (:outfitters db)
         (mapcat :outfits)
         set)))

(defn- outfit-attributes [outfits outfit-name]
  (get-in outfits [(kebabize outfit-name) :attributes]))

(rf/reg-sub ::configurator-attributes
  (fn []
    [(rf/subscribe [::configuration])
     (rf/subscribe [::outfits])])
  (fn [[{:keys [ship outfits]} all-outfits]]
    (when (some? ship)
      (c/ship-attributes ship
                         outfits
                         (partial outfit-attributes all-outfits)))))

(rf/reg-sub ::configurator-violations
  (fn []
    [(rf/subscribe [::attribute-minimums])
     (rf/subscribe [::configurator-attributes])])
  (fn [[minimums attributes]]
    (c/violations minimums attributes)))

(defn- quantities [outfits]
  (->> outfits
       (map (juxt :name :quantity))
       (into {})))

(rf/reg-sub ::configurator-stock?
  (fn []
    (rf/subscribe [::configuration]))
  (fn [{:keys [ship outfits]}]
    (= (quantities outfits)
       (quantities (:outfits ship)))))

(rf/reg-sub ::configurator-cost
  (fn []
    [(rf/subscribe [::configuration])
     (rf/subscribe [::outfits])])
  (fn [[{:keys [ship outfits]} all-outfits]]
    (let [hull (get ship :empty-hull-cost 0)
          outfits-cost (->> outfits
                            (map (fn [{:keys [name quantity]}]
                                   (* (get-in all-outfits [(kebabize name) :cost] 0)
                                      quantity)))
                            (reduce + 0))]
      {:hull hull
       :outfits outfits-cost
       :total (+ hull outfits-cost)})))

(def ^:private main-resources
  ["outfit space" "weapon capacity" "engine capacity" "gun ports" "turret mounts"])

(def ^:private hidden-resources
  "Limited attributes that make no sense as resources (still reported as violations)."
  #{"installable" "required crew" "heat dissipation"})

(rf/reg-sub ::configurator-resources
  (fn []
    [(rf/subscribe [::configuration])
     (rf/subscribe [::outfits])
     (rf/subscribe [::attribute-minimums])
     (rf/subscribe [::configurator-attributes])])
  (fn [[{:keys [ship outfits]} all-outfits minimums attributes]]
    (let [hull (c/hull-attributes ship)
          installed (map (fn [{:keys [name quantity]}]
                           {:attributes (outfit-attributes all-outfits name)
                            :quantity quantity})
                         outfits)
          consumed (->> installed
                        (mapcat (fn [{:keys [attributes]}]
                                  (for [[attr-name value] attributes
                                        ;; only attributes with the default minimum of 0
                                        ;; work like a resource that outfits use up
                                        :when (and (neg? value)
                                                   (not (contains? minimums attr-name)))]
                                    attr-name)))
                        (remove (set main-resources))
                        (remove hidden-resources)
                        distinct
                        sort)]
      (->> (concat main-resources consumed)
           (map (fn [attr-name]
                  (let [added (->> installed
                                   (map (fn [{:keys [attributes quantity]}]
                                          (* (max 0 (get attributes attr-name 0))
                                             quantity)))
                                   (reduce + 0))
                        capacity (+ (c/value hull attr-name)
                                    added)
                        free (c/value attributes attr-name)]
                    {:attribute attr-name
                     :capacity capacity
                     :used (- capacity free)
                     :free free})))))))

(def ^:private unlimited
  "Quantity used to find out how many instances of an outfit can be added."
  1000000)

(defn- max-addable [minimums attributes outfit]
  (max 0
       (c/can-add minimums
                  attributes
                  (:attributes outfit)
                  unlimited)))

(rf/reg-sub ::configurator-installed
  (fn []
    [(rf/subscribe [::configuration])
     (rf/subscribe [::outfits])
     (rf/subscribe [::attribute-minimums])
     (rf/subscribe [::configurator-attributes])])
  (fn [[{:keys [outfits]} all-outfits minimums attributes]]
    (->> outfits
         (map (fn [{:keys [name quantity]}]
                (let [outfit (get all-outfits (kebabize name))]
                  {:name name
                   :category (:category outfit)
                   :quantity quantity
                   :can-add? (pos? (max-addable minimums attributes outfit))})))
         (group-by :category))))

(defn- installable? [outfit]
  (not (neg? (get-in outfit [:attributes "installable"] 0))))

(defn- addable-outfit? [outfit]
  (and (not (:deprecated? outfit))
       (installable? outfit)))

(def ^:private non-installed-categories
  "Outfit categories of items that are bought but never installed on a ship."
  #{"Licenses"})

(rf/reg-sub ::outfit-categories
  (fn [db]
    (:outfit-categories db)))

(rf/reg-sub ::configurator-categories
  (fn []
    [(rf/subscribe [::outfits])
     (rf/subscribe [::outfit-categories])
     (rf/subscribe [::configurator-installed])])
  (fn [[all-outfits outfit-categories installed]]
    (let [addable-categories (->> (vals all-outfits)
                                  (filter addable-outfit?)
                                  (map :category)
                                  set)
          known-categories (->> outfit-categories
                                (remove non-installed-categories)
                                (filter #(or (contains? addable-categories %)
                                             (contains? installed %))))]
      ;; installed outfits of other categories (if any) go last, they can't be added
      (->> (keys installed)
           (remove (set known-categories))
           (map (fn [category]
                  {:category category
                   :addable? false}))
           (concat (map (fn [category]
                          {:category category
                           :addable? (contains? addable-categories category)})
                        known-categories))))))

(rf/reg-sub ::configurator-catalog
  (fn []
    [(rf/subscribe [::outfits])
     (rf/subscribe [::attribute-minimums])
     (rf/subscribe [::configurator-attributes])
     (rf/subscribe [::configurator-settings])
     (rf/subscribe [::sold-outfit-names])])
  (fn [[all-outfits minimums attributes {:keys [search only-sold?]} sold-outfit-names]
       [_ category]]
    (let [search (str/lower-case (str/trim search))
          category-outfits (->> (vals all-outfits)
                                (filter #(= (:category %) category))
                                (filter addable-outfit?))
          ;; types come from the whole category, so groups don't change while searching
          primary-types (outfits/primary-types category-outfits)]
      (->> category-outfits
           (filter #(str/includes? (str/lower-case (:name %)) search))
           (filter #(or (not only-sold?)
                        (contains? sold-outfit-names (:name %))))
           (group-by #(get primary-types (:name %)))
           ;; groups follow the order of types on the outfits page, untyped outfits go last
           (sort-by (fn [[type]]
                      (if (some? type)
                        (.indexOf (vec (keys outfits/types)) type)
                        js/Infinity)))
           (map (fn [[type type-outfits]]
                  {:type type
                   :header (get-in outfits/types [type :header])
                   :outfits (->> (if (some? type)
                                   (outfits/sort-by-initial-ordering type type-outfits)
                                   (sort-by :name type-outfits))
                                 (map (fn [outfit]
                                        (let [addable (max-addable minimums attributes outfit)]
                                          {:name (:name outfit)
                                           :cost (:cost outfit)
                                           :licenses (:licenses outfit)
                                           :stats (outfits/key-stats outfit type)
                                           :max (when (< addable unlimited)
                                                  addable)
                                           :can-add? (pos? addable)}))))}))))))

(rf/reg-sub ::configurator-stats
  (fn []
    [(rf/subscribe [::configuration])
     (rf/subscribe [::outfits])
     (rf/subscribe [::configurator-attributes])])
  (fn [[{:keys [ship outfits]} all-outfits attributes]]
    (c/configuration-stats ship
                           outfits
                           attributes
                           #(get all-outfits (kebabize %)))))
