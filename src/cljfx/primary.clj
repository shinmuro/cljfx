(ns cljfx.primary
  "JavaFX の Application クラスを extend させ、start 時に外から参照できるようにしたもの。"
  (:import javafx.application.Application
           javafx.stage.Stage
           [javafx.scene Scene Group])

  (:gen-class
   :extends javafx.application.Application
   :methods [^{:static true} [getPrimaryStage [] javafx.stage.Stage]
             ^{:static true} [getPrimaryScene [] javafx.scene.Scene]
             ^{:static true} [isReady [] Boolean]]))

(def ^:private ready
  "start メソッド完了フラグ。標準で完了したかどうかを見る方法がない為設置"
  (promise))
(defn -isReady [] (and (realized? ready) @ready))

(def ^:private stage
  "Primary Stage。start メソッド完了後インスタンス化される。"
  (promise))
(defn -getPrimaryStage [] (and (realized? stage) @stage))

(def ^:private scene
  "Primary Scene。start メソッド完了後インスタンス化される。"
  (promise))
(defn -getPrimaryScene [] (and (realized? scene) @scene))

(defn -start [this s]
  (deliver stage s)
  (deliver scene (Scene. (Group.)))
  (.setScene @stage @scene)
  (deliver ready true))

(defn -stop [this]
  ; 現状 future オブジェクトが閉じないのでその対処
  (shutdown-agents))
