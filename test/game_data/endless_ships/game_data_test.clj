(ns endless-ships.game-data-test
  "Tests on the real game data. Reference ships check that our reimplementation of the
  game logic still matches the game after updating it."
  (:require [clojure.test :refer [deftest is testing]]
            [endless-ships.attributes :refer [attribute-minimums]]
            [endless-ships.configurator :as c]
            [endless-ships.core :refer [modifications-data outfits-data ships-data]]))

(def ^:private ships-by-name
  (->> ships-data
       (map (juxt :name identity))
       (into {})))

(def ^:private outfits-by-name
  (->> outfits-data
       (map (juxt :name identity))
       (into {})))

(defn- ship
  "Returns a ship (or its modification merged with the base ship) like the site shows it."
  ([ship-name]
   (get ships-by-name ship-name))
  ([ship-name modification-name]
   (->> modifications-data
        (filter #(and (= (:name %) ship-name)
                      (= (:modification %) modification-name)))
        first
        (merge (ship ship-name)))))

(defn- stock-attributes [ship]
  (c/ship-attributes ship
                     (:outfits ship)
                     #(get-in outfits-by-name [% :attributes])))

(deftest stock-outfits-exist-test
  (let [configurations (concat ships-data
                               (->> modifications-data
                                    (filter #(contains? ships-by-name (:name %)))
                                    (map #(merge (ships-by-name (:name %)) %))))
        missing (->> configurations
                     (mapcat (fn [{:keys [name modification outfits]}]
                               (->> outfits
                                    (map :name)
                                    (remove #(contains? outfits-by-name %))
                                    (map (fn [outfit-name]
                                           {:outfit outfit-name
                                            :ship name
                                            :modification modification})))))
                     (sort-by (juxt :outfit :ship)))]
    (is (= missing []))))

(deftest reference-ships-test
  (testing "stock Falcon fits"
    (is (empty? (c/violations attribute-minimums
                              (stock-attributes (ship "Falcon"))))))
  (testing "add attributes of a variant are added to the base ship's attributes"
    (is (empty? (c/violations attribute-minimums
                              (stock-attributes (ship "Carrier" "Carrier (Alpha)"))))))
  (testing "hardpoints without coordinates are inherited from the base ship"
    (is (empty? (c/violations attribute-minimums
                              (stock-attributes (ship "Tubfalet" "Tubfalet (Crippler)"))))))
  (testing "stock Korath Raider is overloaded with missiles (checked in the game)"
    (let [attributes (stock-attributes (ship "Korath Raider"))
          missile (get-in outfits-by-name ["Firelight Missile" :attributes])]
      (is (= (c/violations attribute-minimums attributes)
             {"firelight missile capacity" {:value -8.0
                                            :minimum 0.0}}))
      (testing "and a sold missile can't be bought back"
        ;; like in the game, can-add is negative for an already overloaded ship
        (is (not (pos? (c/can-add attribute-minimums
                                  (c/add-outfit attributes missile -1)
                                  missile
                                  1))))))))
