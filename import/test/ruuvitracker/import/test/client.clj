(ns ruuvitracker.import.test.client
  (:use ruuvitracker.import.client)
  (:require [clj-http.client :as client])
  (:import [org.joda.time DateTime DateTimeZone])
  (:use midje.sweet)
  )

(def timestamp (DateTime. 2012 6 8 9 37 43 0 (DateTimeZone/forID "UTC")))

(def gpgga {:type :GPGGA
            :lat "6103.8614,N"
            :lon "2805.7936,E"
            :speed "10.05"
            :heading "42.40"
            :timestamp timestamp
            })
(def url "http://example.com/ruuvitracker/api/v1/events")
(def tracker-id "track")
(def secret "password")

(def expected-params
  {:body "{\"mac\":\"ca872b588dbfb89327faacec9b220b0b525b85ea\",\"version\":\"1\",\"tracker_code\":\"track\",\"time\":\"2012-06-08T09:37:43.000+0000\",\"latitude\":\"6103.8614,N\",\"longitude\":\"2805.7936,E\"}",
   :headers {"User-Agent" "RuuviTracker NMEA Importer/0.1"},
   :content-type :json,
   :accept :json,
   :socket-timeout 300000,
   :conn-timeout 60000})

(fact "Sending message succeeds"
      (send-nmea-data url gpgga tracker-id secret) => {:status 200}
      (provided
       (client/post url expected-params) => {:status 200}))
