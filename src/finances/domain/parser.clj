(ns finances.domain.parser
  (:require [finances.trx.types]
            [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [clj-time.core :as t]
            [clj-time.format :as f])
  (:import (finances.trx.types Transaction)))

(def disoverDateFormat (f/formatter "MM/dd/yyyy"))

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

(defn- str_to_amount
  "converts a string to an integer, or 0 if it is blank or null"
  [s multiplier]
  (* multiplier
     (if (clojure.string/blank? s)
       (bigdec 0)
       (bigdec s)))
  )

(defn- parseDiscoverDate
  "parses a date, or returns null if the date is empty or null"
  [str]
  (if (clojure.string/blank? str)
    nil
    (f/parse disoverDateFormat (clean_string str)))
  )

(defn- map_discover
  "maps a row from discover card csv download"
  [[date, postDate, desc, amount, category]]
  (Transaction.
    "discover"
    (parseDiscoverDate date)
    (clean_string desc)
    (str_to_amount amount -1)
    category
    nil
    )
  )

(defn- map_sunset
  "maps a row from a sunset csv download"
  [[accountNum, checkNum, debt, payment, balance, date, desc, extra2]]
  (Transaction.
    "sunset"
    (parseDiscoverDate date)
    (clean_string desc)
    (if (clojure.string/blank? payment) (str_to_amount debt -1) (str_to_amount payment 1))
    nil,
    checkNum)
  )

(defn- map_usaa
  "maps a row from a USAA csv download"
  [[extra1, extra2, date, extra3, desc, category, amount]]
  (Transaction.
    "usaa"
    (parseDiscoverDate date)
    (clean_string desc)
    (str_to_amount amount 1)
    category,
    nil)
  )


(defn- map_target
  "maps a row from a Target csv download"
  [[date, postedDate, desc, amount, category]]
  (Transaction.
    "target"
    (parseDiscoverDate date)
    (clean_string desc)
    (str_to_amount amount 1)
    category,
    nil)
  )


(def parser_map {
                 "discover" [rest map_discover]
                 "sunset"   [rest map_sunset]
                 "usaa"     [identity map_usaa]
                 "target"   [identity map_target]
                 })


(defn parse_bank_file
  "parses a bank file based on the passsed account type"
  [account filePath]
  (let [mapper (get parser_map account)]
    (map (get mapper 1) ((get mapper 0) (load-bank-trx filePath)))
    )
  )