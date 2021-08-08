(ns yuyu.env
  (:require
    [selmer.parser :as parser]
    [clojure.tools.logging :as log]
    [yuyu.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[yuyu started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[yuyu has shut down successfully]=-"))
   :middleware wrap-dev})
