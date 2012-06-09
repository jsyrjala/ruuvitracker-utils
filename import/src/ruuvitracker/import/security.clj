(ns ruuvitracker.import.security
  (:import [java.security MessageDigest])
  (:import [javax.crypto Mac])
  (:import [javax.crypto.spec SecretKeySpec])
  (:import [org.apache.commons.codec.binary Hex])
  (:use [clojure.tools.logging :only (debug info warn error)]))

(defn generate-mac-message [params mac-field]
  (let [;; remove mac key
        non-mac-params (dissoc params mac-field)
        ;; sort keys alphabetically
        sorted-keys (sort (keys non-mac-params))
        ;; make included-keys a vector and convert to non-lazy list
        param-keys (vec sorted-keys)]
    ;; concatenate keys, values and separators
    (apply str (for [k param-keys]
                 (str (name k) ":" (params k) "|")))))

(defn compute-hmac [params secret mac-field]
  (info params)
  (let [mac-message (generate-mac-message params mac-field)
        request-mac (params mac-field)
        algorithm "HmacSHA1"
        mac (Mac/getInstance algorithm)
        secret-key (SecretKeySpec. (.getBytes secret "ASCII") algorithm)]
    
    (.init mac secret-key)
    (let [computed-hmac (.doFinal mac (.getBytes mac-message "ASCII"))
          computed-hmac-hex (Hex/encodeHexString computed-hmac)]
        (debug (str "orig-hmac " (str request-mac) " computed hmac " (str computed-hmac-hex)))
      computed-hmac-hex
      )))

