(set-env! :source-paths #{"src"}
          :resource-paths #{"resources"}
          :dependencies '[[org.clojure/clojure "1.8.0"]
                          [instaparse "1.4.5"]
                          [cheshire "5.7.0"]
                          [camel-snake-kebab "0.4.0"]])

(require '[endless-ships.core :refer [generate-json]])

(deftask build
  "Build the site into build/ directory."
  []
  (binding [*sh-dir* "./frontend"]
    (dosh "yarn" "install")
    (dosh "yarn" "run" "build"))
  (dosh "mv" "./frontend/build" "./build")
  (generate-json))
