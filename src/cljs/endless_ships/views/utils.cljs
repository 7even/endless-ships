(ns endless-ships.views.utils
  (:require [clojure.string :as str]))

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
