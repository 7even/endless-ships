(ns endless-ships.core
  (:require [clojure.java.io :refer [file resource]]
            [instaparse.core :as insta]))

(defn parse [text]
  (let [parser (-> "parser.bnf"
                   resource
                   insta/parser)]
    (parser text)))

(def files
  (-> "data" resource file file-seq))
