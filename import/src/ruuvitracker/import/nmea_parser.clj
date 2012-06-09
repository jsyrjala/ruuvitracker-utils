(ns ruuvitracker.import.nmea-parser
  (:require [clojure.string :as string])
  )

;; See http://aprs.gids.nl/nmea/
;; Only GPGGA and GPRMC messages are supported.
;; Only partial data is parsed.

(defn parse-nmea-message[line]
  (defn remove-leading-zeros[value]
    (when value
      (string/replace value #"^0+" "")))

  (defn to-date-time [time date]
    (str time date)
    )
  
  (defn to-float [value]
    (when (not (string/blank? value))
      (BigDecimal. value)))

  (defn parse-nmea-type [parts]
    (parts 0))
  
  (defmulti parse-nmea-data
    (fn [parts] (parts 0) ) :default nil)
  
  (defmethod parse-nmea-data "GPGGA"
    [parts]
    {:type (keyword (parts 0))
     :time (parts 1)
     :lat (remove-leading-zeros (str (parts 2) "," (parts 3)))
     :lon (remove-leading-zeros (str (parts 4) "," (parts 5)))
     })
  
  (defmethod parse-nmea-data "GPRMC"
    [parts]
    {:type (keyword (parts 0))
     :time (to-date-time (parts 1) (parts 9))
     :warning (parts 2)
     :lat (remove-leading-zeros (str (parts 3) "," (parts 4)))
     :lon (remove-leading-zeros (str (parts 5) "," (parts 6)))
     :speed (parts 7)
     :heading (parts 8)
   })
  
  (defmethod parse-nmea-data nil
    ;; Unsupported messages return nil
    [parts] nil)

  (when line
    (let [line (string/replace line #"^\$" "")
          parts (string/split line #"," )
          type (parse-nmea-type parts)]
      (parse-nmea-data parts))))
