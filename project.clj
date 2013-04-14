(defproject raspberry-storm "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.4.0"]]
  :aot [raspberry-storm.TopologySubmitter]
  :resource-paths ["config"]
  ;; include storm dependency only in dev because production storm cluster provides it
  :profiles {:dev {:dependencies [[storm "0.8.1"]
                                  [lamina "0.5.0-beta15"]
                                  [twitter-api "0.7.3"]]}})
