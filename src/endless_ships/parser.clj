(ns endless-ships.parser
  (:require [camel-snake-kebab.core :refer [->kebab-case]]
            [clojure.java.io :refer [file resource]]
            [clojure.string :as str]
            [instaparse.core :as insta])
  (:import [java.lang Float Integer]))

(def files
  "All files containing ships data."
  (->> "game/data"
       resource
       file
       file-seq
       (filter #(.endsWith (.getName %) ".txt"))))

(defn first-with-key [key data]
  (->> data
       (filter #(= (first %) key))
       (map (comp vec rest))
       first))

(defn- transform-block [[_ name & args] & child-blocks]
  (let [processed-children (reduce (fn [children [child-name & child-contents]]
                                     (update children
                                             child-name
                                             #(conj (or % [])
                                                    (vec child-contents))))
                                   {}
                                   child-blocks)]
    [name (vec args) processed-children]))

(def transform-options
  {:data vector
   :0-indented-block transform-block
   :1-indented-block transform-block
   :2-indented-block transform-block
   :3-indented-block transform-block
   :4-indented-block transform-block
   :5-indented-block transform-block
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
  (time
   (->> files
        (mapcat (fn [file]
                  (let [race (get-race-of-file file)
                        ships (-> file slurp parse)]
                    (map #(assoc-in % [2 "race"] race) ships))))
        doall)))

(comment
  (->> data
       (map first)
       (reduce (fn [counts object]
                 (update counts object #(inc (or % 0))))
               {})
       (sort-by last >)))
