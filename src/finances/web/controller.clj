(ns finances.web.controller
  (:require [ring.util.response :refer [redirect file-response]]
            [ring.util.anti-forgery :refer [anti-forgery-field]]
            [finances.trx.types]
            [finances.domain.db :as db]
            [finances.domain.parser :as parser]
            [clojure.java.io :as io]
            [clojure.data.csv :as csv])
  )
(use 'selmer.parser)

(selmer.parser/add-tag! :csrf-field (fn [_ _] (anti-forgery-field)))

(defn index
  "Handles the home page"
  [req]
  (render-file "template/index.html" {
                                      :trxData (db/allTrx)
                                      })
  )

(defn upload_form
  "Upload a cvs file"
  [req]
  (render-file "template/upload_form.html" {})
  )


(defn upload-file
  "parses a data file and loads it into the database"
  [params]
  (println (str "Params: " params))
  (db/bulkImport (parser/parse_bank_file (:account params) (:tempfile (:file params)))))

