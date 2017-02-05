(set-env! :source-paths #{"src"}
          :resource-paths #{"resources"}
          :dependencies '[[org.clojure/clojure "1.8.0"]
                          [instaparse "1.4.5"]
                          [cheshire "5.7.0"]])

(require '[endless-ships.core :refer [generate-json]])

(deftask run
  []
  (println "Hello world!"))
