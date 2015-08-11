(ns finances.domain.parser
  (:require [finances.trx.types]
            [clojure.data.csv :as csv]
            [clojure.java.io :as io])
  (:import (finances.trx.types Transaction)))

(defn- load-bank-trx
  "Loads a csv of bank transactions. Only supports Discover for now"
  [filePath]
  (let [trx
        (with-open [in-file (io/reader filePath)]
          (doall
            (csv/read-csv in-file))
          )
        ]
    trx))

(defn- clean_string
  "cleans a string. if the data is nil, then return an empty string"
  [str]
  (if str (clojure.string/trim str) "")
  )

(defn- map_discover
  "maps a row from discover card csv download"
  [row]
  (Transaction.
    "discover"
    (clean_string (get row 0))
    (clean_string (get row 2))
    (clean_string (get row 3))
    nil
    )
  )

(defn parse_discover_file
  "reads a discover csv file and returns a sequence of Transaction records"
  [filePath]
  (map map_discover (rest (load-bank-trx filePath))))
