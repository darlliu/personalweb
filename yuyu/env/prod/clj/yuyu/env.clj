(ns yuyu.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[yuyu started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[yuyu has shut down successfully]=-"))
   :middleware identity})
