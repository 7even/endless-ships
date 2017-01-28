(set-env! :source-paths #{"src"}
          :resource-paths #{"resources"}
          :dependencies '[[org.clojure/clojure "1.8.0"]
                          [instaparse "1.4.5"]])

(require '[endless-ships.core :refer [parse]])

(deftask run
  []
  (println "Hello world!"))
