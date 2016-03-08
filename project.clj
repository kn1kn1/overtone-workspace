(defproject overtone-workspace "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [quil "2.3.0"]
                 [org.clojure/data.json "0.2.3"]
                 [clj-native "0.9.3"]
                 [overtone/at-at "1.2.0"]
                 [overtone/osc-clj "0.9.0"]
                 [overtone/byte-spec "0.3.1"]
                 [overtone/midi-clj "0.5.0"]
                 [overtone/libs.handlers "0.2.0"]
                 [overtone/scsynth "3.5.7.0"]
                 [overtone/scsynth-extras "3.5.7.0"]
                 [clj-glob "1.0.0"]]
  :jvm-opts ^:replace []
)
