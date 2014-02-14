(ns ^{:doc "UI コントロール生成関数。FXML 読込をメインとしてるのでここの機能追加は後回し。"}
  cljfx.control)

(defmacro ^:private defcontrol [control-name class-name]
  `(defn ~control-name []
     (new ~class-name)))

;; 動的に作りそうなもののみ
(defcontrol button javafx.scene.control.Button)
(defcontrol choice-box javafx.scene.control.ChoiceBox)
(defcontrol combo-box javafx.scene.control.ComboBox)
(defcontrol list-view javafx.scene.control.ListView)
(defcontrol table-row javafx.scene.control.TableRow)
(defcontrol table-view javafx.scene.control.TableView)
(defcontrol tree-view javafx.scene.control.TreeView)
