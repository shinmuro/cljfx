(ns  cljfx.core
  "簡易 JavaFX wrapper。FXML から読み込んでどうこうする事を前提としてるので動的生成は弱い。"

  (:import javafx.application.Application
           javafx.application.Platform
           javafx.fxml.FXMLLoader

           javafx.scene.Node
           javafx.scene.control.Skin

           cljfx.primary)

  (:require [clojure.java.io :as io]))

(load "core_property")
(load "core_coerce")
(load "core_event")
(load "core_bind")

(defn fseek
  "f(irst)seek。

   JavaFX CSS セレクタ書式に従ってマッチする Node を返す。
   セレクタの結果複数となっても最初の Node のみ返す。"
  [^Node node fmt]
  (.lookup node fmt))

(defmacro nid-let
  "root から selector-strings にある CSS セレクタで Node を抽出しつつ
   CSS セレクタに指定している Node id のシンボルでローカル変数を設定し、
   body を実行するマクロ。"
  [root selectors & body]
  (letfn [(seek-form [s] `(fseek ~root ~s))]
    (let [syms (map (comp symbol 
                          last
                          (partial re-find #".*#(.*)$"))
                    selectors)
          seek-forms (map seek-form selectors)]
    `(let ~(vec (interleave syms seek-forms))
       ~@body))))

(defn seek
  "JavaFX CSS セレクタ書式に従ってマッチする Node のシーケンスを返す。

   オブジェクト操作を目的としている為、シーケンスを返す際 javafx.scene.control.Skin なものは除外する。
   (いいのかどうか分からんが)"
  [node fmt]
  (letfn [(skin? [obj]
            (isa? (class obj) javafx.scene.control.Skin))]
    (->> (.lookupAll node fmt)
         (remove skin?))))

#_(defn load-fxml
  [name]
  (FXMLLoader/load (io/resource name)))

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

(defn set-scene! [root]
  (v! (primary/getPrimaryScene) :root root))

(defn launch [root & args]
  ;; 単に今のとは別スレッドで走らせたいだけなので future は何も受け取らない
  ;; 以降 StackTrace が取れなくなると言う問題は残るが
  (future (apply launch-await args))
;  (Thread/sleep 2000)
  (loop [ready? (primary/isReady)]
    (if ready?
      (do
;        (v! (primary/getPrimaryScene) :root root)
        (set-scene! root)
        (show (primary/getPrimaryStage)))
      (recur (primary/isReady)))))

(defn- kbd*
  [s]
  (->> (s/upper-case s)
       (str (symbol 'javafx.scene.input.KeyCode) "/")
       symbol
       eval))

(def kbd
  "指定したキーコードの javafx.scene.input.KeyCode オブジェクトを返す。
   大文字小文字の区别はない。"
  (memoize kbd*))
