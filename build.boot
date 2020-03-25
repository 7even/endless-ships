(set-env! :source-paths #{"src/clj"}
          :resource-paths #{"resources"}
          :dependencies '[[org.clojure/clojure "1.8.0"]
                          [instaparse "1.4.5"]
                          [camel-snake-kebab "0.4.0"]
                          [buddy/buddy-core "1.6.0"]])

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
  (dosh "mkdir" "-p" "./build/js")
  (let [edn-filename (generate-edn {:with-hash? true})
        js-filename (-> (slurp "./public/js/manifest.edn")
                        read-string
                        first
                        :output-name)
        html (str/replace (slurp "./public/index.html")
                          "main.js"
                          js-filename)
        js (str/replace (slurp (str "./public/js/" js-filename))
                        "data.edn"
                        edn-filename)]
    (spit "./build/index.html" html)
    (spit (str "./build/js/" js-filename) js)
    (dosh "cp" "./public/app.css" "./public/ga.json" "./build")
    (if (.exists (clojure.java.io/as-file "ga.json"))
      (dosh "cp" "./ga.json" "./build"))))

(deftask generate-data
  "Generate the data.edn file in public/ for local development."
  []
  (generate-edn {:dir "public"}))
