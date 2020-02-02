(set-env! :source-paths #{"src"}
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
  (binding [*sh-dir* "./frontend"]
    (dosh "yarn" "install")
    (dosh "yarn" "run" "build"))
  (dosh "mv" "./frontend/build" "./build")
  (generate-edn)
  (if (.exists (clojure.java.io/as-file "ga.edn"))
    (dosh "cp" "./ga.edn" "./build")))
