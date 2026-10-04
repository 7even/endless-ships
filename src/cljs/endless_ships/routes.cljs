(ns endless-ships.routes
  (:require [re-frame.core :as rf]
            [bidi.bidi :as bidi]
            [clojure.string :as str]
            [pushy.core :as pushy]
            [endless-ships.events :as events]
            [endless-ships.views.utils :refer [kebabize nbspize]]))

(def name-pattern
  #"[A-Za-z0-9\-\(\)\,\"_]+")

(def routes
  ["/" {"" :ships
        ["ships/" [name-pattern :ship/name]] :ship
        ["ships/" [name-pattern :ship/name]
         "/" [name-pattern :ship/modification]] :ship-modification
        "outfits" :outfits
        ["outfits/" [name-pattern :outfit/name]] :outfit
        "configurator" :configurator
        ["configurator/" [name-pattern :ship/name]] :configurator-ship
        ["configurator/" [name-pattern :ship/name]
         "/" [name-pattern :ship/modification]] :configurator-ship}])

(defn- parse-query [query]
  (-> (js/Object.fromEntries (js/URLSearchParams. query))
      (js->clj :keywordize-keys true)))

(defn- parse-url
  "Matches the path of a URL to a route; the query string goes to `:query` of route params."
  [url]
  (let [[path query] (str/split url #"\?" 2)
        {:keys [handler route-params]
         :or {route-params {}}}
        (bidi/match-route routes path)]
    [handler
     (cond-> route-params
       (some? query) (assoc :query (parse-query query)))]))

(defn- dispatch-route [matched-route]
  (if (some? js/window.ga)
    (js/window.ga "send" "pageview" js/location.pathname))
  (rf/dispatch [::events/navigate-to matched-route]))

(def url-for
  (partial bidi/path-for routes))

(defonce ^:private history
  (pushy/pushy dispatch-route parse-url))

(defn start! []
  (pushy/start! history))

(rf/reg-fx ::start! start!)

;; navigates to the URL adding a history entry, so "Back" undoes the change;
;; the route is dispatched as usual
(rf/reg-fx ::set-url
  (fn [url]
    (pushy/set-token! history url)))

(defn ship-link [name]
  (let [url (url-for :ship :ship/name (kebabize name))]
    [:a {:href url} (nbspize name)]))

(defn ship-modification-link [name modification]
  (let [url (str "/ships/" (kebabize name) "/" (kebabize modification))]
    [:a {:href url} (nbspize modification)]))

(defn outfit-link [name]
  (let [url (url-for :outfit :outfit/name (kebabize name))]
    [:a {:href url} (nbspize name)]))
