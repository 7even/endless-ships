(ns endless-ships.configurator-test
  (:require [clojure.test :refer [deftest is testing]]
            [endless-ships.configurator :as c]))

(def minimums
  {"energy generation" nil
   "shield protection" -0.99})

(def ship
  {:attributes {"outfit space" 100
                "weapon capacity" 30
                "required crew" 2}
   :guns 2
   :turrets 0
   :category "Light Warship"})

(def laser
  {"outfit space" -10
   "weapon capacity" -10
   "gun ports" -1
   "energy generation" -0.5})

(defn- hull []
  (c/hull-attributes ship))

(deftest hull-attributes-test
  (testing "gun ports and turret mounts are counted from hardpoints"
    (is (= (get (hull) "gun ports")
           20000))
    (is (= (get (hull) "turret mounts")
           0)))
  (testing "drones are automatons"
    (is (= (get (c/hull-attributes {:category "Drone"}) "automaton")
           10000))
    (is (nil? (get (hull) "automaton")))))

(deftest can-add-test
  (testing "limited by the scarcest attribute"
    (is (= (c/can-add minimums (hull) laser 1)
           1))
    (is (= (c/can-add minimums (hull) laser 5)
           2)))
  (testing "nothing can be added when a resource is exhausted"
    (let [full (c/add-outfit (hull) laser 2)]
      (is (= (c/can-add minimums full laser 1)
             0))))
  (testing "attributes with no minimum may go negative"
    (is (= (c/can-add minimums (hull) {"energy generation" -100} 3)
           3)))
  (testing "attributes with a custom minimum"
    (is (= (c/can-add minimums (hull) {"shield protection" -0.5} 1)
           1))
    (is (= (c/can-add minimums (hull) {"shield protection" -1} 1)
           0)))
  (testing "only automatons may have no required crew"
    (is (= (c/can-add minimums (hull) {"required crew" -2} 1)
           0))
    (is (= (c/can-add minimums (hull) {"required crew" -1} 1)
           1))
    (is (= (c/can-add minimums
                      (hull)
                      {"required crew" -2
                       "automaton" 1}
                      1)
           1)))
  (testing "non-installable items never fit"
    (is (= (c/can-add minimums (hull) {"installable" -1} 1)
           0))))

(deftest ship-attributes-test
  (let [attributes (c/ship-attributes ship
                                      [{:name "Laser"
                                        :quantity 2}]
                                      {"Laser" laser})]
    (is (= (get attributes "outfit space")
           800000))
    (is (= (get attributes "gun ports")
           0))
    (is (= (get attributes "energy generation")
           -10000))))

(deftest violations-test
  (testing "a valid configuration has no violations"
    (is (empty? (c/violations minimums
                              (c/add-outfit (hull) laser 2)))))
  (testing "overloaded attributes are reported in normal units"
    (is (= (c/violations minimums
                         (c/add-outfit (hull) laser 3))
           {"gun ports" {:value -1.0
                         :minimum 0.0}}))
    (is (= (c/violations minimums
                         (c/add-outfit (hull) laser 4))
           {"weapon capacity" {:value -10.0
                               :minimum 0.0}
            "gun ports" {:value -2.0
                         :minimum 0.0}}))))

(deftest precise-floats-test
  (let [precise #(get (c/add-outfit {} {"x" %} 1)
                      "x")]
    (testing "single-precision floats from the parser are converted like the game's doubles"
      (is (= (precise (float 0.42))
             4200)))
    (testing "numbers are built from their digits like the game parses them"
      ;; 0.57 * 10000 is 5699.999…, but the game gets 57 * 10^-2 * 10000 = 5700.000…1
      (is (= (map precise [0.57 1.13 -0.57 2.01])
             [5700 11300 -5700 20100])))
    (testing "integers and exponents"
      (is (= (map precise [3 -12 1.0E7 1.5E-3])
             [30000 -120000 100000000000 15])))))

(defn- stats [attributes mass]
  (c/ship-stats (c/add-outfit {} attributes 1)
                mass
                {:energy 30
                 :heat 60}))

(deftest ship-stats-test
  (testing "shields and hull with multipliers and regeneration per second"
    (let [result (stats {"shields" 1000
                         "shield multiplier" 0.5
                         "shield generation" 2
                         "hull" 500}
                        100)]
      (is (= (:shields result)
             1500.0))
      (is (= (:shield-regen result)
             120.0))
      (is (= (:hull result)
             500.0))))
  (testing "movement depends on mass, drag is limited by the mass"
    (let [result (stats {"thrust" 10
                         "drag" 2
                         "turn" 300
                         "cargo space" 100}
                        100)]
      (is (= (:max-speed result)
             300.0))
      (is (= (:acceleration result)
             [180.0 360.0]))
      (is (= (:turning result)
             [90.0 180.0])))
    (is (= (:max-speed (stats {"thrust" 10
                               "drag" 1000}
                              100))
           6.0)))
  (testing "energy and heat balance"
    (let [result (stats {"energy generation" 2
                         "energy consumption" 0.5
                         "heat generation" 1
                         "cooling" 0.5
                         "thrusting energy" 1
                         "thrust" 1}
                        100)]
      (is (= (map (juxt :label :energy) (:energy-heat result))
             [["idle" 90.0]
              ["thrusting" -60.0]
              ["turning" 0.0]
              ["firing" -30]
              ["charging shields" 0.0]]))
      (is (= (:net-change result)
             {:energy 0.0
              :heat 90.0}))))
  (testing "a ship without heat dissipation overheats if it generates heat"
    (is (:overheating? (stats {"heat generation" 1}
                              100)))
    (is (not (:overheating? (stats {"heat generation" 1
                                    "heat dissipation" 1}
                                   100)))))
  (testing "only automatons may need no crew"
    (is (= (:required-crew (stats {} 100))
           1.0))
    (is (= (:required-crew (stats {"automaton" 1} 100))
           0.0))))

(deftest configuration-stats-test
  (let [outfits {"Blaster" {:mass 5
                            :weapon {:shield-damage {:per-second 10.0}
                                     :hull-damage {:per-second 6.0}
                                     :firing-energy {:per-second 3.0}}}
                 "Ion Cannon" {:mass 3
                               :weapon {:ion-damage {:per-second 1.5}}}
                 "Anti-Missile" {:mass 2
                                 :weapon {:anti-missile 8}}}
        result (c/configuration-stats {:mass 100}
                                      [{:name "Blaster"
                                        :quantity 2}
                                       {:name "Ion Cannon"
                                        :quantity 1}
                                       {:name "Anti-Missile"
                                        :quantity 1}]
                                      (c/add-outfit {} {} 1)
                                      outfits)]
    (testing "outfit masses are added to the hull's mass"
      (is (= (:mass result)
             115)))
    (testing "damage is summed over all weapons by type"
      (is (= (:damage result)
             {:shield-damage 20.0
              :hull-damage 12.0
              :heat-damage 0
              :ion-damage 1.5
              :disruption-damage 0
              :slowing-damage 0})))
    (testing "firing energy is summed over all weapons"
      (is (= (->> (:energy-heat result)
                  (filter #(= (:label %) "firing"))
                  first
                  :energy)
             -6.0)))))
