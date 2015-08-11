(ns finances.domain.db
  (:require [finances.trx.types :as types]
            [clojure.data.csv :as csv]
            [clojure.java.io :as io])
  (:import (finances.trx.types Transaction)))

(def ^:private dbFile "/Users/LisaTauzell/Dropbox/documents/Transactions/2015/archived/discover_201504_201506.csv")

(def ^:private db (atom []))

(defn allTrx
  "returns all transactions for all time"
  []
  @db)

(comment
  (def trxData (map map_discover (rest (load-bank-trx dbFile)))))

(defn bulkImport
  "bulk import a sequence of Transaction records, returns the new db"
  [records]
  (reset! db (sort types/compareByDate (into @db records)))
  allTrx
  )

(defn deleteAll
  "delete all records"
  []
  (reset! db []))



