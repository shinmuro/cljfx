# cljfx
JavaFX 簡易ラッパーデモ。一応一つだけデモが起動します。

### サンプル実行
* git クローン
このリポジトリをクローンするなり zip ダウンロードするなりします。

* JavaFX ランタイムを Maven ローカルリポジトリに追加
JavaFX は Java7 から jar で標準であるもののクラスパスは通っていない為まず Maven ローカルリポジトリに追加します。

Maven ローカルリポジトリに追加する為に JavaFX のバージョンを確認する必要があります。バージョンは %JAVA_HOME%\lib\jre\jfxrt.properties の中に書かれています。

そのバージョンを元に、maven ローカルリポジトリに登録する為に以下を実行します。
mvn install:install-file -DgroupId=local.oracle -DartifactId=javafxrt -Dversion=2.2.45 -Dpackaging=jar -Dfile=jfxrt.jar

-Dversion= 以降にあるものがバージョン番号になります。先ほど確認した番号と変わらなければそのままコピペして実行
するだけで構いません。バージョンが異なる場合はコマンドのバージョン指定の部分を適時変えて下さい。

バージョンを変更して登録した場合は、クローンしたリポジトリフォルダの project.clj の :dependencies を併せて変更します。

```Clojure
[local.oracle/javafxrt "2.2.45"]

そして、プロジェクトフォルダへ移動し、以下を実行します。

```
lein run -m cljfx.examples.draggable/draggable-panel

名前から分かるかもしれませんが、公式 JavaFX チュートリアルのイベントフィルタリングのサンプルを移植してみました。
作りは UI デザインを SceneBuiler で行い、イベントハンドリングその他 SceneBuilder では対応できなかった部分を
Clojure で行っています。

[オリジナルの Java サンプル](http://docs.oracle.com/javafx/2/events/DraggablePanelsExample.java.htm)
[サンプルを解説した記事](http://docs.oracle.com/javafx/2/events/filters.htm)

# 以下目標など
## 動機
- REPL で動作確認したい
- GUI デザイン骨組みは Scene Builder 任せにしたい
- なるべく seesaw に近づけたい
- TableView は早めに使えるようにしたい

## 利点
- REPL で動かせる
- デザインを FXML に任せた結果コードでやる事は draggable-panel サンプルで見る限り
  - Observable なアイテム追加
  - バインド
  - イベントハンドリング
  に絞られる
- コントローラクラスとか知りません
- Node 派生クラスはほぼ何もラップしてないが、単なる Java Interop でコンストラクタ呼べば大体使えるはず

## 欠点
- 画面閉じた後は REPL 再起動必要
  - JavaFX の元々がそうなってるためどうしようもありません
- コードからの動的生成はほぼ考えてない
- FXML 及び Node の id 依存(fx:id 使えないし Clojure で @FXML を使うのは面倒なので)

## 目指してる事
- 極力分かり易さを維持して少ないコードで書けるようにする
- 少し使い易いバインド
- 少し使い易い TableView, ListView

## できれば対応したい事
- カスタム UI コントロール対応
  - ControlsFX とか良さそうだからね

## あんまり目指していないこと
- Clojure コードからの動的な UI 生成
- アニメーション・エフェクト
元々それら目的で JavaFX を使うつもりもない

- fx:id 対応
...id で検索できるのに名前を何種類も持つ意味を感じない
...が、標準的なやりかたでない事は分かってるので JavaFX 自体に動きがあればその時に考える

# 謝辞
upshot 始め色々参考にさせてもらいました

Copyright © 2014 shinmuro

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.
