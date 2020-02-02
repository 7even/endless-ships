(ns endless-ships.core
  (:require [reagent.core :as ra]
            [re-frame.core :as rf]
            [endless-ships.views :as views]))

(defn render []
  (ra/render [views/interface]
             (js/document.getElementById "root")))

(defn init []
  (render))
