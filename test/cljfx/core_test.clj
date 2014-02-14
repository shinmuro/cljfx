(ns wrap-jfx.core-test
  (:require [clojure.test :refer :all]
            [wrap-jfx.core :refer :all]
            [com.stuartsierra.class-diagram :as diagram]))

;; クラスの相関確認
;(doseq [c [;; Node の一番下
;           javafx.scene.control.ColorPicker
;           javafx.scene.control.ComboBox
;           javafx.scene.control.Button
;           javafx.scene.control.CheckBox
;           javafx.scene.control.Hyperlink
;           javafx.scene.control.SplitMenuButton
;           javafx.scene.control.RadioButton
;           javafx.scene.control.cell.CheckBoxListCell
;           javafx.scene.control.TableRow
;           javafx.scene.control.cell.CheckBoxTableCell
;           javafx.scene.control.cell.CheckBoxTreeCell
;           javafx.scene.control.PasswordField
;           javafx.scene.chart.NumberAxis
;           javafx.scene.chart.AreaChart
;           javafx.scene.layout.AnchorPane
;           javafx.util.converter.ShortStringConverter
;
;           ;; バインディング
;           javafx.beans.property.SimpleDoubleProperty
;           javafx.beans.property.SimpleBooleanProperty
;
;           ;; イベント
;           javafx.scene.input.MouseDragEvent]]
;  (diagram/png (str c) c))

(deftest a-test
  (testing "FIXME, I fail."
    (is (= 0 1))))
