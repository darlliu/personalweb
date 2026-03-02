(ns yuyu.blogs
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

(defn parse-frontmatter [content]
  (let [match (re-find #"(?s)^---\r?\n(.*?)\r?\n---\r?\n(.*)" content)]
    (if match
      (let [[_ frontmatter body] match
            lines (str/split-lines frontmatter)
            meta (into {} (for [line lines
                                :let [[k v] (str/split line #":" 2)]
                                :when (and k v)]
                            [(keyword (str/trim k)) 
                             (str/replace (str/trim v) #"^\"|\"$" "")]))]
        {:meta meta :body body})
      {:meta {} :body content})))

(defn get-blog-posts []
  (let [dir (io/file (.getFile (io/resource "blog")))
        files (if (.exists dir)
                (filter #(and (.isFile %) (str/ends-with? (.getName %) ".md")) (file-seq dir))
                [])]
    (->> files
         (map (fn [f]
                (let [content (slurp f)
                      {:keys [meta body]} (parse-frontmatter content)
                      fname (str "blog/" (.getName f))]
                  (merge meta {:fname fname :body body}))))
         (sort-by :date)
         reverse)))