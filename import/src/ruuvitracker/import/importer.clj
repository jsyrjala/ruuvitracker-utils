(ns ruuvitracker.import.importer
  (:use ruuvitracker.import.nmea-parser)
  (:use ruuvitracker.import.client)
  (:use [clojure.tools.logging :only (debug info warn error)]))

(defn -main [nmea-file ruuvitracker-url tracker-id secret]
  (info "Reading NMEA data from" nmea-file)
  (info "Send data to RuuviTracker server" ruuvitracker-url)
  (info "Use tracker-id" tracker-id)
  (with-open [reader (clojure.java.io/reader nmea-file)]
    (parse-nmea reader (fn [msg]
                         (when (= (:type msg) :GPRMC)
                           (prn msg)
                           (send-nmea-data ruuvitracker-url msg tracker-id secret)
                           )))
  ))

