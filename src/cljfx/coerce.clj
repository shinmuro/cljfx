(ns cljfx.coerce
   "FXCollection や Property 生成関数。"

   (import javafx.collections.FXCollections
           javafx.beans.Observable
           [javafx.beans.property
            SimpleBooleanProperty SimpleIntegerProperty SimpleDoubleProperty SimpleStringProperty
            SimpleListProperty SimpleMapProperty SimpleSetProperty]))

(defprotocol IObservableCollectionFactory
  "JavaFX Observable Collection 生成。
   参照のみとする場合は as-observable, 変更可能な Observable を取得する場合は as-observable! を使う事。

   通常の Clojure シーケンス関数群はほぼ使用可能だが、返す値は Observable でなくなるため、
   UI へ実際に設定するタイミングで使う事を推奨。

   シーケンスについてはコレを通した後も評価結果だけ見ると何も変わらない為注意。"
  (as-observable! [this] "状態変更可能なコレクションを返す。")
  (as-observable [this]  "変更不可なコレクションを返す。Clojure の PesistentCollection とは恐らく異なる。"))

(extend-protocol IObservableCollectionFactory
  nil
  (as-observable! [_] (FXCollections/emptyObservableList))
  (as-observable  [_] (FXCollections/emptyObservableList))

  clojure.lang.PersistentList
  (as-observable! [coll] (FXCollections/observableList coll))
  (as-observable  [coll]
    (FXCollections/unmodifiableObservableList (as-observable! coll)))

  clojure.lang.PersistentVector
  (as-observable! [coll] (FXCollections/observableList (apply list coll)))
  (as-observable  [coll]
    (FXCollections/unmodifiableObservableList (as-observable! coll)))

  clojure.lang.PersistentHashMap
  (as-observable! [coll] (FXCollections/observableMap coll))
  (as-observable  [coll]
    (FXCollections/unmodifiableObservableMap (as-observable! coll)))

  clojure.lang.PersistentArrayMap
  (as-observable! [coll] (FXCollections/observableMap coll))
  (as-observable  [coll]
    (FXCollections/unmodifiableObservableMap (as-observable! coll)))
  
  clojure.lang.PersistentHashSet
  (as-observable! [coll] (FXCollections/observableSet coll)))

(defprotocol IPropertyValueFactory
  "SimpleXXXProperty 生成。bind 用。

  シーケンスについてはコレを通した後も評価結果だけ見ると何も変わらない為注意。"
  (as-prop [this]))

(extend-protocol IPropertyValueFactory
  java.lang.Boolean
  (as-prop [x] (SimpleBooleanProperty. x))

  java.lang.Long
  (as-prop [x] (SimpleIntegerProperty. x))

  java.lang.Double
  (as-prop [x] (SimpleDoubleProperty. x))

  java.lang.String
  (as-prop [x] (SimpleStringProperty. x))

  javafx.collections.ObservableList
  (as-prop [x] (SimpleListProperty. x))

  javafx.collections.ObservableSet
  (as-prop [x] (SimpleSetProperty. x))

  javafx.collections.ObservableMap
  (as-prop [x] (SimpleMapProperty. x))

  clojure.lang.PersistentList
  (as-prop [x] (SimpleListProperty. (as-observable! x)))

  clojure.lang.PersistentVector
  (as-prop [x] (SimpleListProperty. (as-observable! x)))

  clojure.lang.PersistentHashMap
  (as-prop [x] (SimpleMapProperty. (as-observable! x)))

  clojure.lang.PersistentArrayMap
  (as-prop [x] (SimpleMapProperty. (as-observable! x)))

  clojure.lang.PersistentHashSet
  (as-prop [x] (SimpleSetProperty. (as-observable! x))))

; API 見ると使ってるのあるのはあるが実際に使うまではペンディング
;  javafx.beans.Observable
;  (as-prop [x] (SimpleObjectProperty x)))
