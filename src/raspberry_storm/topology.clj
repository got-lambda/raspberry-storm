(ns raspberry-storm.topology
  "Topology

More info on the Clojure DSL here:

https://github.com/nathanmarz/storm/wiki/Clojure-DSL"
  (:require [raspberry-storm
             [spouts :refer [twitter-spout]]
             [bolts :refer [stormy-bolt raspberry-storm-bolt]]]
            [backtype.storm [clojure :refer [topology spout-spec bolt-spec]] [config :refer :all]])
  (:import [backtype.storm LocalCluster LocalDRPC]))

(defn stormy-topology []
  (topology
   {"spout" (spout-spec twitter-spout)}

   {"stormy-bolt" (bolt-spec {"spout" ["tweet"]} stormy-bolt :p 2)
    "raspberry-storm-bolt" (bolt-spec {"stormy-bolt" :shuffle} raspberry-storm-bolt :p 2)}))

(defn run! [& {debug "debug" workers "workers" :or {debug "true" workers "2"}}]
  (doto (LocalCluster.)
    (.submitTopology "stormy topology"
                     {TOPOLOGY-DEBUG (Boolean/parseBoolean debug)
                      TOPOLOGY-WORKERS (Integer/parseInt workers)}
                     (stormy-topology))))