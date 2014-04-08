(ns cljfx.examples.dnd
  (:import [javafx.scene.input TransferMode ClipboardContent]
           javafx.scene.paint.Color)

  (:use cljfx.core))

;; 元ネタ: http://docs.oracle.com/javafx/2/drag_drop/HelloDragAndDrop.java.html
;; source -> target へ D&D するとテキストが入れ替わります

;; lein run -m cljfx.examples.dnd/hello-dnd

(defn hello-dnd []
   (let [root (load-fxml "dnd.fxml")
         source (fseek root "#source")
         target (fseek root "#target")]

     (v! source 
         :on-drag-detected (listened [_ e]
                             (println "onDragDetected.")
                             (let [db (.startDragAndDrop source TransferMode/ANY)
                                   content (ClipboardContent.)]
                               (.putString content (.getText source))
                               (.setContent db content))))

     (v! target
         :on-drag-over (listened [_ e]
                         (println "onDragOver")
                         (when (and (not= (.getGestureSource e) target)
                                    (.. e  getDragboard hasString))
                           (.acceptTransferModes e TransferMode/COPY_OR_MOVE)))

         :on-drag-entered (listened [_ e]
                            (println "onDragEntered")
                            (when (and (not= (.getGestureSource e) target)
                                       (.hasString (.getDragboard e)))
                              (v! target :fill Color/GREEN)))

         :on-drag-exited (listened [_ e] (v! target :fill Color/BLACK))

         :on-drag-dropped (listened [_ e]
                            (println "onDragDropped")
                            (when-let [txt (.. e getDragboard getString)]
                              (.setText source (.getText target))
                              (.setText target txt)
                              (.setDropCompleted e true))))

     (launch root)))
