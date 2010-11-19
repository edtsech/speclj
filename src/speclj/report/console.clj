(ns speclj.report.console
  (:use
    [speclj.reporting :only (failure-source tally-time default-reporter)]
    [speclj.exec :only (pass? fail?)])
  (:import
    [speclj.reporting Reporter]
    [speclj SpecFailure]))

(defn print-failure [id result]
  (let [characteristic (.characteristic result)
        description @(.description characteristic)
        failure (.failure result)]
    (println)
    (println (str id ")"))
    (println (str "'" (.name description) " " (.name characteristic) "' FAILED"))
    (println (.getMessage failure))
    (if (= SpecFailure (class failure))
      (println (failure-source failure))
      (.printStackTrace failure System/out))))

(defn print-failures [results]
  (println)
  (let [failures (vec (filter fail? results))]
    (dotimes [i (count failures)]
      (print-failure (inc i) (nth failures i)))))

(def seconds-format (java.text.DecimalFormat. "0.00000"))

(defn- print-duration [results]
  (println)
  (println "Finished in" (.format seconds-format (tally-time results)) "seconds"))

(defn- print-tally [results]
  (println)
  (let [fails (reduce #(if (fail? %2) (inc %) %) 0 results)]
    (println (count results) "examples," fails "failures")))

(deftype ConsoleReporter []
  Reporter
  (report-description [this description])
  (report-pass [this characteristic]
    (print "."))
  (report-fail [this characteristic]
    (print "F"))
  (report-runs [this results]
    (print-failures results)
    (print-duration results)
    (print-tally results)))

(defn new-console-reporter []
  (ConsoleReporter.))

(swap! default-reporter (fn [_] (new-console-reporter)))