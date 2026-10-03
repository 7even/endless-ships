(ns endless-ships.utils.configurator
  "Configurator state kept in the URL: `/configurator/<ship>[/<modification>]?outfits=...`
  where outfits are dot-separated outfit slugs with an optional `*<quantity>` suffix,
  e.g. `hyperdrive.torpedo*60`. Without the `outfits` parameter the ship has its stock outfits."
  (:require [clojure.string :as str]
            [endless-ships.views.utils :refer [kebabize]]))

(defn- parse-quantity [s]
  (let [quantity (js/parseInt s 10)]
    (if (pos? quantity)
      quantity
      1)))

(defn- parse-outfits
  "Parses the `outfits` URL parameter into a sequence of [outfit-slug quantity]."
  [param]
  (->> (str/split param #"\.")
       (remove str/blank?)
       (map (fn [item]
              (let [[slug quantity] (str/split item #"\*" 2)]
                [slug (parse-quantity quantity)])))))

(defn- outfits-param [outfits]
  (->> outfits
       (map (fn [{:keys [name quantity]}]
              (str (js/encodeURIComponent (kebabize name))
                   (when (> quantity 1)
                     (str "*" quantity)))))
       (str/join ".")))

(defn url
  "Returns the configurator URL for a ship (and its modification) with the given outfits;
  nil outfits mean stock outfits."
  [ship-slug modification-slug outfits]
  (str "/configurator/"
       ship-slug
       (when (some? modification-slug)
         (str "/" modification-slug))
       (when (some? outfits)
         (str "?outfits=" (outfits-param outfits)))))

(defn- merge-duplicates
  "Sums quantities of outfits listed several times, keeping the order of first occurrences."
  [outfits]
  (let [quantities (reduce (fn [quantities {:keys [name quantity]}]
                             (update quantities name (fnil + 0) quantity))
                           {}
                           outfits)]
    (->> outfits
         (map :name)
         distinct
         (mapv (fn [outfit-name]
                 {:name outfit-name
                  :quantity (get quantities outfit-name)})))))

(defn configuration
  "Returns the configuration open in the configurator: the ship (merged with its modification),
  its outfits as a vector of {:name :quantity} and slugs of outfits from the URL that don't
  exist."
  [db]
  (let [[_ {ship-slug :ship/name
            modification-slug :ship/modification
            query :query}] (:route db)
        base (get-in db [:ships ship-slug])
        modification (when (some? modification-slug)
                       (get-in db [:ship-modifications ship-slug modification-slug]))
        ship (merge base modification)
        param (:outfits query)]
    (when (some? base)
      (if (some? param)
        (let [items (map (fn [[slug quantity]]
                           {:slug slug
                            :outfit (get-in db [:outfits slug])
                            :quantity quantity})
                         (parse-outfits param))]
          {:ship ship
           :outfits (->> items
                         (filter :outfit)
                         (map (fn [{:keys [outfit quantity]}]
                                {:name (:name outfit)
                                 :quantity quantity}))
                         merge-duplicates)
           :unknown-outfits (->> items
                                 (remove :outfit)
                                 (map :slug))})
        {:ship ship
         :outfits (vec (:outfits ship))
         :unknown-outfits []}))))

(defn change-quantity
  "Adds `delta` to the quantity of an outfit (appending it if it's not installed yet)
  and removes outfits with no quantity left."
  [outfits outfit-name delta]
  (let [installed? (some #(= (:name %) outfit-name) outfits)
        changed (if installed?
                  (map (fn [outfit]
                         (if (= (:name outfit) outfit-name)
                           (update outfit :quantity + delta)
                           outfit))
                       outfits)
                  (conj (vec outfits)
                        {:name outfit-name
                         :quantity delta}))]
    (->> changed
         (filter #(pos? (:quantity %)))
         vec)))
