(set-env! :source-paths #{"src/clj"}
          :resource-paths #{"resources"}
          :dependencies '[[org.clojure/clojure "1.8.0"]
                          [instaparse "1.4.5"]
                          [camel-snake-kebab "0.4.0"]])

(require '[clojure.string :as str]
         '[endless-ships.core :refer [generate-edn]])

(deftask dev
  "Starts an nREPL server."
  []
  (comp
   (wait)
   (repl :server true)))

(deftask build
  "Build the site into build/ directory."
  []
  (dosh "rm" "-rf" "./build")
  (dosh "yarn" "install")
  (dosh "shadow-cljs" "release" "main")
  (let [js-filename (-> (slurp "./public/js/manifest.edn")
                        read-string
                        first
                        :output-name)
        html (str/replace (slurp "./public/index.html") "main.js" js-filename)]
    (dosh "mkdir" "-p" "./build/js")
    (spit "./build/index.html" html)
    (dosh "cp" "./public/app.css" "./public/ga.json" "./build")
    (dosh "cp" (str "./public/js/" js-filename) "./build/js")
    (generate-edn)
    (if (.exists (clojure.java.io/as-file "ga.json"))
      (dosh "cp" "./ga.json" "./build"))))

(deftask generate-data
  "Generate the data.edn file in public/ for local development."
  []
  (generate-edn "public/data.edn"))
