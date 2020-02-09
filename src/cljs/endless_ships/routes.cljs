(ns endless-ships.routes
  (:require [re-frame.core :as rf]
            [bidi.bidi :as bidi]
            [pushy.core :as pushy]
            [endless-ships.events :as events]))

(def routes
  ["/" {"" :ships
        ["ships/" :ship/name] :ship
        ["ships/" :ship/name "/" [#"[A-Za-z0-9\-\(\)]+" :ship/modification]] :ship-modification}])

(defn- parse-url [url]
  (let [{:keys [handler route-params]
         :or {route-params {}}}
        (bidi/match-route routes url)]
    [handler route-params]))

(defn- dispatch-route [matched-route]
  (rf/dispatch [::events/navigate-to matched-route]))

(def url-for
  (partial bidi/path-for routes))

(defn start! []
  (let [history (pushy/pushy dispatch-route parse-url)]
    (pushy/start! history)))
