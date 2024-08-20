(ns endless-ships.core
  (:require [buddy.core.codecs :refer [bytes->hex]]
            [buddy.core.hash :refer [sha1]]
            [clojure.java.io :as io]
            [clojure.java.shell :refer [sh]]
            clojure.pprint
            [clojure.set :refer [rename-keys]]
            [clojure.string :as str]
            [endless-ships.outfits :refer [outfits]]
            [endless-ships.outfitters :refer [outfitters]]
            [endless-ships.ships :refer [modifications ships]]
            [endless-ships.parser]))

(def file->race
  {"kestrel.txt" :human
   "hai ships.txt" :hai
   "pug.txt" :pug
   "wanderer ships.txt" :wanderer
   "quarg ships.txt" :quarg
   "remnant ships.txt" :remnant
   "kahet ships.txt" :ka'het
   "korath ships.txt" :korath
   "marauders.txt" :pirate
   "coalition ships.txt" :coalition
   "drak ships.txt" :drak
   "ships.txt" :human
   "indigenous.txt" :indigenous
   "sheragi ships.txt" :sheragi
   "gegno ships.txt" :gegno
   "rulei ships.txt" :vyu-rulei
   "incipias ships.txt" :incipias
   "bunrodea ships.txt" :bunrodea
   ;; fixme: there is no guarantee this file will contain only Korath ships
   "deprecated ships.txt" :korath})

(def outfits-data
  (->> outfits
       (remove #(#{"deprecated outfits.txt"
                   "nanobots.txt"
                   "transport missions.txt"
                   "vyrmeid.txt"} (:file %)))
       (map #(dissoc % :file))))

(defn- assoc-outfits-cost [ship]
  (let [outfits (:outfits ship)]
    (if (empty? outfits)
      ship
      (assoc ship
             :outfits-cost
             (reduce (fn [cost {:keys [name quantity]}]
                       (let [outfit (->> outfits-data
                                         (filter #(= (:name %) name))
                                         first)]
                         (+ cost
                            (* (get outfit :cost 0)
                               quantity))))
                     0
                     outfits)))))

(def ships-data
  (->> ships
       (filter #(some? (file->race (:file %))))
       (map #(-> %
                 (select-keys [:name :sprite :licenses :file
                               :cost :category :hull :shields :mass
                               :engine-capacity :weapon-capacity :fuel-capacity
                               :outfits :outfit-space :cargo-space
                               :required-crew :bunks :description
                               :guns :turrets :drones :fighters
                               :self-destruct :ramscoop])
                 (assoc :race (get file->race (:file %) :other))
                 (dissoc :file)
                 (rename-keys {:cost :empty-hull-cost})
                 assoc-outfits-cost))))

(def modifications-data
  (->> modifications
       (map #(-> %
                 (dissoc :file)
                 (rename-keys {:cost :empty-hull-cost})
                 assoc-outfits-cost))))

(def game-version
  (let [git-cmd (fn [& args]
                  (->> (concat ["git"] args [:dir "./resources/game"])
                       (apply sh)
                       :out
                       str/trim))
        commit-hash (git-cmd "rev-parse" "HEAD")
        commit-date (-> (git-cmd "show" "-s" "--format=%ci" "HEAD")
                        (str/split #" ")
                        first)
        [tag commits-since-tag] (-> (git-cmd "describe" "--tags" "HEAD")
                                    (str/split #"-"))]
    (merge {:hash commit-hash
            :date commit-date}
           (when (nil? commits-since-tag)
             {:tag tag}))))

(def edn
  (let [data {:ships ships-data
              :ship-modifications modifications-data
              :outfits outfits-data
              :outfitters outfitters
              :version game-version}]
    (with-out-str (clojure.pprint/pprint data))))

(defn- filename-with-hash [filename content]
  (let [sha1-hash (-> content sha1 bytes->hex (subs 0 10))
        [name extension] (str/split filename #"\.")]
    (str/join "." [name sha1-hash extension])))

(defn build
  "Builds the site into build/ directory."
  [_]
  (when (.exists (io/file "./build"))
    (->> (io/file "./build")
         file-seq
         reverse
         (run! io/delete-file)))
  (sh "npm" "install")
  (sh "shadow-cljs" "release" "main")
  (io/make-parents "./build/js/main.js")
  (let [edn-filename (filename-with-hash "data.edn" edn)
        js (-> (slurp "./public/js/main.js")
               (str/replace "data.edn" edn-filename))
        js-filename (filename-with-hash "main.js" js)
        html (-> (slurp "./public/index.html")
                 (str/replace "main.js" js-filename))]
    (spit (str "./build/" edn-filename) edn)
    (spit (str "./build/js/" js-filename) js)
    (spit "./build/index.html" html)
    (io/copy (io/file "./public/app.css")
             (io/file "./build/app.css"))
    (io/copy (io/file "./public/ga.json")
             (io/file "./build/ga.json"))
    (when (.exists (io/file "./ga.json"))
      (io/copy (io/file "./ga.json")
               (io/file "./build/ga.json")))))

(comment
  ;; generate data for frontend development
  (spit "public/data.edn" edn)
  ;; get a list of all possible attribute names
  (->> ships-data
       (map keys)
       (apply concat)
       (into #{}))
  ;; get ship counts by race
  (->> ships-data
       (map :race)
       (reduce (fn [counts object]
                 (update counts object #(inc (or % 0))))
               {})
       (sort-by last >))
  ;; get government colors in CSS format
  (let [preset-colors (->> endless-ships.parser/data
                           (filter #(= (first %) "color"))
                           (reduce (fn [acc [_ [name r g b]]]
                                     (assoc acc name [r g b]))
                                   {}))]
    (->> endless-ships.parser/data
         (filter #(= (first %) "government"))
         (map (fn [[_ [name] {[[color]] "color"}]]
                [name color]))
         (filter #(some? (second %)))
         (reduce (fn [acc [government color]]
                   (let [real-color (if (and (= (count color) 1)
                                             (string? (first color)))
                                      (get preset-colors (first color))
                                      color)]
                     (assoc acc
                            government
                            (->> real-color
                                 (map (partial * 255))
                                 (map int)
                                 (map (partial format "%02x"))
                                 clojure.string/join
                                 (str "#")))))
                 {})))
  ;; get all licenses from ships & outfits
  (-> (sorted-set)
      (into (mapcat :licenses ships-data))
      (into (mapcat :licenses outfits-data))))
