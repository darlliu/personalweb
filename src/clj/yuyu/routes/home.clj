(ns yuyu.routes.home
  (:require
   [clojure.tools.logging :as log]
   [yuyu.layout :as layout]
   [yuyu.db.core :as db]
   [clojure.java.io :as io]
   [yuyu.middleware :as middleware]
   [ring.util.response]
   [ring.util.http-response :as response]))

(defn home-page [request]
  (log/info  (request :request-method) "[<-]" (request :remote-addr) "[@@]" (get (request :headers) "user-agent")
   "[lan]" (get (request :headers) "accept-language"))
  (layout/render request "home.html" {:docs (-> "docs/docs.md" io/resource slurp)}))

(defn papers-page [request]
  (layout/render request "papers.html" {:papers (-> "docs/papers.md" io/resource slurp)}))

(defn about-page [request]
  (layout/render request "about.html"))

(defn home-routes []
  [ "" 
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats]}
   ["/" {:get home-page}]
   ["/papers" {:get papers-page}]
   ["/about" {:get about-page}]])

