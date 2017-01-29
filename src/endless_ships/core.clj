(ns endless-ships.core
  (:require [clojure.java.io :refer [file resource]]
            [clojure.string :as str]
            [instaparse.core :as insta])
  (:import [java.lang Float Integer]))

(def transform-options
  {:string identity
   :integer #(Integer/parseInt %)
   :float #(Float/parseFloat (str/replace % "," "."))})

(defn parse [text]
  (let [parser (-> "parser.bnf"
                   resource
                   insta/parser)]
    (->> (parser text)
         (insta/transform transform-options))))

(def files
  "All files containing ships data."
  (->> "game/data"
       resource
       file
       file-seq
       (filter #(str/ends-with? % "ships.txt"))))
