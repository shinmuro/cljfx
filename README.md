# cljfx
JavaFX 簡易ラッパーデモ。一応一つだけデモが起動します。

## デモ実行
### git クローン

このリポジトリをクローンするなり zip ダウンロードするなりします。

### JavaFX ランタイムを Maven ローカルリポジトリに追加

JavaFX は Java7 から jar で標準であるもののクラスパスは通っていない為まず JavaFX のバージョンを確認し、Maven ローカルリポジトリに追加します。バージョンは %JAVA_HOME%\lib\jre\jfxrt.properties の中に書かれています。
そのバージョンを元に、maven ローカルリポジトリに登録する為に以下を実行します。

```
mvn install:install-file -DgroupId=local.oracle -DartifactId=javafxrt -Dversion=2.2.45 -Dpackaging=jar -Dfile=jfxrt.jar
```
-Dversion= 以降にあるものがバージョン番号になります。先ほど確認した番号と変わらなければそのまま
コピペして実行するだけで構いません。バージョンが異なる場合はコマンドのバージョン指定の部分を適時変えて下さい。

### project.clj へ依存関係追加

バージョンを変更して登録した場合は、クローンしたリポジトリフォルダの project.clj の :dependenciesを併せて変更します。

```Clojure
[local.oracle/javafxrt "2.2.45"]
```

### デモ
そして、プロジェクトフォルダへ移動し、どれかを実行します。

```
lein run -m cljfx.examples.draggable/draggable-panel
```
[オリジナルの Java サンプル](http://docs.oracle.com/javafx/2/events/DraggablePanelsExample.java.htm)


```
lein run -m cljfx.examples.draggable/change-listener
```
公式サンプルである ensemble の ChangeListenerSample.java がベース

### サンプルデモについて
構成は UI デザインを主に Scene Builder で行い、Scene Builder では無理(でもない？)なイベントハンドリングなどを
Clojure で行ってます。

### ライブラリとしての使用
まだ API は自分の中でも全く固めていないのですが、それでも良ければ。固めた段階で 1.0.0 としたいなとはおぼろげに考えてます。

## 以下目標など
### 動機
- REPL で動作確認したい
- GUI デザイン骨組みは Scene Builder 任せにしたい
- なるべく seesaw に近づけたい
- TableView は早めに使えるようにしたい

### 利点
- REPL で動かせる
- デザインを FXML に任せた結果コードでやる事は draggable-panel サンプルで見る限り
  - Observable なアイテム追加
  - バインド
  - イベントハンドリング
  に絞られる
- コントローラクラスとか知りません
- Node 派生クラスはほぼ何もラップしてないが、単なる Java Interop でコンストラクタ呼べば大体使えるはず

### 欠点
- 画面閉じた後は REPL 再起動必要
  - JavaFX の元々がそうなってるためどうしようもありません
- コードからの動的生成はほぼ考えてない
- FXML 及び Node の id 依存(fx:id 使えないし Clojure で @FXML を使うのは面倒なので)

### 目指してる事
- 極力分かり易さを維持して少ないコードで書けるようにする
- 少し使い易いバインド
- 少し使い易い TableView, ListView

### できれば対応したい事
- カスタム UI コントロール対応
  - ControlsFX とか良さそうだからね

### あんまり目指していないこと
- Clojure コードからの動的な UI 生成
- アニメーション・エフェクト
元々それら目的で JavaFX を使うつもりもない

- fx:id 対応
id で検索できるのに名前を何種類も持つ意味を感じない
が、標準的なやりかたでない事は分かってるので JavaFX 自体に動きがあればその時に考える

# 謝辞
upshot 始め色々参考にさせてもらいました

Copyright © 2014 shinmuro

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.
