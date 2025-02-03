(ns yuyu.routes.home
  (:require
   [clojure.java.io :as io]
   [clojure.string :as str]
   [clojure.tools.logging :as log]
   [hiccup2.core :as hiccup]
   [markdown.core :as md]
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

(defn get-blog-posts [request]
  (->> (io/file "resources/blog")
       (file-seq)
       (filter #(str/ends-with? (.getName %) ".md"))
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
                 :slug (str/replace (.getName file) #".md$" "")
                 :content (md/md-to-html-string (str/join body))})))))

(defn get-blog-menu [request]
  (layout/render request "blog.html"
                 {:blogs
                  (let [posts (get-blog-posts)]
                    (hiccup/html
                     [:ul
                      (for [post posts]
                        [:li [:a {:href (str "/blogs/" (:slug post))} (:title post)]])]))}))

(defn render-blog-post [slug]
  (if-let [post (->> (get-blog-posts)
                     (filter #(= (:slug %) slug))
                     first)]
    (hiccup/html
     [:div
      [:h1 (:title post)]
      [:div (:date post)]
      [:div {:dangerouslySetInnerHTML {:__html (:content post)}}]])
    (route/not-found "Post not found")))


(defn home-routes []
  [""
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats]}
   ["/" {:get home-page}]
   ["/papers" {:get papers-page}]
   ["/blogs" {:get get-blog-menu}]
   ["/blog/:slug" {post: (fn [{:keys [path-params query-params body-params]}]
                           {:status 200
                            :body (render-blog-post (path-params slug))})}]
   ["/about" {:get about-page}]])

