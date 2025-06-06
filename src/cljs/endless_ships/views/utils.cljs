(ns endless-ships.views.utils
  (:require [clojure.string :as str])
  (:import (goog.i18n NumberFormat)
           (goog.i18n.NumberFormat Format)))

(def license-label-styles
  {"City-Ship"          "human"
   "Navy"               "human"
   "Navy Carrier"       "human"
   "Navy Cruiser"       "human"
   "Navy Auxiliary"     "human"
   "Militia"            "human"
   "Unfettered Militia" "hai"
   "Wanderer"           "wanderer"
   "Wanderer Military"  "wanderer"
   "Wanderer Outfits"   "wanderer"
   "Coalition"          "coalition"
   "Heliarch"           "coalition"
   "Remnant"            "remnant"
   "Remnant Capital"    "remnant"
   "Scin Adjutant"      "gegno"
   "Scin Architect"     "gegno"
   "Scin Hoplologist"   "gegno"
   "Vi Lord"            "gegno"
   "Vi Centurion"       "gegno"
   "Vi Evocati"         "gegno"
   "Gegno Civilian"     "gegno"
   "Gegno Driller"      "gegno"
   "Hicemus Conflict"   "incipias"
   "Successor"          "successor"
   "High Houses"        "successor"
   "Avgi Atomics"       "avgi"
   "Avgi Torch"         "avgi"
   "Twilight Guard"     "avgi"})

(defn license-label [license]
  (let [style (get license-label-styles license)]
    ^{:key license} [:span.label {:class (str "label-" style)} license]))

(def nbsp "\u00a0")

(defn nbspize [s]
  (str/replace s #" " nbsp))

(defn kebabize [s]
  (-> s
      (str/replace #"\s+" "-")
      (str/replace #"[\?':]" "")
      str/lower-case))

(defn format-number [num]
  (if (number? num)
    (let [rounded (-> num
                      (* 10)
                      js/Math.round
                      (/ 10))
          formatter (NumberFormat. Format/DECIMAL)]
      (.format formatter (str rounded)))
    num))

(defn render-attribute [m prop label]
  (let [v (prop m)]
    (when (some? v)
      (if (number? v)
        [:li (str label ": " (format-number v))]
        [:li (str label ": " v)]))))

(defn render-percentage [m prop label]
  (let [v (prop m)]
    (when (some? v)
      [:li (str label ": " (format-number (* v 100)) "%")])))

(defn render-description [entity]
  (->> (:description entity)
       (map-indexed (fn [idx paragraph]
                      [paragraph
                       ^{:key idx} [:span [:br] [:br]]]))
       (apply concat)
       butlast))
