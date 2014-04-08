機能の充実よりまずは現時点の機能整理作業がメイン

- [ ] 定数らしきものを keyword 化
    - [x] KeyCode -> kbd
    - [ ] Color
    - [ ] EventType

    - 単なる keyword への名称変換だと値として使いたい時ちと困る
      (案) KeyEvent/KEY_PRESSED
           #event/type[key-pressed] ; 長くなってあんま意味ない
           :key/pressed             ; 分かり易いが値にしたい時変換が必要

           ; 単純な変換関数用意する
           (event :key/pressed)
           ;=> KeyEvent/KEY_PRESSED (が評価したのに相当するオブジェクト)

           (kbd "a")
           ;=> (symbol `(str KeyCode "/" s))

           ;=> KeyCode/A でなくこれを評価した結果を返さんと

           (color :white)

- [ ] when, then, otherwise 簡素化
    - と言っても 一番外の (.. ) が消えるだけな気がする
    - これ otherwise 省略できるんだっけ
      - API じゃできなくてもマクロで省略可にするのは有りかもしれん

- [ ] add, remove handler! で暗黙に listener かませてるのやっぱどうなんだろ

- [ ] KeyboardExample.java が when, then 使ったサンプルになってるので Clojure へ移植しがいありそう
    - [ ] FXML にしてみよう
      - Key を表すレイアウト 1 個は作ったが(#key って ID)、これを元に複製する方法が分からん
        - まずなんかのクラスを new したものを単純代入するのはムリ
            Foo a = new Foo();
            Foo b = a;
          この時 b は a の参照がコピーされるだけ。
          なので、a.someField = someValue; とかすると
          b.someField も someValue に変わる。

        - java.lang.Object#clone() な方法
          これはシャローコピーとか呼ばれ、フィールドがプリミティブなものは
          実体をコピーするが、フィールドがオブジェクトの場合は参照のコピーとなる。
          つまり最初の new したのを単純代入した時と同じ扱い。

          また、シャローコピーで要件を満たす場合でも、clone しようとする Class は 
          Cloneable でなくてはならない(Interface 実装)とかの作法が必要。
          マーカーインターフェースのテクをここで使ってるとか。

        - ディープコピー
          いやフィールドがオブジェクトだろうが何だろうが丸ごとコピーしたい時。
          Java ではディープコピーと呼ばれるものの、標準的な方法はない。

          いくつか外部ライブラリも存在してるみたいだが使い勝手は不明。

- [ ] listener マルチメソッドの docstring 充実
- [ ] primary もう少し使いやすくならんか
    - まだちょっと no-doc 対象にはしづらい
- [ ] javafxpackager 設定、cljfx 化しても使えるか確認(-appclass 指定が primary 固定になるだけでは)
- [ ] Leiningen プラグイン化
- [ ] TableView のラッパー実装どうするか
- [x] プロパティに名前付けてるなあ→コード見たら意味なかった
    - [x] (defprop pressed value) っての作った方がいいか？
        - よく見ると KeyboardExample.java は単にローカル変数名とプロパティ名を手で合わせてるだけだった
        - 中身は (def pressed (SimpleBooleanProperty. obj "pressed")) とかに展開するマクロ
          API は引数なしのコンストラクタも呼べるみたいだが、初期値指定は有りでいいかな
        - これで p, v, v! で上のシンボルの :pressed プロパティにアクセスできる
