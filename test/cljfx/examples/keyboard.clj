(ns cljfx.examples.keyboard
  (import javafx.beans.binding.Bindings
          javafx.scene.input.KeyCode
          javafx.scene.input.KeyEvent
          javafx.scene.paint.Color
          javafx.scene.Node)

  (:use cljfx.core))

;; lein run -m cljfx.examples.keyboard/keyboard-example

;;;
;;; Key クラス
;;;
(defprotocol IKey
  (key-code [this] "class Key#getKeyCode()")
  (set-pressed! [this v] "class Key#setPressed()")
  (create-node [this] "class Key#createNode()"))

(deftype Key [^KeyCode key ^:volatile-mutable pressed]
  IKey
  (key-code [this] key)
  (set-pressed! [this v] (.set pressed v))

  (create-node [this]
    (let [node (load-fxml "key.fxml")
          lbl (fseek node "#key-label")
          bg (fseek node "#key-back-ground")
          handler-fn
          (fn [_ key-event]
            (if (= (.getCode key-event) (kbd "enter"))
              (do
                (.set pressed
                      (= (.getEventType key-event) KeyEvent/KEY_PRESSED))
                (.consume key-event))))]
      (do
        (.bind (p bg :fill)
               (.. (Bindings/when pressed)
                   (then Color/RED)
                   (otherwise (.. (Bindings/when (p node :focused))
                                  (then Color/LIGHTGRAY)
                                  (otherwise Color/WHITE)))))

        (v! lbl :text (.getName key))

        (add-handler! node :key-pressed handler-fn)
        (add-handler! node :key-released handler-fn))
      node)))

(defn find-at
  "シーケンスにマッチする位置から n 移動した位置の item を返す。
   n の指定がない場合はマッチする位置の item を返す。
   マッチする item がない、または移動した位置に item が存在しなければ nil を返す。"
  [coll item & [n]]
  (let [index (.indexOf coll item)]
    (if-not (neg? index)
      (nth coll
           (if n (+ index n) index)
           nil))))

;;;
;;; Keyboard クラス込みの起動関数
;;;
(defn keyboard-example []
  (let [root (load-fxml "keyboard.fxml")
        key-codes (map #(Key. %1 %2)
                       (map kbd [\a \s \d \f])
                       (repeatedly #(as-prop false)))
        key-nodes (map create-node key-codes)
        lookup-node (fn [k] (first (filter #(= (key-code %) k) key-codes)))
        handler-fn
        (fn [_ key-event]
          (when-let [k (lookup-node (.getCode key-event))]
            (do
              (set-pressed! k (= (.getEventType key-event) KeyEvent/KEY_PRESSED))
;               (set-pressed! k (= (.getEventType key-event) (e :key-pressed)))
              (.consume key-event))))]

    (.. root getChildren (addAll (to-array key-nodes)))

    (add-handler! root :key-pressed handler-fn)
    (add-handler! root :key-released handler-fn)

    (add-handler! root
      :key-pressed
      (fn [_ key-event]
        (if-let [next-node
                 (case (.. key-event getCode getName)
                   "Left" (do (.consume key-event)
                              (find-at key-nodes (.getTarget key-event) -1))
                   "Right" (do (.consume key-event)
                               (find-at key-nodes (.getTarget key-event) 1))
                   nil)]
          (do
            (.requestFocus next-node)))))

    (launch root)))
