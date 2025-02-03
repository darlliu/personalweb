(ns yuyu.routes.home
  (:require
   [clojure.java.io :as io]
   [clojure.string :as str]
   [clojure.tools.logging :as log]
   [hiccup2.core :as hiccup]
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

(defn get-blog-posts []
  (->> (-> "blog" io/resource slurp)
       (file-seq)
      ;;  (filter #(str/ends-with? (.getName %) ".md"))
       (map (fn [file]
              (let [content (slurp file)
                    [meta & body] (str/split content #"---" 3)]
                {:title (-> meta
                            (str/split #"\n")
                            (->> (filter #(str/includes? % "title:"))
                                 first
                                 (re-find #":\s*(.*)")
                                 second))
                 :date (-> meta
                           (str/split #"\n")
                           (->> (filter #(str/includes? % "date:"))
                                first
                                (re-find #":\s*(.*)")
                                second))
                 :fname (.getName file)
                 :slug (str/replace (.getName file) #".md$" "")})))
       (into [])))

(defn get-blog-menu [request]
  (layout/render request "blog.html"
                 {:blogs (get-blog-posts)}))

(defn render-blog-post [request]
  (if-let [post (->> (get-blog-posts)
                     (filter #(= (:slug %) (request :slug)))
                     first)]
    (layout/render request "home.html" {:docs (-> (post :fname) io/resource slurp)})
    (layout/error-page {:status 404
                        :title "Blog post not found!"
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

