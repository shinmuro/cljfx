(defproject cljfx "0.1.0"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [local.oracle/javafxrt "2.2.45"]
                 [com.stuartsierra/class-diagram "0.1.0"]]
  :resource-paths ["resources"]
  :aot [cljfx.primary cljfx.core cljfx.coerce cljfx.util cljfx.listener
        cljfx.event cljfx.seek cljfx.property cljfx.control]
  :target-path "target/%s")
