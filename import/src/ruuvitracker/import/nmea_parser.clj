(ns ruuvitracker.import.nmea-parser
  (:require [clojure.string :as string])
  (:import [org.joda.time.format DateTimeFormat DateTimeFormatter]
           [org.joda.time DateTime DateTimeZone]))

;; See http://aprs.gids.nl/nmea/
;; Only GPGGA and GPRMC messages are supported.
;; Only partial data is parsed.

(defonce nmea-date-time-formatter (.withZone
                                   (DateTimeFormat/forPattern "ddMMyy'T'HHmmss.SSS")
                                   (DateTimeZone/forID "UTC")))

(defn- remove-leading-zeros[value]
  (when value
    (string/replace value #"^0+" "")))

(defn- to-date-time [time date]
  (when (not (or (string/blank? time) (string/blank? date)))
    (.parseDateTime nmea-date-time-formatter (str date "T" time))
    ))

(defn- to-float [value]
  (when (not (string/blank? value))
    (BigDecimal. value)))

(defn- to-coordinate [value type]
  (when (and (not (string/blank? value))
             (not (string/blank? type)))
    (remove-leading-zeros (str value "," type))))

(defn- remove-empty-values [map-data]
  (merge {} (filter (fn [[key value]]
                      (cond
                       (= value nil) false
                       (and (string? value) (string/blank? value)) false
                       :else true))
                    map-data)))

(defmulti parse-nmea-data
  (fn [parts]
    (parts 0) )
  :default nil)

(defmethod parse-nmea-data "GPGGA"
  [parts]
  (remove-empty-values
   {:type (keyword (parts 0))
    :time (parts 1)
    :lat (to-coordinate (parts 2) (parts 3))
    :lon (to-coordinate (parts 4) (parts 5))
   }))

(defmethod parse-nmea-data "GPRMC"
  [parts]
  (remove-empty-values
   {:type (keyword (parts 0))
    :timestamp (to-date-time (parts 1) (parts 9))
    :warning (parts 2)
    :lat (to-coordinate (parts 3) (parts 4))
    :lon (to-coordinate (parts 5) (parts 6))
    :speed (parts 7)
    :heading (parts 8)
    }))

(defmethod parse-nmea-data nil
  ;; Unsupported messages return nil
  [parts] nil)

(defn parse-nmea-message[line]
  (when line
    (let [line (string/replace line #"^\$" "")
          parts (string/split line #"," ) ]
      (parse-nmea-data parts))))

(defn parse-nmea [reader handler]
  (doall (map handler (filter identity (map parse-nmea-message (line-seq reader))))))
