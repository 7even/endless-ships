(set-env! :source-paths #{"src/clj"}
          :resource-paths #{"resources"}
          :dependencies '[[org.clojure/clojure "1.8.0"]
                          [instaparse "1.4.5"]
                          [camel-snake-kebab "0.4.0"]])

(require '[endless-ships.core :refer [generate-edn]])

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
  (dosh "cp" "./public/index.html" "./public/app.css" "./public/ga.json" "./build")
  (dosh "cp" "./public/js/main.js" "./build/js")
  (generate-edn)
  (if (.exists (clojure.java.io/as-file "ga.json"))
    (dosh "cp" "./ga.json" "./build")))
