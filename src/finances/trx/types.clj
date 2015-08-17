(ns finances.trx.types)


(comment
  "just use records for now ... "
  (defrecord Transaction [account date desc amount category checkNum]))

(defn compareByDate
  "Compare two transactions by date"
  [a b]
  (compare (:date a) (:date b)))

