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
  (testing "mass in add attributes of a variant is added to the base ship's mass"
    (is (= (- (:mass (ship "Schist" "Schist (Heavy Load)"))
              (:mass (ship "Schist")))
           57))
    (is (= (- (:mass (ship "Deep River" "Deep River 0"))
              (:mass (ship "Deep River")))
           -620)))
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

(defn- round
  "Cuts off decimals the way the game's Format::Number shows numbers: 2 decimal places below
  1,000, 1 below 10,000 and none above; extra digits are truncated, not rounded."
  [x]
  (let [places (cond
                 (>= (Math/abs (double x)) 10000) 0
                 (>= (Math/abs (double x)) 1000) 1
                 :else 2)
        scale (Math/pow 10 places)]
    (/ (* (Math/signum (double x))
          (Math/floor (+ (* (Math/abs (double x)) scale)
                         1e-10)))
       scale)))

(deftest reference-stats-test
  ;; values from the game's info display of a stock Falcon (game v0.11.3)
  (let [falcon (ship "Falcon")
        stats (c/configuration-stats falcon
                                     (:outfits falcon)
                                     (stock-attributes falcon)
                                     outfits-by-name)
        rows (->> (:energy-heat stats)
                  (map (juxt :label identity))
                  (into {}))]
    (is (= (->> [:shields :shield-regen :hull :mass :cargo-space :required-crew :bunks
                 :fuel-capacity :max-speed]
                (map (juxt identity #(round (get stats %))))
                (into {}))
           {:shields 12800.0
            :shield-regen 30.6
            :hull 3700.0
            :mass 1810.0
            :cargo-space 130.0
            :required-crew 55.0
            :bunks 91.0
            :fuel-capacity 600.0
            :max-speed 367.89}))
    (is (= (map round (:acceleration stats))
           [129.71 139.02]))
    (is (= (map round (:turning stats))
           [52.56 56.33]))
    (testing "the game shows thrusting and turning together as moving"
      (is (= (round (+ (get-in rows ["thrusting" :energy])
                       (get-in rows ["turning" :energy])))
             -288.0))
      (is (= (round (+ (get-in rows ["thrusting" :heat])
                       (get-in rows ["turning" :heat])))
             636.0)))
    (is (= (->> ["idle" "firing" "charging shields"]
                (map (fn [label]
                       [label
                        (round (get-in rows [label :energy]))
                        (round (get-in rows [label :heat]))])))
           [["idle" 846.0 360.0]
            ["firing" -649.6 2366.0]
            ["charging shields" -30.6 0.0]]))
    (is (= (map round ((juxt :energy :heat) (:capacity stats)))
           [19200.0 4561.2]))))
