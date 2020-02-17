(ns endless-ships.utils.outfits)

(def types
  (array-map :thrusters {:header "Thrusters"
                         :filter #(contains? % :thrust)
                         :initial-ordering {:column-name "Thrust per space"
                                            :order :desc}
                         :columns (array-map "Outfit sp."       {:value :outfit-space}
                                             "Thrust"           {:value :thrust}
                                             "Thrust per space" {:value #(/ (:thrust %)
                                                                            (:outfit-space %))}
                                             "Thr. energy"      {:value :thrusting-energy}
                                             "Thr. heat"        {:value :thrusting-heat})}
             :steerings {:header "Steerings"
                         :filter #(contains? % :turn)
                         :initial-ordering {:column-name "Turn per space"
                                            :order :desc}
                         :columns (array-map "Outfit sp."     {:value :outfit-space}
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
                                   (array-map "Outfit sp."        {:value :outfit-space}
                                              "Energy generation" {:value energy-generation}
                                              "Energy per space"  {:value #(/ (energy-generation %)
                                                                              (:outfit-space %))}
                                              "Heat generation"   {:value :heat-generation}))}
             :batteries {:header "Batteries"
                         :filter #(and (contains? % :energy-capacity)
                                       (#{"Power" "Systems"} (:category %)))
                         :initial-ordering {:column-name "Energy per space"
                                            :order :desc}
                         :columns (array-map "Outfit sp."       {:value :outfit-space}
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
                                  (array-map "Outfit sp."        {:value :outfit-space}
                                             "Cooling"           {:value total-cooling}
                                             "Cooling per space" {:value #(/ (total-cooling %)
                                                                             (:outfit-space %))}
                                             "Cooling energy"    {:value :cooling-energy}))}
             :shields {:header "Shield generators"
                       :filter #(contains? % :shield-generation)
                       :initial-ordering {:column-name "Shield per space"
                                          :order :desc}
                       :columns (array-map "Outfit sp."        {:value :outfit-space}
                                           "Shield generation" {:value :shield-generation}
                                           "Shield per space"  {:value #(/ (:shield-generation %)
                                                                           (:outfit-space %))}
                                           "Shield energy"     {:value :shield-energy}
                                           "Shield heat"       {:value :shield-heat})}
             :hull-repair {:header "Hull repair modules"
                           :filter #(contains? % :hull-repair-rate)
                           :initial-ordering {:column-name "Hull per space"
                                              :order :desc}
                           :columns (array-map "Outfit sp."       {:value :outfit-space}
                                               "Hull repair rate" {:value :hull-repair-rate}
                                               "Hull per space"   {:value #(/ (:hull-repair-rate %)
                                                                              (:outfit-space %))}
                                               "Hull energy"      {:value :hull-energy}
                                               "Hull heat"        {:value :hull-heat})}
             :ramscoops {:header "Ramscoops"
                         :filter #(contains? % :ramscoop)
                         :initial-ordering {:column-name "Ramscoop per space"
                                            :order :desc}
                         :columns (array-map "Outfit sp."         {:value :outfit-space}
                                             "Ramscoop"           {:value :ramscoop}
                                             "Ramscoop per space" {:value #(/ (:ramscoop %)
                                                                              (:outfit-space %))})}))

(defn columns-for [type]
  (->> (conj (get-in types [type :columns])
             ["Licenses" {:value :licenses
                          :orderable? false}])
       (concat [["Name" {:value :name}]
                ["Cost" {:value :cost}]])
       (into (array-map))))
