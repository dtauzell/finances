(ns finances.trx.types)

(defrecord Transaction [account date desc amount category])

(defn compareByDate
  "Compare two transactions by date"
  [a b]
  (compare (:date a) (:date b)))

