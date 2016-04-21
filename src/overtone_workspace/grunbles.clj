(ns overtone-workspace.grumbles
  ;;(:require  [overtone.at-at :as at-at])
  (:use [overtone.live]
        [overtone-workspace.synth laserbeam latchbell]))

;; Inspired by an example in an early chapter of the SuperCollider book

;; (def kick (sample "resources/kick.wav"))
;; (kick)

(stop)

(odoc line)
(odoc asr)
(odoc buf-rd)
(odoc phasor)
(odoc bpz2)
(odoc latch)
(odoc buf-rate-scale)
(odoc buf-dur)

(definst grumble [freq 440 freq-mul 1 speed 10 attack 10 release 50]
  (let [snd (mix (map #(* (lf-cub (* % freq-mul freq))
                          (max 0 (+ (lf-noise1:kr speed)
                                    (env-gen (perc attack release) :action FREE))))
                      [1 (/ 2 3) (/ 3 2) 2]))]
    (pan2 (* 0.7 snd) (sin-osc:kr 16))))

;;(stop)
;; (grumble)
;; (grumble :freq-mul 0.5)
;; (grumble :freq-mul 0.75)
;; (grumble :freq-mul 1)
;; (grumble :freq-mul 1.5)
;; (grumble :freq-mul 2)
;; (ctl grumble :speed 3000)

(volume (/ 25 127))

;; at overtone.music.time
;; (at (+ (now) 2000) (grumble))
;; (at (+ (now) 2000) #(println "hoge"))
;; (periodic 200 #(println "hoge"))

;; at overtone.at-at
;; (def mypool (at-at/mk-pool))
;; (at-at/at (+ 1000 (now))  #(println "hello from the past") mypool :desc "Message from the past")

(def metro (metronome 128))

(defn player [beat rates]
  (let [rates (if (empty? rates)
                [5 4 3 3 2 2 1.5 1.5 0.75 0.75 0.5]
                rates)
        freq (midi->hz (+ (note :C3) -1))
        dur (/ 60.0 (metro :bpm))
        attack (* dur 4) ; 1bar
        rel (* dur (* 4 15))]  ; 15bars
    (at (metro beat)
        (do
          (if (zero? (mod beat 8)) (grumble :freq freq :freq-mul 0.5 :attack attack :release rel))
          (if (zero? (mod beat 16)) (grumble :freq freq :freq-mul 1 :attack attack :release rel))
          (if (zero? (mod beat 48)) (grumble :freq freq :freq-mul (choose rates) :attack attack :release rel))
          ))
    (apply-by (metro (inc beat)) #'player (inc beat) rates [])
    ))

;; (player (metro) [])

(defn laserplayer [beat]
  (let [dur (/ 60.0 (metro :bpm))]      ; 15bars
    (at (metro beat)
        (laserbeam :pan (- (rand 2.0) 1.0) :freq (+ (rand-int 1000) 100) :dur 0.25))
    (at (metro (+ beat 0.25))
        (laserbeam :pan (- (rand 2.0) 1.0) :freq (+ (rand-int 1000) 100) :dur 0.25))
    (at (metro (+ beat 0.5))
        (laserbeam :pan (- (rand 2.0) 1.0) :freq (+ (rand-int 1000) 100) :dur 0.25))
    (at (metro (+ beat 0.75))
        (laserbeam :pan (- (rand 2.0) 1.0) :freq (+ (rand-int 1000) 100) :dur 0.25))
    (apply-by (metro (inc beat)) #'laserplayer (inc beat) [])
    ))

;; (laserplayer (metro))

;; (stop)

(def latchbell-arg (atom {:rate 100 :amp 0.4 :max 5}))

(defn latchbellplayer [beat]
  (let [dur (/ 60.0 (metro :bpm))]
    (at (metro beat)
        (latchbell :rate (:rate @latchbell-arg) :amp (:amp @latchbell-arg) :time-scale-max (:max @latchbell-arg)))
    (at (metro (+ beat 0.25))
        (latchbell :rate (:rate @latchbell-arg) :amp (:amp @latchbell-arg) :time-scale-max (:max @latchbell-arg)))
    (at (metro (+ beat 0.5))
        (latchbell :rate (:rate @latchbell-arg) :amp (:amp @latchbell-arg) :time-scale-max (:max @latchbell-arg)))
    (at (metro (+ beat 0.75))
        (latchbell :rate (:rate @latchbell-arg) :amp (:amp @latchbell-arg) :time-scale-max (:max @latchbell-arg)))
    (apply-by (metro (inc beat)) #'latchbellplayer (inc beat) [])
    ))
;; (init-latchbell-mod (:rate @latchbell-arg))
;; (latchbellplayer (metro))
;; (stop)

(comment
  (inst-fx! latchbell fx-echo)
  (inst-fx! latchbell fx-distortion2)
  (clear-fx latchbell)

  (inst-fx! grumble fx-bitcrusher)
  (clear-fx grumble)

  (inst-fx! laserbeam fx-freeverb)
  (clear-fx laserbeam))


;; (def dirty-kick (freesound 777))
;; (dirty-kick)
;; (def kick (sample (freesound-path 2086)))
;; (kick)
;; (def close-hihat (sample-player (sample (freesound-path 802))))


(def _ 0)
(def beats {laserbeam [1 _ _ _ _ _ _ 1 _ _ 1 _ _ _ _ 1]
            latchbell [1 _ 1 _ 1 _ 1 _ 1 _ 1 _ 1 _ _ 1]})

(def *beats (atom beats))

; (defn live-sequencer [nome beat live-patterns scale idx]
;   (doseq [[sound pattern] @live-patterns]
;      (when (= 1 (nth pattern (mod idx (count pattern))))
;        (at (nome beat) (sound))))
;   (let [next-beat (+ scale beat)]
;     (apply-by (nome next-beat) live-sequencer [nome next-beat live-patterns scale (inc idx)])))

(defn flatten1
  "Takes a map and returns a seq of all the key val pairs:
      (flatten1 {:a 1 :b 2 :c 3}) ;=> (:b 2 :c 3 :a 1)"
  [m]
  (reduce (fn [r [arg val]] (cons arg (cons val r))) [] m))


(defn live-sequencer [nome beat live-patterns scale idx]
  (doseq [[sound pattern] @live-patterns
          :let [v (nth pattern (mod idx (count pattern)))
                v (cond
                    (= 1 v)
                    []

                    (map? v)
                    (flatten1 v)

                    :else
                    nil)]
          :when v]
    (at (nome beat) (apply sound v)))
  (let [next-beat (+ scale beat)]
    (apply-by (nome next-beat) live-sequencer [nome next-beat live-patterns scale (inc idx)])))

(def a {:rate 100 :amp 0.5 :time-scale-max 10})
(def a {:rate 2 :amp 0.05 :time-scale-max 5})
(def b {:pan -1 :freq 1000})
(def c {:freq 25000 :amp 0.2})
(def d {:amp 3.0 :dur 0.25})

(metro :bpm 110)
(volume 0.5)
(comment
  (live-sequencer metro (metro) *beats 1/4 0)

  (swap! *beats assoc laserbeam [d _ _ _ b _ _ d _ _ d _ b _ _ d])
  (swap! *beats assoc latchbell [a _ a _ a _ a _ a _ a _ a _ a _])
  (swap! *beats assoc laserbeam [d c c c b c c d c c d c b c c d])

  (reset! *beats beats)
  (stop))
