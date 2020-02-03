(ns endless-ships.utils.ships)

(defn total-cost [{:keys [empty-hull-cost outfits-cost]}]
  (+ empty-hull-cost outfits-cost))

(defn or-zero [prop]
  #(or (prop %)
       0))

(def columns
  (array-map "Name"         :name
             "Race"         nil
             "Cost"         total-cost
             "Category"     nil
             "Hull"         (or-zero :hull)
             "Shields"      (or-zero :shields)
             "Mass"         (or-zero :mass)
             "Engine cap."  (or-zero :engine-capacity)
             "Weapon cap."  (or-zero :weapon-capacity)
             "Fuel cap."    (or-zero :fuel-capacity)
             "Outfit sp."   (or-zero :outfit-space)
             "Cargo sp."    (or-zero :cargo-space)
             "Crew / bunks" (or-zero :bunks)
             "Licenses"     nil))
