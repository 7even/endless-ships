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

(def ^:private game-repo-url
  "https://raw.githubusercontent.com/endless-sky/endless-sky/")

(defn game-image-url
  "Returns the URL of an image file (relative to images/) at the given game commit."
  [commit path]
  (str game-repo-url
       commit
       "/images/"
       (js/window.encodeURI path)))

(def nbsp "\u00a0")

(defn nbspize [s]
  (str/replace s #" " nbsp))

(defn kebabize [s]
  (-> s
      (str/replace #"\s+" "-")
      (str/replace #"[\?':\.]" "")
      str/lower-case))

(defn format-number [num]
  (cond
    (js/Number.isNaN num) "—"
    (number? num) (let [rounded (-> num
                                    (* 10)
                                    js/Math.round
                                    (/ 10))
                        formatter (NumberFormat. Format/DECIMAL)]
                    (.format formatter (str rounded)))
    :else num))

(defn format-game-number
  "Formats a number like the game's Format::Number: 2 decimal places below 1,000, 1 below
  10,000 and none above; extra digits are cut off, not rounded; trailing zeros are trimmed."
  [num]
  (cond
    (js/Number.isNaN num) "???"
    (zero? num) "0"
    (= num ##Inf) "infinity"
    (= num ##-Inf) "-infinity"
    :else (let [magnitude (js/Math.abs num)
                places (cond
                         (>= magnitude 10000) 0
                         (>= magnitude 1000) 1
                         :else 2)
                scale (js/Math.pow 10 places)
                ;; like the game, add an epsilon to account for floating-point errors
                scaled (js/Math.floor (+ (* magnitude scale)
                                         1e-10))
                whole (js/Math.floor (/ scaled scale))
                decimals (-> (js/Math.round (- scaled
                                               (* whole scale)))
                             str
                             (.padStart places "0")
                             (str/replace #"0+$" ""))]
            (str (when (neg? num)
                   "-")
                 (.toLocaleString whole "en-US")
                 (when (seq decimals)
                   (str "." decimals))))))

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
