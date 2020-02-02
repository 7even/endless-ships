(ns endless-ships.ships
  (:require [clojure.set :refer [rename-keys]]
            [endless-ships.parser :refer [->map data]]))

(defn- add-key-if [cond key value]
  (if cond
    {key value}
    {}))

(defn- process-ship [[_
                      [ship-name ship-modification]
                      {[[[sprite] animation]] "sprite"
                       [[_ {[[_ license-attrs]] "licenses"
                            [[_ weapon-attrs]] "weapon"
                            :as attrs}]] "attributes"
                       [[_ outfit-attrs]] "outfits"
                       gun-points "gun"
                       turret-points "turret"
                       drone-points "drone"
                       fighter-points "fighter"
                       description-attrs "description"
                       file "file"
                       :as ship}]]
  (merge (->map attrs)
         {:name ship-name
          :modification ship-modification
          :weapon (->map weapon-attrs)
          :file file}
         (add-key-if (contains? ship "sprite")
                     :sprite
                     [sprite (not (empty? animation))])
         (add-key-if (contains? attrs "licenses")
                     :licenses
                     (-> license-attrs keys vec))
         (add-key-if (contains? ship "outfits")
                     :outfits
                     (map (fn [[outfit-name [[[quantity]]]]]
                            {:name outfit-name
                             :quantity (or quantity 1)})
                          outfit-attrs))
         (add-key-if (> (count gun-points) 0)
                     :guns
                     (count gun-points))
         (add-key-if (> (count turret-points) 0)
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
                          vec))))

(def ships
  (->> data
       (filter #(and (= (first %) "ship")
                     (= (-> % second count) 1)
                     (not= (second %) ["Unknown Ship Type"])))
       (map #(-> %
                 process-ship
                 (dissoc :modification)))))

(def modifications
  (->> data
       (filter #(and (= (first %) "ship")
                     (= (-> % second count) 2)))
       (map process-ship)))
