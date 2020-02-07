(ns endless-ships.views.ship-page)

(defn ship-page [ship-name ship-modification]
  [:div.app
   [:div.row
    [:div.col-md-6
     [:div.panel.panel-default
      [:div.panel-heading ship-name]
      [:div.panel-body "Ship details"]]]]])
