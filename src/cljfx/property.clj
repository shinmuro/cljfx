(ns cljfx.property
  "JavaFX Property を扱う関数。"

  (:import javafx.beans.binding.BooleanExpression)

  (:use cljfx.util)

  (:require [clojure.reflect :as r]
            [clojure.string :as s]))

(defn- properties-fn
  "指定 JavaFX UI インスタンスのプロパティ情報を取得する。
   プロパティ名をキーとしたマップを返す。

   マップエントリの構成。
     キー→ :<property-name> Java プロパティ名をベースとした Clojure キーワード。
     値  → :name            プロパティの Java 名称文字列。
            :return-type     プロパティ値のデータ型。
            :flags           宣言時のアクセス修飾子。多分デバッグ時のみ使用。

   エントリの一例:
   :scale-x {:name \"scaleX\", :return-type javafx.beans.property.DoubleProperty, :flags #{:public :final}}

   注意事項:
     impl_XXX や private なプロパティも取得できてしまっているが、使っても多分例外になるので使用しない事。"
  [obj]
  (let [base-props
        (->> (:members (r/reflect obj :ancestors true))
             (map #(dissoc % :declaring-class :parameter-types :exception-types))
             (map #(update-in % [:name] str))
             (map #(update-in % [:return-type] str))
             (filter #(re-find #".*Property$" (:name %)))
             (map #(update-in % [:name] (fn [s] (s/replace s #"(.*)Property$" "$1"))))
             (map #(update-in % [:return-type] (fn [s] (Class/forName s)))))]
    (zipmap (map (comp keyword camel->dash :name) base-props) base-props)))

(def ^:private properties (memoize properties-fn))

;; TODO: ここエラーハンドリングきっちりしときたいが
(defn- clj-invoke
  [target meth & args]
  (try (clojure.lang.Reflector/invokeInstanceMethod target meth (to-array args))
       (catch Exception e
         (throw (IllegalArgumentException. (str "No matching method: " meth " on " (class target)))))))

(defn- upper-case-1st [s]
  (apply str (cons (Character/toUpperCase (first s)) (rest s))))

(defn- getter-str
  [target prop]
  (str (if (isa? (-> (properties target) prop :return-type) BooleanExpression) "is" "get")
       (-> (properties target) prop :name upper-case-1st)))

(defn v
  "JavaFX UI インスタンスのプロパティ値を取得する。"
  [target prop]
  (clj-invoke target (getter-str target prop)))

(defn- setter-str
  [target prop]
  (str "set" (-> (properties target) prop :name upper-case-1st)))

(defn v!
  "JavaFX UI インスタンスのプロパティ値を変更する。"
  ([target prop value] (clj-invoke target (setter-str target prop) value))
  ([target prop value & prop-values]
     {:pre [(even? (count prop-values))]}
     (v! target prop value)
     (doseq [pvs (partition 2 prop-values)]
       (v! target (first pvs) (second pvs)))))

(defn- prop-str
  [target prop]
  (str (-> (properties target) prop :name) "Property"))

(defn p
  "JavaFX UI インスタンスのプロパティそのものを取得する。主に bind 用。"
  [target prop]
  (clj-invoke target (prop-str target prop)))
