(in-ns 'cljfx.core)

(import javafx.scene.Node
        '[javafx.event EventHandler EventType]
        '[javafx.event Event ActionEvent]
        '[javafx.scene.control
          ListView$EditEvent
          CheckBoxTreeItem$TreeModificationEvent
          TableColumn$CellEditEvent
          TreeItem$TreeModificationEvent
          TreeView$EditEvent]

        '[javafx.scene.media MediaMarkerEvent MediaErrorEvent]

        '[javafx.scene.input
          InputEvent ContextMenuEvent DragEvent
          GestureEvent RotateEvent ScrollEvent
          SwipeEvent ZoomEvent InputMethodEvent
          KeyEvent MouseEvent MouseDragEvent TouchEvent]

         javafx.scene.web.WebEvent
         javafx.stage.WindowEvent
         javafx.concurrent.WorkerStateEvent)

(use 'cljfx.util)

(import javafx.event.EventHandler
        javafx.beans.InvalidationListener
        javafx.beans.value.ChangeListener)


(defmacro ^:private listener*
  [cls ifmethod f & args]
  (when-let [arg-syms (map (comp gensym str) args)]
    `(reify ~cls
       (~ifmethod [this# ~@arg-syms] (apply ~f [this# ~@arg-syms])))))

(defmulti listener "各種 listener 生成マルチメソッド"
  (fn [listener-type f & receivers] (identity listener-type)))

(defmethod listener :event [listener-type f]
  (listener* EventHandler handle f event))

(defmethod listener :invalidated [listener-type f]
  (listener* InvalidationListener invalidated f ovservable))

(defmethod listener :changed [listener-type f]
  (listener* ChangeListener changed f ovservable old-value new-value))

(defmacro listened
  "EventHandler 簡易生成マクロ。

   body を実行する EventHandler を生成し、body 実行後は Event.consume() も行う。"
  [obj-and-event & body]
  `(listener :event (fn ~obj-and-event
                      (try ~@body
                           (finally
                             (.consume ~(obj-and-event 1)))))))

;; リストアップはほぼ力技
#_(def ^:private event-classes
  [CheckBoxTreeItem$TreeModificationEvent ListView$EditEvent TableColumn$CellEditEvent
   TreeItem$TreeModificationEvent TreeView$EditEvent Event ActionEvent MediaMarkerEvent InputEvent
   ContextMenuEvent DragEvent GestureEvent RotateEvent ScrollEvent SwipeEvent ZoomEvent InputMethodEvent
   KeyEvent MouseEvent MouseDragEvent TouchEvent MediaErrorEvent WebEvent WindowEvent WorkerStateEvent])

#_(defn- static-fields-str [^Class c]
  (->> (:members (clojure.reflect/reflect c))
       (remove :return-type)
       (filter #(clojure.set/superset? (:flags %) #{:public :static}))
       (map #(select-keys % [:name :declaring-class]))
       (map #(str (:declaring-class %) "/" (:name %)))
       (map #(clojure.string/replace % #".*\.(.*)/(.*)" "$1/$2"))))

(def ^:private event-typenames
  [ActionEvent/ACTION ContextMenuEvent/CONTEXT_MENU_REQUESTED DragEvent/ANY
   DragEvent/DRAG_DONE DragEvent/DRAG_DROPPED DragEvent/DRAG_ENTERED DragEvent/DRAG_ENTERED_TARGET DragEvent/DRAG_EXITED
   DragEvent/DRAG_EXITED_TARGET DragEvent/DRAG_OVER GestureEvent/ANY InputEvent/ANY InputMethodEvent/INPUT_METHOD_TEXT_CHANGED

   KeyEvent/ANY KeyEvent/KEY_PRESSED KeyEvent/KEY_RELEASED KeyEvent/KEY_TYPED MediaErrorEvent/MEDIA_ERROR
   MouseDragEvent/ANY MouseDragEvent/MOUSE_DRAG_ENTERED MouseDragEvent/MOUSE_DRAG_ENTERED_TARGET MouseDragEvent/MOUSE_DRAG_EXITED
   MouseDragEvent/MOUSE_DRAG_EXITED_TARGET MouseDragEvent/MOUSE_DRAG_OVER MouseDragEvent/MOUSE_DRAG_RELEASED
   MouseEvent/ANY MouseEvent/DRAG_DETECTED MouseEvent/MOUSE_CLICKED MouseEvent/MOUSE_DRAGGED MouseEvent/MOUSE_ENTERED
   MouseEvent/MOUSE_ENTERED_TARGET MouseEvent/MOUSE_EXITED MouseEvent/MOUSE_EXITED_TARGET MouseEvent/MOUSE_MOVED
   MouseEvent/MOUSE_PRESSED MouseEvent/MOUSE_RELEASED RotateEvent/ANY RotateEvent/ROTATE RotateEvent/ROTATION_FINISHED

   RotateEvent/ROTATION_STARTED ScrollEvent/ANY ScrollEvent/SCROLL ScrollEvent/SCROLL_FINISHED
   ScrollEvent/SCROLL_STARTED SwipeEvent/ANY SwipeEvent/SWIPE_DOWN SwipeEvent/SWIPE_LEFT
   SwipeEvent/SWIPE_RIGHT SwipeEvent/SWIPE_UP TouchEvent/ANY TouchEvent/TOUCH_MOVED TouchEvent/TOUCH_PRESSED TouchEvent/TOUCH_RELEASED
   TouchEvent/TOUCH_STATIONARY WebEvent/ALERT WebEvent/ANY WebEvent/RESIZED WebEvent/STATUS_CHANGED
   WebEvent/VISIBILITY_CHANGED WindowEvent/ANY WindowEvent/WINDOW_CLOSE_REQUEST WindowEvent/WINDOW_HIDDEN
   WindowEvent/WINDOW_HIDING WindowEvent/WINDOW_SHOWING WindowEvent/WINDOW_SHOWN
   WorkerStateEvent/ANY WorkerStateEvent/WORKER_STATE_CANCELLED
   WorkerStateEvent/WORKER_STATE_FAILED WorkerStateEvent/WORKER_STATE_READY
   WorkerStateEvent/WORKER_STATE_RUNNING WorkerStateEvent/WORKER_STATE_SCHEDULED WorkerStateEvent/WORKER_STATE_SUCCEEDED
   ZoomEvent/ANY ZoomEvent/ZOOM ZoomEvent/ZOOM_FINISHED ZoomEvent/ZOOM_STARTED])

;; この3つは NPE 他 exception 出る。
; Event/NULL_SOURCE_TARGET
; Event/ANY
; KeyEvent/CHAR_UNDEFINED

;            (assoc m (trim-any (const->key (str c) "on-"))

(def ^:private event-map
  (reduce (fn [m c]
            (assoc m (trim-any (const->key (str c)))
               c)) {} event-typenames))

(defn- event-exists? [k] (not (nil? (event-map k))))

(defn add-filter!
  "指定ノード target のイベントタイプ event-key に関数 f を *イベントフィルタとして* 登録。

   remove-filter! する必要がある場合はあらかじめ f に名前を付けておくこと推奨。"
  [^Node target event-key f]
  {:pre [(event-exists? event-key)]}
  (.addEventFilter target (event-map event-key) (listener :event f)))

(defn add-handler!
  "指定ノード target のイベントタイプ event-key に関数 f を *イベントハンドラとして* 登録。

   remove-filter! する必要がある場合はあらかじめ f に名前を付けておくこと推奨。"
  [^Node target event-key f]
  {:pre [(event-exists? event-key)]}
  (.addEventHandler target (event-map event-key) (listener :event f)))

(defn remove-filter!
  "指定ノード target のイベントタイプ event-key に登録された *イベントハンドラ関数* f を削除。

   remove-filter! する必要がある場合はあらかじめ f に名前を付けておくこと推奨。"
  [^Node target event-key f]
  {:pre [(event-exists? event-key)]}
  (.removeEventFilter target (event-map event-key) (listener :event f)))

(defn remove-handler!
  "指定ノード target のイベントタイプ event-key に登録された *イベントハンドラ関数* f を削除。

   remove-filter! する必要がある場合はあらかじめ f に名前を付けておくこと推奨。"
  [^Node target event-key f]
  {:pre [(event-exists? event-key)]}
  (.removeEventHandler target (event-map event-key) (listener :event f)))
