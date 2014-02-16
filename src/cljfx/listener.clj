(ns cljfx.listener
  "JavaFX Listener インターフェースを扱う関数。"

  (:import javafx.event.EventHandler
           javafx.beans.InvalidationListener
           javafx.beans.value.ChangeListener))

(defmacro ^:private listener*
  [cls ifmethod f & args]
  (when-let [arg-syms (map (comp gensym str) args)]
    `(reify ~cls
       (~ifmethod [this# ~@arg-syms] (apply ~f [this# ~@arg-syms])))))

(defmulti listener "各種 listener 生成マルチメソッド"
  (fn [listener-type f & receivers] (identity listener-type)))

(defmethod listener :event [listener-type f]
  (listener* EventHandler handle f event))

(defmethod listener :invalitated [listener-type f]
  (listener* InvalidationListener invalidated f ovservable))

(comment
(defmethod listener :changed [listener-type f]
  (listener* ChangeListener changed f ovservable-value old-val new-val))

; for TableView, ListView
(defmethod listener :cell [listener-type f]
  (listener* ChangeListener changed f ovservable-value old-val new-val))

; javafx.collections.ListChangeListener Change -> ObservableList
; javafx.collections.MapChangeListener  Change -> ObservableMap
; javafx.collections.SetChangeListener  Change -> ObservableSet

)
