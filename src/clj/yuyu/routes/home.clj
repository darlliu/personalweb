(ns yuyu.routes.home
  (:require
   [clojure.java.io :as io]
   [clojure.string :as str]
   [clojure.tools.logging :as log]
   [hiccup2.core :as hiccup]
   [yuyu.blogs :as blog]
   [yuyu.db.core :as db]
   [yuyu.layout :as layout]
   [yuyu.middleware :as middleware]
   [ring.util.response]
   [ring.util.http-response :as response]))

(defn home-page [request]
  (log/info  (request :request-method) "[<-]" (get (request :headers) "X-Real-IP") "[@@]" (get (request :headers) "user-agent")
             "[lan]" (get (request :headers) "accept-language"))
  (layout/render request "home.html" {:docs (-> "docs/docs.md" io/resource slurp)}))

(defn papers-page [request]
  (layout/render request "papers.html" {:papers (-> "docs/papers.md" io/resource slurp)}))

(defn about-page [request]
  (layout/render request "about.html"))

(defn get-blog-menu [request]
  (layout/render request "blog.html"
                 {:blogs (blog/get-blog-posts)}))

(defn render-blog-post [request]
  (if-let [post (->> (blog/get-blog-posts)
                     (filter #(= (:slug %) (-> request :path-params :slug)))
                     first)]
    (layout/render request "home.html" {:docs (-> (post :fname) io/resource slurp)})
    (layout/error-page {:status 404
                        :title (str "Blog post not found: " request)
                        :message "Probably devoured by the sharks :("})))

(defn home-routes []
  [""
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats]}
   ["/" {:get home-page}]
   ["/papers" {:get papers-page}]
   ["/blogs" {:get get-blog-menu}]
   ["/blog/:slug" {:get render-blog-post}]
   ["/about" {:get about-page}]])

