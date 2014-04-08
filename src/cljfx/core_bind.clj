(in-ns 'cljfx.core)

(import javafx.beans.Observable
        javafx.beans.binding.ObjectBinding)

;; javafx.beans.binding.ObjectBinding#bind() を見えるようにする為だけの継承クラス
(gen-class
 :name "cljfx.binding.ObjectBinding"
 :extends javafx.beans.binding.ObjectBinding
 :exposes-methods {bind bindAll})

(defmacro make-bindings
  "JavaFX 低 レベル Bind 生成マクロ。
   deps には let と同様の形式でシンボル、値で続く指定可。但し値は JavaFX Property である事。
   body には Bind 先の値となる計算式を入れる。body の最後の評価結果が Bind 先の値になる。

   ObjectBinding を生成するので、Property.bind() が使える Property であればどれでも使える、はず。
   但しバインド先の型は見ないので body で適切な型の値を返すようにする事。"
  [deps & body]
  (cond
   (not (even? (count deps)))
   (throw (IllegalArgumentException. ": deps は偶数でないとダメです。"))

   :else
   `(let ~deps
      (doto (proxy [cljfx.binding.ObjectBinding] []
              (computeValue [] ~@body))
        (.bindAll (into-array Observable [~@(take-nth 2 deps)]))))))
