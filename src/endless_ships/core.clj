(ns endless-ships.core
  (:require [clojure.java.io :refer [file resource]]
            [clojure.string :as str]
            [instaparse.core :as insta])
  (:import [java.lang Float Integer]))

(def transform-options
  {:string identity
   :integer #(Integer/parseInt %)
   :float #(Float/parseFloat (str/replace % "," "."))})

(defn parse [data]
  (let [parser (-> "parser.bnf"
                   resource
                   insta/parser)]
    (->> (parser data)
         (insta/transform transform-options))))

(def files
  "All files containing ships data."
  (->> "game/data"
       resource
       file
       file-seq
       (filter #(str/ends-with? % "ships.txt"))))

(def data
  (->> files
       (map (comp rest parse slurp))
       (apply concat)))

(defn all-with-key [key data]
  (->> data
       (filter #(= (first %) key))
       (map (comp vec rest))))

(defn first-with-key [key data]
  (first (all-with-key key data)))

(defn all-without-key [key data]
  (->> data
       (filter #(not= (first %) key))
       (map (comp vec rest))))

(defn transform-ship [[_ & ship-params]]
  (let [all-attributes (first-with-key :attributes ship-params)
        weapons (->> all-attributes
                     (first-with-key :weapon)
                     (map (comp vec rest))
                     (into {}))
        other-attributes (->> all-attributes
                              (all-without-key :weapon)
                              (into {}))]
    {:ship-name (-> (first-with-key :ship-name ship-params) first)
     :sprite (-> (first-with-key :sprite ship-params) first)
     :attributes (merge {:weapons weapons} other-attributes)}))

(comment (def desired
           {:ship-name "blabla"
            :sprite "blabla"
            :attributes {"category" "Heavy Warship"
                         "cost" 5900000
                         "shields" 160000
                         "hull" 50000
                         :weapons {"blast radius" 250
                                   "shield damage" 1000}}
            :outfits {"Quarg Skylance" 2
                      "Quarg Anti-Missile" 2}
            :description "The Wardragon is used by Quarg"
            :other {"engine" [[-14 47]
                              [14 47]]
                    "turret" [[-29 6 "Quarg Skylance"]
                              [29 6 "Quarg Skylance"]]
                    "explode" [["tiny explosion" 12]
                               ["small explosion" 16]]
                    "final explosion" "final explosion medium"}}))
