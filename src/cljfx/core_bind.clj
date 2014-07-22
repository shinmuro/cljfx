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

(defmacro bind-nodes-prop
  "make-bindings の変種。bind するものが特定 node の同一プロパティ時であり、
   ローカル変数名が不要な場合に便利なマクロ。"
  [prop nodes & body]
  (letfn [(binding-form [prop node]
            `(~(gensym) (p ~node ~prop)))]
    `(make-bindings ~(vec (mapcat (partial binding-form prop) nodes))
       ~@body)))

(defprotocol IBindable
  "JavaFX Binding 用プロトコル。双方向 Binding は現在未対応。"
  (bind!
    [src dep]
    [src key dep]
    [src src-key dep dep-key]
    "bound <- dep Binding。bound の値は dep に依存する。この関数自体は常に nil を返す。
     利便性の為にいくつかの指定方法を定義。

     1. [src dep]
        src, dep それぞれに対して Property or Bindings を指定する一番基本的な呼び方。

     2. [src key dep]
       - src を Node で指定した場合に src が持つ Property を key に指定する。
         (bind b :text (p txt :text))

     3. [src src-key dep dep-key]
       - Node に存在する Property 同士を bind する場合有効。
         (bind b :text txt :text)

         p をかましているだけなので 1. でも構わないと言えば構わない。")

  (unbind! [x] [x key] "単なる unbind 呼び出し。Java interop そのままでも良いが bind! の対として用意。")
  (bounds  [x] "未実装。大元の getDependencies も未実装なんでどうしようかなと。"))

(extend-protocol IBindable

  javafx.beans.property.Property
  (bind! [bound dep] (.bind bound dep))
  (unbind! [x] (.unbind x))

  javafx.scene.Node
  (bind!
    ([node key dep]
       (bind! (p node key) dep))
    ([bound-obj bound-key dep-obj dep-key]
       (bind! (p bound-obj bound-key) (p dep-obj dep-key))))

  (unbind! [x key] (.unbind (p x key))))
