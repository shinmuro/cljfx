(ns 'cljfx.core.deprecated.bind)

; 恐らくどこにも使われなくなるが一応しばらく残しておく。
(import javafx.beans.binding.ObjectBinding
        javafx.beans.binding.MapBinding)

(defmacro ^{:deprecated true
            :doc
  "DEPRECATED.

   多分近い用法でちゃんと Bind を生成する make-bindings を発展させていけば事足りる。

   JavaFX 低レベル Bind API をやや簡素化するマクロ。

   bindings には一時変数名シンボル と Property 値のペアを指定する事。
   with-open の指定方法と似ているが、最初のペアのみ特別で
   JavaFX での Bind した Properties に *依存する Property になる事に* 注意。
   (依存する Property -> a.bind(b) で言う a の側)

   body には bind されたものを計算する式を入れる。
   マクロの結果は Binding オブジェクトを返す。

   また、binding の先頭で指定した Property の値についても以後ここの計算結果が返ってくるようになる。"}
  bind-let
  [bindings & body]
  (cond
   (zero? (count bindings))
   (assert "an even number of forms in binding vector")
   
   (and (even? (count bindings))
        (every? symbol? (take-nth 2 bindings)))
   `(let [~@bindings
          args# ~(vec (take-nth 2 (subvec bindings 2)))
          instance#
          (proxy [ObjectBinding] []
            (~'bind [args#]
              (println "bounding.")
              (proxy-super ~'bind (into-array javafx.beans.Observable args#)))
            (~'computeValue []
              (println "compute.")
              ~@body))]
      (.bind instance# args#)
      (.bind ~(bindings 0) instance#) ; src との bind
      instance#)))

;;; これ要らなくなるかも。
;(defprotocol Bindable
;  "JavaFX bind 簡易 wrapper。
;   javafx.beans.property.Property インターフェースを実装していれば大概使える、はず。
;   言い換えたら、JavaFX API doc にある Property とドキュメントされてるものは多分大丈夫。"
;  (bind-> [src other]
;    "高レベル API 単方向バインド。this は other に依存する形。
;     JavaFX Bind 元々の仕様により this を直接変更しようとすると RuntimeException が発生する。")
;
;  (bind   [src deps compute-fn]
;    "低レベルバインド。javafx.beans.binding.Binding 実装クラスで使用可。
;     deps はシーケンスで複数指定可。
;
;     compute-fn には this へ bind する処理内容を引数なしの関数として渡す。")
;
;  (bind_  [src other] "双方向バインド。でもしばらく使わないので実装は無し。"))
;
;
;;; proxy-super は macro だから apply できないんだよな。どうすっか。
;(extend-type javafx.beans.property.Property
;  Bindable
;  (bind [src deps compute-fn]
;    (.bind src
;           (proxy [ObjectBinding] []
;             (bind [] (proxy-super bind deps))
;             (computeValue [] (compute-fn))))))
