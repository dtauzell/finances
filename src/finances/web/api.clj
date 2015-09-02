(ns finances.web.api
  (:require [ring.util.response :refer [redirect file-response]]
            [ring.util.anti-forgery :refer [anti-forgery-field]]
            [finances.trx.types]
            [finances.domain.db :as db]
            [finances.domain.parser :as parser]
            [clojure.data.json :as json]
            [liberator.core :refer [defresource]]
            [clojure.java.io :as io])
  (:import (org.joda.time DateTime)))


(extend-type DateTime
  json/JSONWriter
  (-write [date out]
    (json/-write (str date) out)))

(defresource transaction-r
             :available-media-types ["application/json"]
             :allowed-methods [:get]
             :handle-ok (fn [_]
                          (json/write-str
                            (db/allTrx))))
