(ns ruuvitracker.import.client
  (:require [clj-http.client :as client])
  (:import [java.security MessageDigest])
  (:import [javax.crypto Mac])
  (:import [javax.crypto.spec SecretKeySpec])
  (:import [org.apache.commons.codec.binary Hex])
  (:require [clj-json.core :as json])
  (:use ruuvitracker.import.security)
  (:import [org.joda.time.format DateTimeFormat DateTimeFormatter]
           [org.joda.time DateTime DateTimeZone])
  (:use [clojure.tools.logging :only (debug info warn error)]))

(defonce date-time-formatter (.withZone
                              (DateTimeFormat/forPattern "YYYY-MM-dd'T'HH:mm:ss.SSSZ")
                              (DateTimeZone/forID "UTC")))

(defn- generate-tracker-message [nmea-data tracker-id secret]
  (let [message {:version "1"
                 :tracker_code tracker-id
                 :time (.print date-time-formatter (:timestamp nmea-data))
                 :latitude (:lat nmea-data)
                 :longitude (:lon nmea-data)
                 }
        mac (compute-hmac message secret :mac)]
    (merge message {:mac mac} )))

(defn- send-tracker-message [url tracker-msg]
  (let [params {:body (json/generate-string tracker-msg)
                    :headers {"User-Agent" "RuuviTracker NMEA Importer/0.1"}
                    :content-type :json
                    :accept :json
                    :socket-timeout 1000
                :conn-timeout 1000}]
    (info "POST" url params)    
    (client/post url params)))

(defn send-nmea-data [url nmea-data tracker-id secret]
  (info "Sending" (:type nmea-data) "message to" url)
  (let [tracker-msg (generate-tracker-message nmea-data tracker-id secret)]
    (send-tracker-message url tracker-msg)
  ))
