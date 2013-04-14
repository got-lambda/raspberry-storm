(ns raspberry-storm.bolts
  "Bolts.

More info on the Clojure DSL here:

https://github.com/nathanmarz/storm/wiki/Clojure-DSL"
  (:require [backtype.storm [clojure :refer [emit-bolt! defbolt ack! bolt]]]))

(defbolt stormy-bolt ["stormy"] [{{text :text} :tweet :as tuple} collector]
  (emit-bolt! collector [(if (< 50 (count text))
                           "I'm a short tweet!"
                           "I'm a long tweet!")]
              :anchor tuple)
  (ack! collector tuple))

(defbolt raspberry-storm-bolt ["message"] [{stormy :stormy :as tuple} collector]
  (emit-bolt! collector [(str "raspberry-storm produced: "stormy)] :anchor tuple)
  (ack! collector tuple))
