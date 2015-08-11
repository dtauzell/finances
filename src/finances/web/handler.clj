(ns finances.web.handler
  (:require [finances.web.controller :refer :all]
            [finances.trx.types :refer :all]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.util.response :refer [redirect file-response]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))

(defroutes app-routes
           (GET "/" [] index)
           (GET "/upload" [] upload_form)
           (POST "/upload" [file]
             (upload-file file)
             (redirect "/"))
           (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))
