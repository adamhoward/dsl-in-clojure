(ns dsl-in-clojure.core
  (:use [clojure.string :only [split-lines]]))

;;; Dispatch on first four characters of line
(defmulti parse-line #(subs % 0 4))

;;; Print out "mapping undefined" message by default
(defmethod parse-line :default [line] (str "No mapping defined for " (subs line 0 4)))

;;; Macro to create parse-line implementations
(defmacro defmapping [prefix & description]
  `(defmethod parse-line ~prefix [line#]
     (reduce
      (fn [m# [start# end# slot#]]
        (assoc m# (keyword slot#) (subs line# start# (inc end#))))
      {} '~description)))

;;; Mapping for Service Calls
(defmapping "SVCL"
  (4 18 customer-name)
  (19 23 customer-id)
  (24 27 call-type-code)
  (28 35 date-of-call-string))

;;; Mapping for Usages
(defmapping "USGE"
  (4 8 customer-id)
  (9 22 customer-name)
  (30 30 cycle)
  (31 36 read-date))

(def ^:dynamic *example-data*
  "SVCLFOWLER         10101MS0120050313.........................
SVCLHOHPE          10201DX0320050315........................
SVCLTWO           x10301MRP220050329..............................
USGE10301TWO          x50214..7050329...............................")

(defn parse-example-data []
  (map parse-line (split-lines *example-data*)))

;;; Run parse-example-data to see output
(comment (parse-example-data))
