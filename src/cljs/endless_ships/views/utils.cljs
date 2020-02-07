(ns endless-ships.views.utils
  (:require [clojure.string :as str])
  (:import (goog.i18n NumberFormat)
           (goog.i18n.NumberFormat Format)))

(def license-label-styles
  {"City-Ship"          "human"
   "Navy"               "human"
   "Carrier"            "human"
   "Cruiser"            "human"
   "Militia Carrier"    "human"
   "Unfettered Militia" "hai"
   "Wanderer"           "wanderer"
   "Wanderer Military"  "wanderer"
   "Wanderer Outfits"   "wanderer"
   "Coalition"          "coalition"
   "Heliarch"           "coalition"
   "Remnant"            "remnant"
   "Remnant Capital"    "remnant"})

(defn license-label [license]
  (let [style (get license-label-styles license)]
    ^{:key license} [:span.label {:class (str "label-" style)} license]))

(def nbsp "\u00a0")

(defn nbspize [s]
  (str/replace s #" " nbsp))

(defn kebabize [s]
  (-> s
      (str/replace #"\s+" "-")
      str/lower-case))

(defn format-number [num]
  (let [formatter (NumberFormat. Format/DECIMAL)]
    (.format formatter (str num))))
