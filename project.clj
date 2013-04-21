(defproject raspberry-storm "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [storm "0.8.2"]
                 [lamina "0.5.0-beta15"]
                 [twitter-api "0.7.3"]]
  :jar-exclusions     [#"log4j\.properties" #"backtype" #"trident"
                       #"META-INF" #"meta-inf" #"\.yaml"]
  :uberjar-exclusions [#"log4j\.properties" #"backtype" #"trident"
                       #"META-INF" #"meta-inf" #"\.yaml"]
  :aot :all)
