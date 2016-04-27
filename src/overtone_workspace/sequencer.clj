(ns overtone-workspace.sequencer
  (:use [overtone.live]))

(defn- flatten1
  "Takes a map and returns a seq of all the key val pairs:
        (flatten1 {:a 1 :b 2 :c 3}) ;=> (:b 2 :c 3 :a 1)"
  [m]
  (reduce (fn [r [arg val]] (cons arg (cons val r))) [] m))

(defn- normalise-beat-info
  [beat]
  (cond
    (= 0 beat)         nil
    (= 1 beat)         {}
    (map? beat)        beat
    (sequential? beat) beat
    :else              {}))

(defn- schedule-pattern
  [curr-t pat-dur sound pattern]
  {:pre [(sequential? pattern)]}
  (let [beat-sep-t (/ pat-dur (count pattern))]
    (doseq [[beat-info idx] (partition 2 (interleave pattern (range)))]
      (let [beat-t    (+ curr-t (* idx beat-sep-t))
            beat-info (normalise-beat-info beat-info)]
        (if (sequential? beat-info)
          (schedule-pattern beat-t beat-sep-t sound beat-info)
          (at beat-t (when beat-info (apply sound (flatten1 beat-info)))))))))

(def live-sequencer-states {})
(def *live-sequencer-states (atom live-sequencer-states))

;; (defn live-sequencer [nome beat live-patterns scale idx]
;;   (doseq [[sound pattern] @live-patterns]
;;      (when (= 1 (nth pattern (mod idx (count pattern))))
;;        (at (nome beat) (sound))))
;;   (let [next-beat (+ scale beat)]
;;     (apply-by (nome next-beat) live-sequencer [nome next-beat live-patterns scale (inc idx)])))

;; (defn live-sequencer [nome beat live-patterns scale idx]
;;   (doseq [[sound pattern] @live-patterns
;;           :let [v (nth pattern (mod idx (count pattern)))
;;                 v (cond
;;                     (= 1 v)
;;                     []
;;
;;                     (map? v)
;;                     (flatten1 v)
;;
;;                     :else
;;                     nil)]
;;           :when v]
;;     (at (nome beat) (apply sound v)))
;;   (let [next-beat (+ scale beat)]
;;     (apply-by (nome next-beat) live-sequencer [nome next-beat live-patterns scale (inc idx)])))

(defn- live-sequencer
  [curr-t pat-dur live-patterns uid]
  (let [running (@*live-sequencer-states uid)]
    (when running
      (doseq [[sound pattern] @live-patterns]
        (schedule-pattern curr-t pat-dur sound pattern))
      (let [new-t (+ curr-t pat-dur)]
        (apply-by new-t #'live-sequencer [new-t pat-dur live-patterns uid]))
      )))

(defn start-live-sequencer
  ([curr-t pat-dur live-patterns] (let [uid (trig-id)] (start-live-sequencer curr-t pat-dur live-patterns uid)))
  ([curr-t pat-dur live-patterns uid]
   (do
     (swap! *live-sequencer-states assoc uid true)
     (live-sequencer curr-t pat-dur live-patterns uid)
     uid
     )))

(defn stop-live-sequencer
  [uid]
  (do
    (swap! *live-sequencer-states dissoc uid)))

(defn next-beat [cur-beat beat beats]
  (let [mod-beat (mod cur-beat beats)
        d-beat   (- beat mod-beat)]
    (cond
      (<= d-beat 0) (+ cur-beat beats d-beat)
      (> d-beat 0) (+ cur-beat d-beat)
      )))

(comment
  (mod 1 4)
  (- 0 1)
  (+ 1 )
  (next-beat 0 0 4) ;; => 4
  (next-beat 1 0 4) ;; => 4
  (next-beat 2 0 4) ;; => 4
  (next-beat 3 0 4) ;; => 4
  (next-beat 4 0 4) ;; => 8

  (next-beat 0 2 4) ;; => 2
  (next-beat 1 2 4) ;; => 2
  (next-beat 2 2 4) ;; => 6
  (next-beat 3 2 4) ;; => 6
  (next-beat 4 2 4) ;; => 6
  (next-beat 5 2 4) ;; => 6
  (next-beat 6 2 4) ;; => 10
  )
