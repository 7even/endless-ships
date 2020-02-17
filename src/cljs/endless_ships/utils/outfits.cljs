(ns endless-ships.utils.outfits)

(def types
  (array-map :thrusters {:header "Thrusters"
                         :filter #(contains? % :thrust)
                         :initial-ordering {:column-name "Thrust per space"
                                            :order :desc}
                         :columns (array-map "Cost"             {:value :cost}
                                             "Outfit sp."       {:value :outfit-space}
                                             "Thrust"           {:value :thrust}
                                             "Thrust per space" {:value #(/ (:thrust %)
                                                                            (:outfit-space %))}
                                             "Thr. energy"      {:value :thrusting-energy}
                                             "Thr. heat"        {:value :thrusting-heat})}
             :steerings {:header "Steerings"
                         :filter #(contains? % :turn)
                         :initial-ordering {:column-name "Turn per space"
                                            :order :desc}
                         :columns (array-map "Cost"           {:value :cost}
                                             "Outfit sp."     {:value :outfit-space}
                                             "Turn"           {:value :turn}
                                             "Turn per space" {:value #(/ (:turn %)
                                                                          (:outfit-space %))}
                                             "Turn. energy"   {:value :turning-energy}
                                             "Turn. heat"     {:value :turning-heat})}))

(defn columns-for [type]
  (->> (conj (get-in types [type :columns])
             ["Licenses" {:value :licenses
                          :orderable? false}])
       (cons ["Name" {:value :name}])
       (into (array-map))))
