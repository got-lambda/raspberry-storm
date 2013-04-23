(ns raspberry-storm.word-count
  (:require
   [backtype.storm [clojure :refer [defspout spout emit-spout!]]]
   [clojure.java.io :as io]
   [clojure.data.json :as json]
   [http.async.client :as ac])
  (:import
   [java.io PushbackReader])
  (:require [backtype.storm [clojure :refer [emit-bolt! defbolt ack! bolt]]])
  (:require [raspberry-storm
             [spouts :refer [twitter-spout]]
             [bolts :refer [stormy-bolt raspberry-storm-bolt]]]
            [backtype.storm [clojure :refer [topology spout-spec bolt-spec]] [config :refer :all]])
  (:import [backtype.storm LocalCluster LocalDRPC]))

(def poem
  "I, too, sing America.

I am the darker brother.
They send me to eat in the kitchen
When company comes,
But I laugh,
And eat well,
And grow strong.

Tomorrow,
I'll be at the table
When company comes.
Nobody'll dare
Say to me,
Eat in the kitchen,\"
Then.

Besides,
They'll see how beautiful I am
And be ashamed--

I, too, am America.\"")

(def queue
  (atom (into [] (.split poem "\n"))))

(defspout sentences ["sentence"]
  [conf context collector]
  (spout
   (nextTuple []
              (while (= (count @queue) 0)
                (Thread/sleep 100))
              (let [sentence (peek @queue)]
                (swap! queue pop)
                (emit-spout! collector [sentence])))
   (ack [id])))

(defbolt wordiser ["word"]
  [touple collector]
  (doseq [word (.split (.getString touple 0) " ")]
    (emit-bolt! collector [word]))
  (ack! collector touple))

(defbolt counter ["word" "count"] {:prepare true}
  [conf context collector]
  (.println System/out "-------------COUNTER RESTART-----------------")
  (let [counter (atom {})]
    (bolt
     (execute [touple]
              (let [word (.getString touple 0)]
                (swap! counter
                       (fn [m1 m2] (merge-with + m1 m2)) {word 1})
                (emit-bolt! collector [word (get @counter word)])
                (.println System/out @counter)
                (ack! collector touple))))))

(defn word-count-top
  []
  (topology
   {"sentence-emiter" (spout-spec sentences)}
   {"wordiser" (bolt-spec
                {"sentence-emiter" :shuffle}
                wordiser :p 5)
    "counter" (bolt-spec
               {"wordiser" ["word"]}
               counter :p 5)}))

(defn submit-local
  []
  (doto (LocalCluster.)
    (.submitTopology
     "word counter"
     {TOPOLOGY-DEBUG false
      TOPOLOGY-WORKERS 2}
     (word-count-top))))
