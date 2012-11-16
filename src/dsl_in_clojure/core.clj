;;;; ## The challenge
;;;; Given a log of events with fixed-length sections:
;;;;
;;;;     SVCLFOWLER         10101MS0120050313.............
;;;;     SVCLHOHPE          10201DX0320050315.............
;;;;     SVCLTWO           x10301MRP220050329.............
;;;;     USGE10301TWO          x50214..7050329............
;;;;
;;;; And a spec:
;;;;
;;;; <pre><code>A <strong>Service Call</strong> line is prefixed with SVCL
;;;; and has the following description:
;;;;  * 4 - 18 Customer Name
;;;;  * 19 - 23 Customer ID
;;;;  * 24 - 27 Call Type Code
;;;;  * 28 - 35 Date of Call
;;;;
;;;; A <strong>Usage</strong> line is prefixed with USGE...
;;;; </code></pre>
;;;;
;;;; Parse the event log into a format suitable for analysis.
;;;;
;;;; <sub>Footnote: This is my Clojure implementation of Rainer Joswig's
;;;; Common Lisp implementation of the DSL described in Martin Fowler's
;;;; *Language Workbenches* article.</sub>
(ns dsl-in-clojure.core
  (:use [clojure.string :only [split-lines]]))

;;; ## The DSL
;;; We're going to create a DSL that mimicks the description provided
;;; in our spec. Something like:
;;;
;;;     (defmapping "SVCL"
;;;       (4 18 customer-name)
;;;       (19 23 customer-id)
;;;       (24 27 call-type-code)
;;;       (28 35 date-of-call-string))

;;; Let's use a multimethod to parse the different line types, using
;;; the first four characters in a line to determine which mapping to
;;; use

(defmulti parse-line
  "Dispatch on first four characters of line"
  #(subs % 0 4))

;;; Print out "mapping undefined" message by default
(defmethod parse-line :default
  [line]
  (str "No mapping defined for " (subs line 0 4)))

(defmacro defmapping
  "Macro to create parse-line implementations"
  [prefix & description]
  `(defmethod parse-line ~prefix [line#]
     (reduce
      (fn [m# [start# end# slot#]]
        (assoc m# (keyword slot#) (subs line# start# (inc end#))))
      {} '~description)))

;;; ## Our example at work

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

;;; ## Some test data

(def ^:dynamic *example-data*
  "SVCLFOWLER         10101MS0120050313.........................
SVCLHOHPE          10201DX0320050315........................
SVCLTWO           x10301MRP220050329..............................
USGE10301TWO          x50214..7050329...............................")

(defn parse-example-data
  "Parse each line of test data into a Service Call or a Usage"
  []
  (map parse-line (split-lines *example-data*)))

;;; Run (parse-example-data) to see output
(comment (parse-example-data))
