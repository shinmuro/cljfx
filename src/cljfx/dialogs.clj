(ns cljfx.dialogs
  "[javafx-dialogs](https://github.com/marcojakob/javafx-ui-sandbox/tree/master/javafx-dialogs)
   を利用した JavaFX 2.2 用ダイアログ API ラッパ。

   このラッパーを使うメリットは
     - import 簡略化
     - Java interop での static method 呼び出しが少し短くなる
     - 戻り値の Java static field 名が Clojure Keyword として返される
   事のみ。

   Java interop に抵抗がなければ dependencies に追加するだけにして
   この ns は use/require せず そのまま使っても構わない。

   注意点として JavaFX Application Thread 上でしか動かせない為、
   REPL 上でテスト実行する場合は run-later の boby 下で呼び出す必要がある。

   maven central にさえ deploy されていれば cljfx.core(core_dialogs.clj)
   に入れても構わないかもしれない。作者にモチベは無く他の人がやる必要あるが。"

  (:import [javafx.scene.control Dialogs Dialogs$DialogOptions Dialogs$DialogResponse]
           javafx.stage.FileChooser
           javafx.stage.FileChooser$ExtensionFilter))

(def ^:private opt-map
  {:dialog.opt/yes-no        Dialogs$DialogOptions/YES_NO
   :dialog.opt/yes-no-cancel Dialogs$DialogOptions/YES_NO_CANCEL
   :dialog.opt/ok            Dialogs$DialogOptions/OK
   :dialog.opt/ok-cancel     Dialogs$DialogOptions/OK_CANCEL})

(def ^:private ret-map
  {Dialogs$DialogResponse/YES    :dialog/yes
   Dialogs$DialogResponse/NO     :dialog/no
   Dialogs$DialogResponse/CANCEL :dialog/cancel
   Dialogs$DialogResponse/OK     :dialog/ok
   Dialogs$DialogResponse/CLOSED :dialog/closed})

(defn information-dialog
  "showInformationDialog() メソッド呼び出し。"
  ([stage message]
     (Dialogs/showInformationDialog stage message))
  ([stage message header]
     (Dialogs/showInformationDialog stage message header))
  ([stage message header title]
     (Dialogs/showInformationDialog stage message header title)))

(defn confirm-dialog
  "showConfirmDialog() メソッド呼び出し。"
  ([stage message]
     (ret-map (Dialogs/showConfirmDialog stage message)))
  ([stage message header]
     (ret-map (Dialogs/showConfirmDialog stage message header)))
  ([stage message header title]
     (ret-map (Dialogs/showConfirmDialog stage message header title)))
  ([stage message header title opt]
     (ret-map (Dialogs/showConfirmDialog stage message header title (opt-map opt)))))

(defn input-dialog
  "showInputDialog() メソッド呼び出し。"
  ([stage message]
     (Dialogs/showInputDialog stage message))
  ([stage message header]
     (Dialogs/showInputDialog stage message header))
  ([stage message header title]
     (Dialogs/showInputDialog stage message header title))
  ([stage message header title value]
     (Dialogs/showInputDialog stage message header title value))
  ([stage message header title value lis]
     (Dialogs/showInputDialog stage message header title value lis)))


(defn- set-ext-filters! [fc & filters]
  "FileChooser の Extension Filter 設定。"
  (when (odd? (count filters))
    (throw (IllegalArgumentException.
            "filter は description, [extensions] のペアで偶数で指定して下さい。")))
  (let [ext-filters (map (fn [[d e]] (FileChooser$ExtensionFilter. d (seq e)))
                         (partition 2 filters))]
    (.. fc getExtensionFilters clear)
    (.. fc getExtensionFilters (addAll (into-array ext-filters)))))

(defn file-chooser
  "ファイル選択ダイアログを開く。
   どの Stage(Window) インスタンスで開くか owner に設定する必要がある。
   mode で開くダイアログの種類を設定可能。

     :open       ファイルを開くダイアログ
     :open-multi ファイルを開くダイアログ(複数)
     :save       ファイル保存ダイアログ

   filters には文字列で description, [extensions] を必ずペアで指定する。
   [extensions] は文字列で 1 個以上のシーケンスで指定する。
   上記記述に従ったペアでさえあれば複数指定可。

     例: 
       (file-chooser :open stage
                     \"ファイルを開く\" nil
                     \"Excel 97-2003 ブック\" [\"*.xls\"])

   指定されると File オブジェクトを返す。"
  [mode owner title fname & filters]
  (let [fc (FileChooser.)]
    (doto fc
      (.setTitle title)
      (.setInitialFileName fname))
    (when filters (apply set-ext-filters! fc filters))
    (case mode
      :open       (.showOpenDialog fc owner)
      :open-multi (.showOpenMultipleDialog fc owner)
      :save       (.showSaveDialog fc owner)
      (throw
       (IllegalArgumentException. "Invalid filter type. It can choose only :open, :open-multi, :save")))))


(comment
  ;; テスト
  (input-dialog stage "エリア選択：" "エリアを選びます" "エリア選択" "b" '("a" "b" "c"))

  ;; テスト
  (def stage (cljfx.primary/-getPrimaryStage))

  (confirm-dialog stage "メッセージのみ")
  (confirm-dialog stage "メッセージ" "ヘッダまで")
  (confirm-dialog stage "メッセージ" "ヘッダまで" "タイトルまで")
  (confirm-dialog stage "YES,NO" "オプション変更" "オプション変更テスト" 
                  Dialogs$DialogOptions/YES_NO)

  (confirm-dialog stage "OKだけ" "オプション変更" "オプション変更テスト" 
                  Dialogs$DialogOptions/OK)
  (confirm-dialog stage "YES,NO" "オプション変更" "オプション変更テスト" 
                  Dialogs$DialogOptions/YES_NO)

)


;; やる事あまり変わらないのだが面倒 + すぐには使わない為未実施分の Java 定義。
;; 見易さの為すぐ分かる戻り値型は削除してる。
;; 
;; public class javafx.scene.control.Dialogs {
;;   showWarningDialog(javafx.stage.Stage, java.lang.String);
;;   showWarningDialog(javafx.stage.Stage, java.lang.String, java.lang.String);
;;   showWarningDialog(javafx.stage.Stage, java.lang.String, java.lang.String, java.lang.String);
;;   showWarningDialog(javafx.stage.Stage, java.lang.String, java.lang.String, java.lang.String, javafx.scene.control.Dialogs$DialogOptions);
;; 
;;   showErrorDialog(javafx.stage.Stage, java.lang.String);
;;   showErrorDialog(javafx.stage.Stage, java.lang.String, java.lang.String);
;;   showErrorDialog(javafx.stage.Stage, java.lang.String, java.lang.String, java.lang.String);
;;   showErrorDialog(javafx.stage.Stage, java.lang.String, java.lang.String, java.lang.String, javafx.scene.control.Dialogs$DialogOptions);
;;   showErrorDialog(javafx.stage.Stage, java.lang.String, java.lang.String, java.lang.String, java.lang.Throwable);
;; 
;;  <T extends java/lang/Object> T showInputDialog(javafx.stage.Stage, java.lang.String, java.lang.String, java.lang.String, T, T...);
;;   <T extends java/lang/Object> javafx.scene.control.Dialogs$DialogResponse showCustomDialog(javafx.stage.Stage, javafx.scene.layout.Pane, java.lang.String, java.lang.String, javafx.scene.control.Dialogs$DialogOptions, javafx.util.Callback<java.lang.Void, java.lang.Void>);
;; }
