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

(defn get-blog-files []
  (let [url (io/resource "blog")]
    (when url
      (if (= "jar" (.getProtocol url))
        (let [conn (.openConnection url)
              jar-file (.getJarFile conn)
              entries (enumeration-seq (.entries jar-file))]
          (->> entries
               (map #(.getName %))
               (filter #(and (str/starts-with? % "blog/")
                             (str/ends-with? % ".md")))
               (map io/resource)))
        (let [dir (io/file url)]
          (when (.exists dir)
            (filter #(and (.isFile %) (str/ends-with? (.getName %) ".md"))
                    (file-seq dir))))))))

(defn get-blog-posts []
  (if-let [files (get-blog-files)]
    (->> files
         (map (fn [f]
                (let [content (slurp f)
                      {:keys [meta body]} (parse-frontmatter content)]
                  (merge meta {:body body}))))
         (sort-by :date)
         reverse)
    []))