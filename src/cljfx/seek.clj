(ns cljfx.seek
  "JavaFX id を CSSで検索する関数。"

  (:import javafx.scene.Node
           cljfx.primary)

  (:use [cljfx property]))

(defn fseek
  "f(irst)seek。

   JavaFX CSS セレクタ書式に従ってマッチする Node を返す。
   セレクタの結果複数となっても最初の Node のみ返す。"
  [^Node node fmt]
  (.lookup node fmt))

(defn fseek-root
  "f(irst)seek root。

   Primary Scene から取得できる root node から fseek する。"
  [fmt]
  (fseek (p (primary/getPrimaryScene) :root) fmt))

(defn seek
  "JavaFX CSS セレクタ書式に従ってマッチする Node のシーケンスを返す。

   オブジェクト操作を目的としている為、シーケンスを返す際 javafx.scene.control.Skin なものは除外する。
   (いいのかどうか分からんが)"
  [node fmt]
  (letfn [(skin? [obj]
            (isa? (class obj) javafx.scene.control.Skin))]
    (->> (.lookupAll node fmt)
         (remove skin?))))

(defn seek-root
  "Primary Scene から取得できる root node から seek を行う。"
  [fmt]
  (seek (p (primary/getPrimaryScene) :root) fmt))
