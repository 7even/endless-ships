(ns endless-ships.core
  (:require [clojure.java.io :refer [file resource]]
            [clojure.string :as str]
            [instaparse.core :as insta])
  (:import [java.lang Float Integer]))

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

(defn transform-ship [& ship-params]
  (let [licenses (->> ship-params
                      (first-with-key :licenses)
                      (map #(get % 1))
                      vec)
        all-attributes (first-with-key :attributes ship-params)
        weapon (->> all-attributes
                    (first-with-key :weapon)
                    (map (comp vec rest))
                    (into {}))
        other-attributes (->> all-attributes
                              (all-without-key :weapon)
                              (into {}))
        outfits (->> ship-params
                     (first-with-key :outfits)
                     (map (fn [[_ name quantity]]
                            [name (or quantity 1)]))
                     (into {}))
        other (->> ship-params
                   (all-with-key :any-detail)
                   (reduce (fn [details [detail-name & detail]]
                             (update details
                                     detail-name
                                     #(conj (or % []) (vec detail))))
                           {}))]
    {:ship-name (first-with-key :ship-name ship-params)
     :sprite (first-with-key :sprite ship-params)
     :licenses licenses
     :attributes (merge {:weapon weapon} other-attributes)
     :outfits outfits
     :other other}))

(def transform-options
  {:ship transform-ship
   :string identity
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
       (map (fn [file]
              (let [filename (.getName file)
                    race (if (= filename "ships.txt")
                           "human"
                           (-> filename (str/split #" ") first))
                    ships (-> file
                              slurp
                              parse
                              rest)]
                (map #(assoc % :race race) ships))))
       (apply concat)))
