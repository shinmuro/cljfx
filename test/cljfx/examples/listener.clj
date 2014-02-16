(ns
  cljfx.examples.listener
  (:use [cljfx core property event listener seek coerce]))

;; lein run -m cljfx.examples.draggable/change-listener

(defn change-listener []
  (let [root (load-fxml "change-listener.fxml")
        txt  (fseek root "#txt")
        rect (fseek root "#rect")
        hover-listener (listener :invalitated
                         (fn [_ ov]
                           (if (v rect :hover)
                             (v! txt :text "hovered.")
                             (v! txt :text "not hovered."))))]

    (v! (fseek root "#add")
        :on-action (listener :event
                     (fn [_ e]
                       (.addListener (p rect :hover) hover-listener)
                       (v! txt :text "listener added."))))

    (v! (fseek root "#remove")
        :on-action (listener :event
                     (fn [_ e]
                       (.removeListener (p rect :hover) hover-listener)
                       (v! txt :text "listener removed."))))
    (launch root)))
