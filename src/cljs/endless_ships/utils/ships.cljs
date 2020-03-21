(ns endless-ships.utils.ships)

(defn total-cost [{:keys [empty-hull-cost outfits-cost]}]
  (+ empty-hull-cost outfits-cost))

(defn or-zero [prop]
  #(or (prop %)
       0))

(def columns
  (array-map "Name"         {:value :name}
             "Race"         {:value :race}
             "Cost"         {:value total-cost}
             "Category"     {:value :category
                             :orderable? false}
             "Hull"         {:value (or-zero :hull)}
             "Shields"      {:value (or-zero :shields)}
             "Mass"         {:value (or-zero :mass)}
             "Engine cap."  {:value (or-zero :engine-capacity)}
             "Weapon cap."  {:value (or-zero :weapon-capacity)}
             "Fuel cap."    {:value (or-zero :fuel-capacity)}
             "Outfit sp."   {:value (or-zero :outfit-space)}
             "Cargo sp."    {:value (or-zero :cargo-space)}
             "Crew / bunks" {:value (or-zero :bunks)}
             "Licenses"     {:value :licenses}))
