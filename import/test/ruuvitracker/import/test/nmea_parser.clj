(ns ruuvitracker.import.test.nmea-parser
  (:use [ruuvitracker.import.nmea-parser])
  (:use midje.sweet)
  (:import [org.joda.time DateTime DateTimeZone]))

(def gprmc "$GPRMC,093743.000,A,6103.8614,N,02805.7936,E,10.05,42.40,080612,,,A*7C")
(def gprmc-empty-values "$GPRMC,,A,,,,,10.05,42.40,,,,A*7C")
(def gpgga "$GPGGA,093741.000,6103.8614,N,02805.7936,E,1,10,1.0,78.1,M,18.4,M,,0000*6C")
(def gpgga-empty-values "$GPGGA,093741.000,,,,,1,10,1.0,78.1,M,18.4,M,,0000*6C")

(def unsupported "$GPGSV,3,1,12,08,79,114,40,26,56,281,45,28,39,176,36,07,37,084,39*73")
(def corrupted "corrupted")

(fact (parse-nmea-message nil) =>
      nil)

(fact (parse-nmea-message corrupted) =>
      nil)

(fact (parse-nmea-message unsupported) =>
      nil)

;; GPGGA tests
(fact (:type (parse-nmea-message gpgga)) =>
      :GPGGA)

(fact (:time (parse-nmea-message gpgga)) =>
      "093741.000")

(fact (:lat (parse-nmea-message gpgga)) =>
      "6103.8614,N")

(fact (:lon (parse-nmea-message gpgga)) =>
      "2805.7936,E")

;; GPRMC tests
(fact (:type (parse-nmea-message gprmc)) =>
      :GPRMC)

(fact (:lat (parse-nmea-message gprmc)) =>
      "6103.8614,N")

(fact (:lon (parse-nmea-message gprmc)) =>
      "2805.7936,E")

(fact (contains? (parse-nmea-message gprmc-empty-values) :lon) =>
      false)

(fact (:speed (parse-nmea-message gprmc)) =>
      "10.05")

(fact (:heading (parse-nmea-message gprmc)) =>
      "42.40")

(fact (:timestamp (parse-nmea-message gprmc)) =>
      (DateTime. 2012 6 8 9 37 43 0 (DateTimeZone/forID "UTC")))

(fact (contains? (parse-nmea-message gprmc-empty-values) :timestamp) =>
      false)

(let [data (with-open [rdr (clojure.java.io/reader "test/ruuvitracker/import/test/nmea-example.txt")]
             (parse-nmea rdr identity))]
  (fact (count data)
        => 12)
  (fact (count (filter (fn [item] (= :GPGGA (:type item))) data))
        => 6)
  (fact (count (filter (fn [item] (= :GPRMC (:type item))) data))
        => 6)
  (fact (count (filter (fn [item] (= :GPGSV (:type item))) data))
        => 0)
  )

