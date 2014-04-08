# cljfx
JavaFX 簡易ラッパー(予定)。

現状 JavaFX8 は cljfx、と言うよりも Clojure で動きません([参考](http://tnoda-clojure.tumblr.com/post/80282499945/java-8-clojure-2-javafx))。

Java7 に含まれる JavaFX 2.2 系列を前提に作成中です。

## デモ実行
### git クローン

このリポジトリをクローンするか zip ダウンロードします。

### JavaFX ランタイムを Maven ローカルリポジトリに追加

JavaFX は Java7 から jar で標準であるもののクラスパスは通っていない為まず JavaFX のバージョ
ンを確認し、Maven ローカルリポジトリに追加します。バージョンは
%JAVA_HOME%\lib\jre\jfxrt.properties の中に書かれています。

そのバージョンを元に、maven ローカルリポジトリに登録する為に、先に書いたパスに移動した上で
``mvn install`` コマンドを実行しｍす。

```
mvn install:install-file -DgroupId=local.oracle -DartifactId=javafxrt -Dversion=2.2.45 -Dpackaging=jar -Dfile=jfxrt.jar
```

-Dversion= 以降にあるものがバージョン番号になります。先ほど確認した番号と変わらなければそのまま
コピペして実行するだけで構いません。バージョンが異なる場合はコマンドのバージョン指定の部分を適時変えて下さい。

### project.clj へ依存関係追加

バージョンを変更して登録した場合は、クローンしたリポジトリフォルダの project.clj の :dependencies を併せて変更します。

```Clojure
[local.oracle/javafxrt "2.2.45"]
```

### デモ
現状 4 つほどサンプルがあります。プロジェクトフォルダへ移動して下記 leiningen コマンドを実行すればサンプルアプリが動きます。

```
lein run -m cljfx.examples.draggable/draggable-panel
lein run -m cljfx.examples.draggable/change-listener
lein run -m cljfx.examples.dnd/hello-dnd
lein run -m cljfx.examples.keyboard/keyboard-example
```

### サンプルデモについて
構成は UI デザインを主に Scene Builder で行い、Scene Builder では無理(でもない？)なイベントハンドリングなどを
Clojure で行ってます。

### ライブラリとしての使用
現在 examples に含まれるコードを少しずつ追加しながら API を固めている段階です。
ですので一度公開していた API doc は、単なるノイズでしかないように思えてきましたので一度削除させてもらいました。
申し訳ありません。

## 以下目標など
### 動機
- REPL で動作確認したい
- GUI デザイン骨組みは Scene Builder 任せにしたい
- なるべく seesaw に近づけたい
- TableView は早めに使えるようにしたい

### 利点
- REPL で動かせる
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
- カスタム UI コントロール対応

# 謝辞
upshot 始め色々参考にさせてもらいました。

Copyright © 2014 shinmuro

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.
