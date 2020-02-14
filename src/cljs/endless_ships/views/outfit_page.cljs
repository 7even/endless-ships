(ns endless-ships.views.outfit-page
  (:require [re-frame.core :as rf]
            [endless-ships.subs :as subs]))

(defn outfit-page [outfit-name]
  (let [outfit @(rf/subscribe [::subs/outfit outfit-name])]
    [:h2 (:name outfit)]))
