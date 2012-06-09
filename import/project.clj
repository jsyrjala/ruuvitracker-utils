(defproject import "1.0.0-SNAPSHOT"
  :description "FIXME: write description"
  :dependencies [
                 [org.clojure/clojure "1.4.0"]
                 [joda-time/joda-time "2.1"]
                 [clj-http "0.4.3"]
                 [clj-json "0.5.0"]
                 [ch.qos.logback/logback-classic "1.0.3"]
                 [org.slf4j/log4j-over-slf4j "1.6.4"]
                 [org.clojure/tools.logging "0.2.3"
                  :exclusions [log4j/log4j
                               commons-logging/commons-logging
                               org.slf4j/slf4j-api
                               org.slf4j/slf4j-log4j12]]
                 
                 ]
  :dev-dependencies[
                    [midje "1.4.0"]
                    [lein-midje "1.0.10"]
                    ]

  )