(ns cljfx.examples.listener
  (:use cljfx.core))

;; lein run -m cljfx.examples.listener/change-listener

(defn change-listener []
  (let [root (load-fxml "change-listener.fxml")
        txt  (fseek root "#txt")
        rect (fseek root "#rect")
        hover-listener (listener :invalidated
                         (fn [_ ov]
                           (if (v rect :hover)
                             (v! txt :text "hovered.")
                             (v! txt :text "not hovered."))))]

    (v! (fseek root "#add")
        :on-action (listened [_ e]
                     (.addListener (p rect :hover) hover-listener)
                     (v! txt :text "listener added.")))

    (v! (fseek root "#remove")
        :on-action (listened [_ e]
                     (.removeListener (p rect :hover) hover-listener)
                     (v! txt :text "listener removed.")))

    (launch root)))
