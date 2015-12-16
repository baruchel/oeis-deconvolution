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

(ns baruchel.oeis.deconvolution
  (:gen-class))

; compute convolution of vectors a and b
(defn convolution [a b]
  (vec
    (for [n (range (min (count a) (count b)))]
      (reduce +'
        (for [i (range (inc n))]
          (*' (nth a (- n i)) (nth b i)))))))

; compute a/b by assuming a and b have exactly same size and no leading zero
(defn deconvol [a b]
  (loop [n 0 l nil]
    (if (= n (count a)) (vec (reverse l))
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




; perform a quick check witthout actually computing the Padé approximant
; (only see if the whole sequence can be cancelled after a finite number
; of operations)
(defn pade [v]
  (loop [n (inc (quot (count v) 2))
         t v]
    (if (= n 0) false
      (let [u (normalize (subvec t 1))]
        (if (nil? u) true
          (recur (dec n)
            (deconvol (cons 1 (repeat (- (count u) 1) 0)) u)))))))

(defn check [v explanation]
  (if (pade v)
    (do
      (println (apply str explanation))
      (println (str "  is " 
        (clojure.string/replace (str (subvec v 0 12)) "N" "")))
      (println "  which seems to follow a simple recurrence relation"))))

(defn scan [[label s] request] 
  (let [s2 (normalize s)
        c (count request)]
    (if (not (nil? s2))
      (if (>= (count s2) c)
        (let [sequence (subvec s2 0 c)
              c1 (convolution sequence request)
              c2 (deconvol request sequence)]
          (do
            (check c1
              (list "Convolution of your sequence with " label))
            (check c2
              (list "Dividing your sequence by " label))
            (check (convolution c1 c1)
              (list "Convolution of your sequence with " label
                    " then squaring"))
            (check (convolution c2 c2)
              (list "Dividing your sequence by " label
                    " then squaring"))))))))

;;
;; Main loop over the whole database
;;
(defn -main [& args]
  (let [request (normalize (eval (read-string
                  (apply str "[" (clojure.string/replace
                                   (first args)
                                   "," " ") "]"))))]
    (do
      (println "OEIS/deconvolution by Th. Baruchel")
      (if (not (nil? request))
        (with-open [in (clojure.java.io/reader
                         (java.util.zip.GZIPInputStream.
                           (clojure.java.io/input-stream
                             (if (> (count args) 1)
                               (second args)
                               (let [path (System/getenv "OEISDATABASE")]
                                 (if path path "stripped.gz"))))))]
          (doseq [line (line-seq in)]
            (if (= (first line) \A)
              (scan (eval (read-string
                        (clojure.string/replace
                          (clojure.string/replace line
                            #"^([^ ]*) ,(.*),$"
                            "'(\"$1\" [$2])") "," " ")))
                    request))))))))
