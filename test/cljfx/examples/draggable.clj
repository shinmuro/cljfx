(ns
  cljfx.examples.draggable
  (:use cljfx.core)

  (:import javafx.beans.binding.Bindings))

;; lein run -m cljfx.examples.draggable/draggable-panel

;;; オリジナルの Java サンプル→ http://docs.oracle.com/javafx/2/events/DraggablePanelsExample.java.htm
;;; サンプルを解説した記事    → http://docs.oracle.com/javafx/2/events/filters.htm

(defn setup-login-panel! [cbox]
  (v! cbox :items (as-observable ["English" "日本語" "French"]))
  (.. cbox getSelectionModel (select 0)))

(defn set-button-action! [b label]
  (v! b :on-action (listener :event
                          (fn [_ e]
                            (v! label :text (.. e getSource getText))))))

; こちらは v! の修正必要
;(defn set-button-action! [b label]
;  (v! b :on-action (fn [_ e] (v! label :text (.. e getSource getText)))))

(defn bind-progress! [prog-indi slider]
  (.bind (p prog-indi :progress)
         (Bindings/divide (p slider :value) (p slider :max))))

(defn config-draggable! [drag-mode node]
  (let [anchor-x (atom 0.0)
        anchor-y (atom 0.0)
        trans-x  (atom 0.0)
        trans-y  (atom 0.0)]
    (add-filter! node :mouse (fn [_ e]
                                  (when (.get drag-mode) (.consume e))))

    (add-filter! node
                 :mouse-pressed
                 (fn [_ e]
                  (when (.get drag-mode)
                    (reset! anchor-x (.getX e))
                    (reset! anchor-y (.getY e))
                    (reset! trans-x  (v node :translate-x))
                    (reset! trans-y  (v node :translate-y)))))

    (add-filter! node
                 :mouse-dragged
                 (fn [_ e]
                   (when (.get drag-mode)
                     (v! node :translate-x (+ @trans-x (.getX e) (- @anchor-x)))
                     (v! node :translate-y (+ @trans-y (.getY e) (- @anchor-y))))))))

(defn draggable-panel []
  (let [root  (load-fxml "draggable-panel.fxml")
        drag-mode (as-prop true)]
    (.bind drag-mode (p (fseek root "#dragMode") :selected))

    (set-button-action! (fseek root "#acceptButton") (fseek root "#acceptanceLabel"))
    (set-button-action! (fseek root "#declineButton") (fseek root "#acceptanceLabel"))

    (setup-login-panel! (fseek root ".choice-box"))

    (bind-progress! (fseek root "#progressIndicator")
                    (fseek root "#slider"))

    (doseq [grp (seek root "#wrapGroup")]
      (config-draggable! drag-mode grp))

    (launch root)))
