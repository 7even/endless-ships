(ns endless-ships.parser
  (:require [clojure.java.io :refer [file resource]]
            [clojure.string :as str]
            [instaparse.core :as insta])
  (:import [java.lang Float Integer]))

(def files
  "All files containing ships data."
  (->> "game/data"
       resource
       file
       file-seq
       (filter #(str/ends-with? % "ships.txt"))))

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

(defn transform-ship2 [ship-params]
  (let [name (vec (remove vector? ship-params))
        sprite (first-with-key "sprite" ship-params)
        licenses (->> ship-params
                      (first-with-key "licenses")
                      first)
        all-attributes (first-with-key "attributes" ship-params)
        weapon (->> all-attributes
                    (first-with-key "weapon")
                    (into {}))
        other-attributes (->> all-attributes
                              (filter #(not= (first %) "weapon"))
                              (into {}))
        outfits (->> ship-params
                     (first-with-key "outfits")
                     (map (fn [[name quantity]]
                            [name (or quantity 1)]))
                     (into {}))
        other (->> ship-params
                   (filter (fn [el]
                             (and (vector? el)
                                  (not (#{"sprite"
                                          "licenses"
                                          "attributes"
                                          "outfits"}
                                        (first el))))))
                   (reduce (fn [details [detail-name & detail]]
                             (update details
                                     detail-name
                                     #(conj (or % []) (vec detail))))
                           {}))]
    {:ship-name name
     :sprite sprite
     :licenses licenses
     :attributes (merge {:weapon weapon} other-attributes)
     :outfits outfits
     :other other}))

(defn- transform-data [& data]
  (map #(if (= (first %) "ship")
          (-> % rest transform-ship2)
          (first %)) data))

(defn- transform-block [[_ name & args] & child-blocks]
  (vec (cons name
             (concat args child-blocks))))

(def transform-options2
  {:data transform-data
   :0-indented-block transform-block
   :1-indented-block transform-block
   :2-indented-block transform-block
   :3-indented-block transform-block
   :string identity
   :integer #(Integer/parseInt %)
   :float #(Float/parseFloat (str/replace % "," "."))})

(defn parse2 [data]
  (let [parser (-> "new-parser.bnf"
                   resource
                   insta/parser)]
    (->> (parser data)
         (insta/transform transform-options2))))

(defn- get-race-of-file [file]
  (let [filename (.getName file)]
    (if (= filename "ships.txt")
      "human"
      (-> filename (str/split #" ") first))))

(def data
  (->> files
       (mapcat (fn [file]
                 (let [race (get-race-of-file file)
                       ships (-> file slurp parse2)]
                   (map #(assoc % :race race) ships))))))
