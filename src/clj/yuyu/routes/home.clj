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

(defn sitemap-page [request]
  (let [blogs (blog/get-blog-posts)
        urls (concat [{:loc "/"}
                      {:loc "/about"}
                      {:loc "/papers"}
                      {:loc "/blogs"}
                      {:loc "/bayes"}]
                     (map (fn [b] {:loc (str "/blog/" (:slug b))}) blogs))
        xml (str "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                 "<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n"
                 (str/join "\n" (map (fn [u] (str "  <url><loc>https://yuliu.io" (:loc u) "</loc></url>")) urls))
                 "\n</urlset>")]
    (-> (response/ok xml)
        (response/content-type "application/xml"))))

(defn bayes-page [request]
  (layout/render request "bayes.html" 
                 {:title "WASM Bayesian Network Builder"
                  :description "A serverless, client-side Bayesian Network inference engine compiled in C++ via WebAssembly and visualized via D3.js."}))

(defn get-blog-menu [request]
  (layout/render request "blog.html"
                 {:blogs (blog/get-blog-posts)}))

(defn save-comment [request]
  (let [slug (-> request :path-params :slug)
        {:keys [author message]} (:params request)]
    (when (and (not (str/blank? author)) (not (str/blank? message)))
      (db/create-comment! {:post_slug slug :author author :message message}))
    (response/found (str "/blog/" slug))))

(defn render-blog-post [request]
  (let [slug (-> request :path-params :slug)]
    (if-let [post (->> (blog/get-blog-posts)
                       (filter #(= (:slug %) slug))
                       first)]
      (let [comments (db/get-comments-by-slug {:post_slug slug})]
        (layout/render request "blog_post.html" 
                       {:post post
                        :title (:title post)
                        :description (str "Read the latest post: " (:title post))
                        :comments comments}))
      (layout/error-page {:status 404
                          :title (str "Blog post not found: " slug)
                          :message "Probably devoured by the sharks :("}))))

(defn home-routes []
  [""
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats]}
   ["/" {:get home-page}]
   ["/papers" {:get papers-page}]
   ["/blogs" {:get get-blog-menu}]
   ["/blog/:slug" {:get render-blog-post
                   :post save-comment}]
   ["/sitemap.xml" {:get sitemap-page}]
   ["/about" {:get about-page}]
   ["/bayes" {:get bayes-page}]])

