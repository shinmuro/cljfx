# cljfx
邪道な JavaFX 簡易ラッパーデモ。

# モチベーション
- REPL で動作確認したい
- GUI デザイン骨組みは Scene Builder 任せにしたい
- なるべく seesaw に近づけたい
- TableView は早めに使えるようにしたい

# 特徴
一応一つだけデモが起動します。

# 利点
- REPL で動かせる
- デザインを FXML に任せた結果コードでやる事は draggable-panel サンプルで見る限り
  - Observable なアイテム追加
  - バインド
  - イベントハンドリング
  に絞られる
- コントローラクラスとか知りません
- Node 派生クラスはほぼ何もラップしてないが、単なる Java Interop でコンストラクタ呼べば大体使えるはず

# 欠点
- 画面閉じた後は REPL 再起動必要
  - JavaFX の元々がそうなってるためどうしようもありません
- コードからの動的生成はほぼ考えてない
- FXML 及び Node の id 依存(fx:id 使えないし Clojure で @FXML を使うのは面倒なので)

# インストール
JavaFX は Java7 から jar で標準であるもののクラスパスは通っていない為まず Maven ローカルリポジトリに追加します。

Maven ローカルリポジトリに追加する為に JavaFX のバージョンを確認する必要があります。
バージョンは %JAVA_HOME%\lib\jre\jfxrt.properties の中に書かれています。

その後、project.clj の :dependencies に追加します。
[local.oracle/javafxrt "2.2.45"]

# サンプル実行
lein run -m cljfx.examples.draggable/draggable-panel

オリジナルの Java サンプル→ http://docs.oracle.com/javafx/2/events/DraggablePanelsExample.java.htm
サンプルを解説した記事    → http://docs.oracle.com/javafx/2/events/filters.htm

# 目指してる事
- 極力分かり易さを維持して少ないコードで書けるようにする
- 少し使い易いバインド
- 少し使い易い TableView, ListView

# できれば対応したい事
- カスタム UI コントロール対応
  - ControlsFX とか良さそうだからね

# あんまり目指していないこと
- Clojure コードからの動的な UI 生成
- アニメーション・エフェクト
元々それら目的で JavaFX を使うつもりもない

- fx:id 対応
id で検索できるのに名前を何種類も持つ意味を感じない
が、標準的なやりかたでない事は分かってるので JavaFX 自体に動きがあればその時に考える

# 謝辞
upshot 始め、以下の github プロジェクトのコードは参考にさせて頂いております。


Copyright © 2014 shinmuro

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.
