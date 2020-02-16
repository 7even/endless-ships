(ns endless-ships.utils.outfits)

(defn- thruster-effectiveness [thruster]
  (/ (:thrust thruster)
     (:outfit-space thruster)))

(def columns
  {:thrusters (array-map "Name"             :name
                         "Cost"             :cost
                         "Outfit sp."       :outfit-space
                         "Thrust"           :thrust
                         "Thrust per space" thruster-effectiveness
                         "Thr. energy"      :thrusting-energy
                         "Thr. heat"        :thrusting-heat
                         "Licenses"         nil)})
