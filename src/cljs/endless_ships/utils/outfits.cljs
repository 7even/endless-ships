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
                                             "Turn. heat"     {:value :turning-heat})}
             :reactors {:header "Reactors"
                        :filter #(or (contains? % :energy-generation)
                                     (contains? % :solar-collection))
                        :initial-ordering {:column-name "Energy per space"
                                           :order :desc}
                        :columns (let [energy-generation #(+ (get % :energy-generation 0)
                                                             (get % :solar-collection 0))]
                                   (array-map "Cost"              {:value :cost}
                                              "Outfit sp."        {:value :outfit-space}
                                              "Energy generation" {:value energy-generation}
                                              "Energy per space"  {:value #(/ (energy-generation %)
                                                                              (:outfit-space %))}
                                              "Heat generation"   {:value :heat-generation}))}
             :batteries {:header "Batteries"
                         :filter #(and (contains? % :energy-capacity)
                                       (#{"Power" "Systems"} (:category %)))
                         :initial-ordering {:column-name "Energy per space"
                                            :order :desc}
                         :columns (array-map "Cost"             {:value :cost}
                                             "Outfit sp."       {:value :outfit-space}
                                             "Energy capacity"  {:value :energy-capacity}
                                             "Energy per space" {:value #(/ (:energy-capacity %)
                                                                            (:outfit-space %))})}
             :coolers {:header "Coolers"
                       :filter #(or (contains? % :cooling)
                                    (contains? % :active-cooling))
                       :initial-ordering {:column-name "Cooling per space"
                                          :order :desc}
                       :columns (let [total-cooling #(+ (get % :cooling 0)
                                                        (get % :active-cooling 0))]
                                  (array-map "Cost"              {:value :cost}
                                             "Outfit sp."        {:value :outfit-space}
                                             "Cooling"           {:value total-cooling}
                                             "Cooling per space" {:value #(/ (total-cooling %)
                                                                             (:outfit-space %))}
                                             "Cooling energy"    {:value :cooling-energy}))}))

(defn columns-for [type]
  (->> (conj (get-in types [type :columns])
             ["Licenses" {:value :licenses
                          :orderable? false}])
       (cons ["Name" {:value :name}])
       (into (array-map))))
