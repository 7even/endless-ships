(ns endless-ships.utils.outfits)

(defn- thruster-effectiveness [thruster]
  (/ (:thrust thruster)
     (:outfit-space thruster)))

(defn- steering-effectiveness [steering]
  (/ (:turn steering)
     (:outfit-space steering)))

(def columns
  {:thrusters (array-map "Name"             :name
                         "Cost"             :cost
                         "Outfit sp."       :outfit-space
                         "Thrust"           :thrust
                         "Thrust per space" thruster-effectiveness
                         "Thr. energy"      :thrusting-energy
                         "Thr. heat"        :thrusting-heat
                         "Licenses"         nil)
   :steerings (array-map "Name"           :name
                         "Cost"           :cost
                         "Outfit sp."     :outfit-space
                         "Turn"           :turn
                         "Turn per space" steering-effectiveness
                         "Turn. energy"   :turning-energy
                         "Turn. heat"     :turning-heat
                         "Licenses"       nil)})
