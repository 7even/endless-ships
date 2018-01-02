(ns endless-ships.parser
  (:require [clojure.java.io :refer [file resource]]
            [clojure.string :as str]
            [instaparse.core :as insta])
  (:import [java.lang Float Integer]))

(def files
  "All files containing ships data."
  (let [filenames ["ships.txt"
                   "hai ships.txt"
                   "quarg ships.txt"
                   "korath ships.txt"
                   "wanderer ships.txt"
                   "coalition ships.txt"
                   "kestrel.txt"
                   ;; "pug.txt"
                   "outfits.txt"]]
    (map #(-> (str "game/data/" %)
              resource
              file)
         filenames)))

(defn first-with-key [key data]
  (->> data
       (filter #(= (first %) key))
       (map (comp vec rest))
       first))

(defn transform-ship [ship-params]
  (let [name (vec (remove vector? ship-params))
        sprite (first-with-key "sprite" ship-params)
        all-attributes (first-with-key "attributes" ship-params)
        weapon (->> all-attributes
                    (first-with-key "weapon")
                    (into {}))
        licenses (->> all-attributes
                      (first-with-key "licenses")
                      (apply concat)
                      vec)
        other-attributes (->> all-attributes
                              (remove #(#{"weapon" "licenses"} (first %)))
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
                                          "attributes"
                                          "outfits"}
                                        (first el))))))
                   (reduce (fn [details [detail-name & detail]]
                             (update details
                                     detail-name
                                     #(conj (or % []) (vec detail))))
                           {}))]
    {:type ::ship
     :ship-name name
     :sprite sprite
     :licenses licenses
     :attributes (merge {:weapon weapon} other-attributes)
     :outfits outfits
     :other other}))

(defn transform-outfit [outfit-params]
  {:type ::outfit
   :data outfit-params}
  (let [name (vec (remove vector? outfit-params))
        thumbnail (first-with-key "thumbnail" outfit-params)
        category (first-with-key "category" outfit-params)
        other-attributes (->> outfit-params
                              (filter #(and (vector? %)
                                            (not (#{"thumbnail" "category"} (first %)))))
                              (into {}))]
    {:type ::outfit
     :name name
     :category category
     :thumbnail thumbnail
     :attributes other-attributes}))

(defn- transform-data [& data]
  (map (fn [[type & attributes]]
         (case type
           "ship" (transform-ship attributes)
           "outfit" (transform-outfit attributes)
           {:type type})
         ;; {:type type :attributes attributes}
         )
       data))

(defn- transform-block [[_ name & args] & child-blocks]
  (vec (cons name
             (concat args child-blocks))))

(def transform-options
  {:data transform-data
   :0-indented-block transform-block
   :1-indented-block transform-block
   :2-indented-block transform-block
   :3-indented-block transform-block
   :string identity
   :integer #(Integer/parseInt %)
   :float #(Float/parseFloat (str/replace % "," "."))})

(defn parse [data]
  (let [parser (-> "parser.bnf"
                   resource
                   insta/parser)]
    (->> (parser data)
         (insta/transform transform-options))))

(defn- get-race-of-file [file]
  (let [filename (.getName file)]
    (case filename
      "ships.txt" "human"
      "kestrel.txt" "human"
      "pug.txt" "pug"
      "outfits.txt" "human"
      (-> filename (str/split #" ") first))))

(def data
  (->> files
       (mapcat (fn [file]
                 (let [race (get-race-of-file file)
                       ships (-> file slurp parse)]
                   (map #(assoc % :race race) ships))))))
