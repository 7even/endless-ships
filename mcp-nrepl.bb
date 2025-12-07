#!/usr/bin/env bb

(require '[babashka.fs :as fs]
         '[babashka.process :as p])

(def nrepl-port-file ".nrepl-port")

(when-not (fs/exists? nrepl-port-file)
  (binding [*out* *err*]
    (println "Error: .nrepl-port file not found. Is the REPL running?"))
  (System/exit 1))

(let [port (str/trim (slurp nrepl-port-file))]
  (when (str/blank? port)
    (binding [*out* *err*]
      (println "Error: .nrepl-port file is empty"))
    (System/exit 1))
  (binding [*out* *err*]
    (println (str "Connecting to nREPL on port " port)))
  (p/exec "clojure" "-X:mcp" ":port" port))
