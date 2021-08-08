(ns clj.env
  (:require
    [selmer.parser :as parser]
    [clojure.tools.logging :as log]
    [clj.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[clj started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[clj has shut down successfully]=-"))
   :middleware wrap-dev})
