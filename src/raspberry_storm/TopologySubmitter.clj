(ns raspberry-storm.TopologySubmitter
  (:require [raspberry-storm.topology :refer [stormy-topology]]
            [backtype.storm [config :refer :all]])
  (:import [backtype.storm StormSubmitter])
  (:gen-class))

(defn -main
  [& {debug "debug" workers "workers" :or {debug "false" workers "4"}}]
  (comment (StormSubmitter/submitTopology
            "raspberry-storm"
            {TOPOLOGY-DEBUG (Boolean/parseBoolean debug)
             TOPOLOGY-WORKERS (Integer/parseInt workers)}
            (stormy-topology))))
