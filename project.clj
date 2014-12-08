(defproject cljfx "0.1.19"
  :description "JavaFX Clojure ラッパ"
  :url "https://github.com/shinmuro/cljfx"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [local.oracle/javafxrt "2.2.72"]
                 [local.javafx-ui-sandbox/javafxrt2-dialogs "0.0.4"]]

  :codox {:exclude cljfx.deprecated.bind}
  :profiles {:dev {:resource-paths ["sample-resources"]}}

  :aot [cljfx.primary cljfx.core]
  :target-path "target/%s")
