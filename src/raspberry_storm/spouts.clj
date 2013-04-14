(ns raspberry-storm.spouts
  "Spouts.

More info on the Clojure DSL here:

https://github.com/nathanmarz/storm/wiki/Clojure-DSL"
  (:use
   [lamina.core]
   [twitter.oauth]
   [twitter.callbacks]
   [twitter.callbacks.handlers]
   [twitter.api.streaming])
  (:require
   [backtype.storm [clojure :refer [defspout spout emit-spout!]]]
   [clojure.java.io :as io]
   [clojure.data.json :as json]
   [http.async.client :as ac])
  (:import
   [twitter.callbacks.protocols AsyncStreamingCallback]
   [java.io PushbackReader]))

(def twitter-conf
  (binding [*read-eval* false]
    (with-open [r (io/reader "config/twitter.clj")]
      (read (PushbackReader. r)))))

(def oath-creds
  (make-oauth-creds (twitter-conf :consumer-token)
                    (twitter-conf :consumer-key)
                    (twitter-conf :access-token)
                    (twitter-conf :access-token-secret)))

(defspout twitter-spout ["tweet"]
  [conf context collector]

  (let [tweet-channel (channel)
        connection    (user-stream :oauth-creds oath-creds)
        callback      (AsyncStreamingCallback.
                       (fn on-body-part [response byte-stream]
                         (->> byte-stream
                              str
                              json/read-json
                              (enqueue tweet-channel)))
                       (fn on-failure [response]
                         (println response))
                       (fn on-exception [response]
                         (println response)))]

    ;; We only have access to the sample stream.
    (statuses-sample :params {}
                     :oauth-creds oath-creds
                     :callbacks callback)

    (spout
     (nextTuple []
                (emit-spout! collector [@(read-channel tweet-channel)]))
     (ack [id]
          ;; You only need to define this method for reliable spouts
          ;; (such as one that reads off of a queue like Kestrel)
          ;; This is an unreliable spout, so it does nothing here
          ))))

(def example-tweet
  {:favorite_count 0,
   :entities {:hashtags [],
              :urls [],
              :user_mentions []},
   :text "A de amor,B de briga,C de ciúmes,D de decepção,E de Emergência,F de FACADA... ♪♫",
   :retweet_count 0,
   :coordinates nil,
   :in_reply_to_status_id_str nil,
   :contributors nil,
   :in_reply_to_user_id_str nil,
   :id_str "323496429249257472",
   :in_reply_to_screen_name nil,
   :retweeted false,
   :truncated false,
   :created_at "Sun Apr 14 18:02:13 +0000 2013",
   :lang "pt",
   :geo nil,
   :place nil,
   :in_reply_to_status_id nil,
   :user {:profile_use_background_image true,
          :follow_request_sent nil,
          :default_profile false,
          :profile_sidebar_fill_color "E5507E",
          :protected false,
          :following nil,
          :profile_background_image_url "http://a0.twimg.com/images/themes/theme11/bg.gif",
          :default_profile_image false,
          :contributors_enabled false,
          :favourites_count 3,
          :time_zone nil,
          :name "Taise Bruning",
          :id_str "363270020",
          :listed_count 0,
          :utc_offset nil,
          :profile_link_color "B40B43",
          :profile_background_tile true,
          :location "",
          :statuses_count 1162,
          :followers_count 94,
          :friends_count 221,
          :profile_banner_url "https://si0.twimg.com/profile_banners/363270020/1359243943",
          :created_at "Sat Aug 27 20:27:26 +0000 2011",
          :lang "pt",
          :profile_sidebar_border_color "CC3366",
          :url nil,
          :notifications nil,
          :profile_background_color "FF6699",
          :geo_enabled false,
          :profile_image_url_https "https://si0.twimg.com/profile_images/3307451966/297b607f17f2640cf5066aee1c416414_normal.jpeg",
          :is_translator false,
          :profile_image_url "http://a0.twimg.com/profile_images/3307451966/297b607f17f2640cf5066aee1c416414_normal.jpeg",
          :verified false,
          :id 363270020,
          :profile_background_image_url_https "https://si0.twimg.com/images/themes/theme11/bg.gif",
          :description nil,
          :profile_text_color "362720",
          :screen_name "TaiseBruning"},
   :favorited false,
   :source "web",
   :id 323496429249257472,
   :in_reply_to_user_id nil,
   :filter_level "medium"})