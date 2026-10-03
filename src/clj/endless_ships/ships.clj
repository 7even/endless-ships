(ns endless-ships.ships
  (:require [camel-snake-kebab.core :refer [->kebab-case-keyword]]
            [clojure.set :refer [rename-keys]]
            [endless-ships.attributes :refer [->attributes]]
            [endless-ships.images :refer [image-file]]
            [endless-ships.parser :refer [->map data]]))

(defn- add-key-if [cond key value]
  (if cond
    {key value}
    {}))

(defn- process-ship [[_
                      [ship-name ship-modification]
                      {[[[sprite]]] "sprite"
                       [[_ {[[_ license-attrs]] "licenses"
                            [[_ weapon-attrs]] "weapon"
                            :as attrs}]] "attributes"
                       add-blocks "add"
                       outfit-blocks "outfits"
                       gun-points "gun"
                       turret-points "turret"
                       drone-points "drone"
                       fighter-points "fighter"
                       description-attrs "description"
                       file "file"
                       :as ship}]]
  (let [sprite-file (some-> sprite image-file)
        ;; hardpoints of a ship variant without coordinates only assign weapons to
        ;; the base ship's hardpoints, so their number comes from the base ship
        ;; (see Ship::FinishLoading in the game sources)
        own-hardpoints? (some #(number? (ffirst %))
                              (concat gun-points turret-points))]
    (merge (->map attrs)
           {:name ship-name
            :modification ship-modification
            :weapon (->map weapon-attrs)
            :file file}
           ;; a ship variant with its own attributes replaces all attributes of the base ship
           (add-key-if (contains? ship "attributes")
                       :attributes
                       (->attributes attrs))
           (add-key-if (some #(= (first %) ["attributes"]) add-blocks)
                       :added-attributes
                       (->> add-blocks
                            (filter #(= (first %) ["attributes"]))
                            (map (comp ->attributes second))
                            (apply merge)))
           (add-key-if (some? sprite-file)
                       :sprite
                       sprite-file)
           (add-key-if (contains? attrs "licenses")
                       :licenses
                       (-> license-attrs keys vec))
           ;; like the game, sum up outfits from all `outfits` blocks
           (add-key-if (contains? ship "outfits")
                       :outfits
                       (->> outfit-blocks
                            (map second)
                            (apply merge-with into)
                            (map (fn [[outfit-name occurrences]]
                                   {:name outfit-name
                                    :quantity (->> occurrences
                                                   (map (fn [[[quantity]]]
                                                          (or quantity 1)))
                                                   (reduce +))}))))
           (add-key-if (and own-hardpoints?
                            (> (count gun-points) 0))
                       :guns
                       (count gun-points))
           (add-key-if (and own-hardpoints?
                            (> (count turret-points) 0))
                       :turrets
                       (count turret-points))
           (add-key-if (> (count drone-points) 0)
                       :drones
                       (count drone-points))
           (add-key-if (> (count fighter-points) 0)
                       :fighters
                       (count fighter-points))
           (add-key-if (contains? ship "description")
                       :description
                       (->> description-attrs
                            (map #(get-in % [0 0]))
                            vec)))))

(def ships
  (->> data
       (filter #(and (= (first %) "ship")
                     (= (-> % second count) 1)
                     (not= (second %) ["Unknown Ship Type"])))
       (map #(-> (process-ship %)
                 (dissoc :modification)))))

(defn- add-attributes
  "Adds `add attributes` of a ship variant to the attributes it has or inherits from
  the base ship, like the game does in Ship::FinishLoading. Displayed attributes
  (kebab-case keys) are updated as well."
  [{:keys [added-attributes]
    :as modification} base]
  (let [own-attributes? (contains? modification :attributes)
        attributes (if own-attributes?
                     (:attributes modification)
                     (:attributes base))]
    (reduce-kv (fn [ship attr-name value]
                 (let [k (->kebab-case-keyword attr-name)
                       current (get (if own-attributes?
                                      modification
                                      base)
                                    k
                                    0)]
                   (assoc ship
                          k
                          (+ current value))))
               (-> modification
                   (dissoc :added-attributes)
                   (assoc :attributes (merge-with + attributes added-attributes)))
               added-attributes)))

(def modifications
  (let [ships-by-name (->> ships
                           (map (juxt :name identity))
                           (into {}))]
    (->> data
         (filter #(and (= (first %) "ship")
                       (= (-> % second count) 2)))
         (map process-ship)
         (map (fn [modification]
                (if (contains? modification :added-attributes)
                  (add-attributes modification (ships-by-name (:name modification)))
                  modification))))))
