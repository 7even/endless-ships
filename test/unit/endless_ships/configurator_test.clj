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
