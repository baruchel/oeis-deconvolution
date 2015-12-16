(ns oeis-deconvolution2.core
  (:require [clojure.tools.cli :refer [parse-opts]])
  (:gen-class))

;;;;
;;;; Usage:
;;;;
;;;;   The sequence is the first argument of the program as a string;
;;;;   consecutive values may be separated by spaces or commas.
;;;;
;;;;   The full path for the stripped.gz file of the database may be specified
;;;;   as a second argument. If no second argument is provided, the environment
;;;;   variable OEISDATABASE is tried; if no such environment variable exists,
;;;;   a file called "stripped.gz" in the current directory is used if any.
;;;;

; compute a/b by assuming a and b have exactly same size and no leading zero
; RETURN A LIST (and not a vector)
; RETURN A REVERSED LIST (because almost all returned sequences will only be used
; for computing a norm; only the most interesting will need to be reversed)
(defn deconvol [a b]
  (loop [n 0 l nil]
    (if (= n (count a)) l
      (recur (inc n)
        (cons (/ (reduce -'
                   (cons (nth a n)
                     (for [i (range n)]
                       (*' (nth b (inc i)) (nth l i)))))
                 (first b)) l)))))

; index of the first non nul 0 (returns nil if everything is 0 in the vector)
(defn fnz [c]
  (loop [i 0]
    (if (= i (count c))
      nil
      (if (= 0 (nth c i))
        (recur (inc i))
        i))))

; normalize vector (remove trailing zeros); return nil if everything is 0
(defn normalize [v]
  (let [iv (fnz v)]
    (if (nil? iv) nil (subvec v iv))))

; compute the norm of a sequence (as a list rather than a vector)
(defn norm [s]
  (loop [v s n 0]
    (if (empty? v) n
      (let [f (first v)]
        (recur (rest v)
          (if (ratio? f)
            (let [p (numerator f)
                  q (denominator f)]
              (+' n (*' p p q q)))
            (+' n (*' f f))))))))


(defn scan [[label s] request target] 
  (let [s2 (normalize s)
        c (count request)]
    (if (not (nil? s2))
      (if (>= (count s2) c)
        (let [sequence (subvec s2 0 c)
              d (deconvol request sequence)
              n (norm d)]
          (if (< n target)
            (do
              (println (str "Deconvolution with " label ":"))
              (println (str "  is "
                (clojure.string/replace (str (reverse d)) "N" "")))
              (println (str "  --> norm is " n)))))))))
;;
;; Options
;;
(def cli-options
  ;; An option with a required argument
  [["-d" "--database PATH" "Path to the stripped.gz file for the OEIS database"
    :default (let [path (System/getenv "OEISDATABASE")]
               (if path path "stripped.gz"))]
   ["-t" "--target VALUE" "Value for filtering results to be displayed"
    :default nil
    :parse-fn #(float (bigint %))]])

;;
;; Main loop over the whole database
;;
(defn -main [& args]
  (let [opts (parse-opts args cli-options)
        request (normalize (eval (read-string
                  (apply str "[" (clojure.string/replace
                                   (first (get opts :arguments))
                                   "," " ") "]"))))
        n0 (norm (seq request))
        target (let [t (get (get opts :options) :target)]
                 (if (nil? t) (float (Math/sqrt n0)) t))]
    (do
      (println "OEIS/deconvolution by Th. Baruchel")
      (println (str "  initial norm = " n0))
      (if (not (nil? request))
        (with-open [in (clojure.java.io/reader
                         (java.util.zip.GZIPInputStream.
                           (clojure.java.io/input-stream
                             (get (get opts :options) :database))))]
          (doseq [line (line-seq in)]
            (if (= (first line) \A)
              (scan (eval (read-string
                        (clojure.string/replace
                          (clojure.string/replace line
                            #"^([^ ]*) ,(.*),$"
                            "'(\"$1\" [$2])") "," " ")))
                    request target))))))))
