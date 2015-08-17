(ns finances.domain.parser
  (:require [finances.trx.types]
            [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [clj-time.core :as t]
            [clj-time.format :as f])
  (:import (java.util UUID)))

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
  (hash-map
    :account "discover"
    :date (parseDiscoverDate date)
    :desc (clean_string desc)
    :amount (str_to_amount amount -1)
    :category category
    :checkNum nil
    )
  )

(defn- map_sunset
  "maps a row from a sunset csv download"
  [[accountNum, checkNum, debt, payment, balance, date, desc, extra2]]
  (hash-map
    :account "sunset"
    :date (parseDiscoverDate date)
    :desc (clean_string desc)
    :amount (if (clojure.string/blank? payment) (str_to_amount debt -1) (str_to_amount payment 1))
    :category nil,
    :checkNum checkNum)
  )

(defn- map_usaa
  "maps a row from a USAA csv download"
  [[extra1, extra2, date, extra3, desc, category, amount]]
  (hash-map
    :account "usaa"
    :date (parseDiscoverDate date)
    :desc (clean_string desc)
    :amount (str_to_amount amount 1)
    :category category,
    :checkNum nil)
  )


(defn- map_target
  "maps a row from a Target csv download"
  [[date, postedDate, desc, amount, category]]
  (hash-map
    :account "target"
    :date (parseDiscoverDate date)
    :desc (clean_string desc)
    :amount (str_to_amount amount 1)
    :category category,
    :checkNum nil)
  )


(def parser_map {
                 "discover" [rest map_discover]
                 "sunset"   [rest map_sunset]
                 "usaa"     [identity map_usaa]
                 "target"   [identity map_target]
                 })


(defn- addId
  "Add a GUID as the id to a map as the :id field"
  [map]
  (assoc map :id (str (UUID/randomUUID)))
  )

(defn parse_bank_file
  "parses a bank file based on the passsed account type"
  [account filePath]
  (let [
        mapper (get parser_map account)
        skipFunc (get mapper 0)
        parseFunc (get mapper 1)

        ]
    (map addId (map parseFunc (skipFunc (load-bank-trx filePath)))
         )
    )
  )