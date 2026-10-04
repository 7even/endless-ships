(ns endless-ships.attributes
  "Raw ship and outfit attributes in the form the game uses them in calculations."
  (:require [clojure.java.io :refer [file resource]]
            [clojure.string :as str]))

(def ^:private non-attribute-keys
  "Numeric keys of attribute nodes that the game stores outside of attributes
  (see Outfit::Load in the game sources)."
  #{"cost" "mass" "index"})

(defn ->attributes
  "Converts a parsed attributes node (an outfit or a ship's `attributes` block)
  into a map from attribute name to its value. Only numeric attributes are kept;
  if an attribute is set several times, the last value wins like in the game."
  [node]
  (reduce (fn [attributes [attr-name occurrences]]
            (let [value (-> occurrences peek first first)]
              (if (and (number? value)
                       (not (contains? non-attribute-keys attr-name)))
                (assoc attributes attr-name value)
                attributes)))
          (sorted-map)
          (dissoc node "file")))

(defn- parse-minimum [entry value]
  (if (= value "std::nullopt")
    nil
    (if-let [[_ minimum] (re-matches #"(-?[0-9.]+) \* ATTRIBUTE_PRECISION" value)]
      (Double/parseDouble minimum)
      (throw (ex-info "Unknown attribute minimum format in MINIMUM_OVERRIDES"
                      {:entry entry})))))

(def attribute-minimums
  "Attributes whose minimum value differs from 0, parsed from MINIMUM_OVERRIDES
  in the game sources (source/Outfit.cpp). A nil minimum means that the attribute
  may have any value."
  (let [source (-> "game/source/Outfit.cpp" resource file slurp)
        [_ body] (re-find #"(?s)MINIMUM_OVERRIDES = [^{]*\{(.*?)\n\t\};" source)
        minimums (->> (str/split-lines (or body ""))
                      (map str/trim)
                      (filter #(str/starts-with? % "{\""))
                      (map (fn [entry]
                             (if-let [[_ attr-name value]
                                      (re-matches #"\{\"([^\"]+)\", (.+)\},?" entry)]
                               [attr-name (parse-minimum entry value)]
                               (throw (ex-info "Unknown entry format in MINIMUM_OVERRIDES"
                                               {:entry entry})))))
                      (into (sorted-map)))]
    (when (empty? minimums)
      (throw (ex-info "MINIMUM_OVERRIDES not found in source/Outfit.cpp" {})))
    minimums))
