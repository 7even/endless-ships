(set-env! :source-paths #{"src/clj"}
          :resource-paths #{"resources"}
          :dependencies '[[org.clojure/clojure "1.8.0"]
                          [instaparse "1.4.5"]
                          [camel-snake-kebab "0.4.0"]
                          [buddy/buddy-core "1.6.0"]])

(require '[buddy.core.codecs :refer [bytes->hex]]
         '[buddy.core.hash :refer [sha1]]
         '[clojure.string :as str]
         '[endless-ships.core :refer [edn]])

(deftask dev
  "Starts an nREPL server."
  []
  (comp
   (wait)
   (repl :server true)))

(defn- filename-with-hash [filename content]
  (let [sha1-hash (-> content sha1 bytes->hex)
        [name extension] (str/split filename #"\.")]
    (str/join "." [name sha1-hash extension])))

(deftask build
  "Build the site into build/ directory."
  []
  (dosh "rm" "-rf" "./build")
  (dosh "yarn" "install")
  (dosh "shadow-cljs" "release" "main")
  (dosh "mkdir" "-p" "./build/js")
  (let [edn-filename (filename-with-hash "data.edn" edn)
        js (-> (slurp "./public/js/main.js")
               (str/replace "data.edn" edn-filename))
        js-filename (filename-with-hash "main.js" js)
        html (-> (slurp "./public/index.html")
                 (str/replace "main.js" js-filename))]
    (spit (str "./build/" edn-filename) edn)
    (spit (str "./build/js/" js-filename) js)
    (spit "./build/index.html" html)
    (dosh "cp" "./public/app.css" "./public/ga.json" "./build")
    (if (.exists (clojure.java.io/as-file "ga.json"))
      (dosh "cp" "./ga.json" "./build"))))

(deftask generate-data
  "Generate the data.edn file in public/ for local development."
  []
  (spit "public/data.edn" edn))
