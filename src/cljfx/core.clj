(ns cljfx.core
  "簡易 JavaFX wrapper。FXML から読み込んでどうこうする事を前提としてるので動的生成は弱い。"

  (:import javafx.application.Application
           javafx.application.Platform
           javafx.fxml.FXMLLoader

           cljfx.primary)

  (:require [clojure.java.io :as io])

  (:use [cljfx property event seek]))

(defn load-fxml
  [name]
  (FXMLLoader/load (io/resource name)))

(defn launch-await
  "Primary Stage 起動。
   JavaFX API の Application.launch() 同様実行後は戻ってこなくなる。"
  [& args]
  (Application/launch cljfx.primary (into-array String args)))

(defmacro run-later
  "JavaFX Application Thread 上で body を実行するマクロ。"
  [& body]
  `(Platform/runLater (fn [] ~@body)))

(defn show [stage]
  (run-later (.show stage)))

(defn launch [root & args]
  ;; 単に今のとは別スレッドで走らせたいだけなので future は何も受け取らない
  ;; 以降 StackTrace が取れなくなると言う問題は残るが
  (future (apply launch-await args))
  (Thread/sleep 3000)
  (loop [ready? (primary/isReady)]
    (if ready?
      (do
        (v! (primary/getPrimaryScene) :root root)
        (show (primary/getPrimaryStage)))
      (recur (primary/isReady)))))
