(ns cljfx.util
   "内部ユーティリティ関数。"

  (:require [clojure.string :as s]))

(defn camel->dash
  [^String s]
  (as-> (s/replace s #"([A-Z])" " $1") x
        (s/lower-case x)
        (s/triml x)
        (s/split x #" ")
        (s/join "-" x)))

(defn dash->camel
  "lisp ライクな命名をキャメルケース化する。オプションで :lcc を指定すれば先頭のみ小文字にする。
    [例]
    (dash->camel \"one-two-three\")
    ;=> \"OneTwoThree\"

    (dash->camel \"one-two-three\" :lcc)
    ;=> \"oneTwoThree\""
  [^String s & opts]
  (as-> (s/split s #"-") x
        (if (= (first opts) :lcc)
          (concat (first x) (map s/capitalize (rest x)))
          (map s/capitalize x))
        (apply str x)))

(defn const->key
  " FOO_BAR -> :foo-bar にする。prefix があれば :prefix-foo-bar にする。"
  [^String s & [prefix]]
  (keyword (apply str prefix (s/replace (s/lower-case s) "_" "-"))))

(defn trim-any [k]
  (keyword (s/replace (name k) #"(on-)*(any-)*(.*)" "$1$3")))
