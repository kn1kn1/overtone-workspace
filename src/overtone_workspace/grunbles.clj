(ns overtone-workspace.grumbles
  ;;(:require  [overtone.at-at :as at-at])
  (:use [overtone.live]
        [overtone-workspace sequencer]
        [overtone-workspace.synth laserbeam latchbell]))

;; Inspired by an example in an early chapter of the SuperCollider book

;; (def kick (sample "resources/kick.wav"))
;; (kick)

(comment
  (odoc line)
  (odoc asr)
  (odoc buf-rd)
  (odoc phasor)
  (odoc bpz2)
  (odoc latch)
  (odoc buf-rate-scale)
  (odoc buf-dur))

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

;; (Latchbellplayer (metro))
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
;; (def beats {laserbeam [1 _ _ _ _ _ _ 1 _ _ 1 _ _ _ _ 1]})

(def *beats (atom beats))

(def a {:rate 2 :amp 0.1 :time-scale-max 5})
(def a {:rate 10 :amp 0.1 :time-scale-max 10})
(def a {:rate 100 :amp 0.4 :time-scale-max 5})
(def b {:pan -1 :freq 1000})
(def c {:freq 25000 :amp 0.2})
(def d {:amp 5.0 :dur 0.25})
(def g {:freq-mul 1})

(metro :bpm 128)
(volume 0.5)
(metro :bpm)

;;(def metro (metronome 128))
(def one-beat-dur (/ 60.0 128))
(def one-bar-dur (* one-beat-dur 4))
(def beat-per-pattern 4)
(def dur-per-pattern (* one-beat-dur beat-per-pattern))

(comment
  ;;(live-sequencer (now) 2000 *beats)
  (let [ms-per-pattern (* 1000 dur-per-pattern)]
    ;;(live-sequencer (now) ms-per-pattern *beats)
    ;; (live-sequencer (metro (inc (metro))) ms-per-pattern *beats))
    (start-live-sequencer (metro (next-beat (metro) 0 beat-per-pattern)) ms-per-pattern *beats))
  (init-latchbell-mod (:rate @latchbell-arg))
  (apply-by (metro (- (next-beat (metro) 0 (* 8 beat-per-pattern)) 1/16))
            #'swap! [*beats assoc laserbeam [d _ _ [_ d d] (vec (repeat 64 0.5)) _ _ d _ _ d _ b _ _ [d d d]]])

  (apply-by (metro (- (next-beat (metro) 0 (* 8 beat-per-pattern)) 1/16))
            #'init-latchbell-mod [(:rate @latchbell-arg)])
  (apply-by (metro (- (next-beat (metro) 0 (* 8 beat-per-pattern)) 1/16))
            #'swap! [*beats assoc latchbell (vec (repeat 24 a))])

  (swap! *beats assoc laserbeam [d _ _ [d d] (vec (repeat 64 0.8)) _ _ d _ _ d _ b _ _ [d d d]])
  (swap! *beats assoc laserbeam [_ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _])
  (swap! *beats assoc latchbell [a _ a _ a _ a _ a _ a _ a _ a _])
  (swap! *beats assoc laserbeam [d c c c b c c d c c d c b c c d])
  (swap! *beats assoc grumble [g _ _ _ _ _ _ _ _  _ _ _ _ _ _ _])

  (reset! *beats beats)
  (stop))

(def beats16 {laserbeam [
                         [[d _ d _] [b _ _ _] [_ d 1 _] [[b _] [1 1 1]]]
                         [[d 1 _ d] [b 1 _ d] [d _ d _] [[b 0.5 0.5] (vec (repeat 3 0.5))]]
                         [[d 1 _ d] [b 1 _ _] [d 1 _ d] [b 1 _ _]]
                         [[d _ d _] [b _ _ _] [_ _ d _] [[b _] [1 1 1]]]
                         [[d _ d _] [b _ _ _] [_ d 1 _] [[b _] [1 1 1]]]
                         [[d 1 _ d] [b 1 _ [1 1 1]] [d _ d _] [[b 0.5 0.5] (vec (repeat 3 0.5))]]
                         [[d 1 _ d] [b 1 _ _] [d 1 _ d] [b 1 _ _]]
                         [[d _ d _] [b _ _ _] [_ _ d _] [[b _] [1 1 1]]]
                         [[d _ d _] [b _ _ _] [_ d 1 _] [[b _] [1 1 1]]]
                         [[d 1 _ d] [b 1 _ [1 1 1]] [d _ d _] [[b 0.5 0.5] (vec (repeat 3 0.5))]]
                         [[d 1 _ d] [b 1 _ _] [d 1 _ d] [b 1 _ _]]
                         [[d _ d _] [b _ _ _] [_ _ d _] [[b _] [1 1 1]]]
                         [[d _ d _] [b _ _ _] [_ d 1 _] [[b _] [1 1 1]]]
                         [[d 1 _ d] [b 1 _ [1 1 1]] [d _ d _] [[b 0.5 0.5] (vec (repeat 3 0.5))]]
                         [[d 1 _ d] [b 1 _ _] [d 1 _ d] [b 1 _ _]]
                         [[d _ d _] [b _ _ _] [_ _ d _] [[b _] [1 1 1]]]
                         ]})
(def *beats16 (atom beats16))
(reset! *beats16 beats16)
(metro :bpm 128)

(comment
  ;;(live-sequencer (now) 2000 *beats)
  (let [ms-per-pattern (* 1000 dur-per-pattern)]
    ;;(live-sequencer (now) ms-per-pattern *beats)
    ;; (live-sequencer (metro (inc (metro))) ms-per-pattern *beats))
    (live-sequencer (metro (next-beat (metro) 0 beat-per-pattern)) (* 16 ms-per-pattern) *beats16))
  (let [ms-per-pattern (* 1000 dur-per-pattern)]
    ;;(live-sequencer (now) ms-per-pattern *beats)
    ;; (live-sequencer (metro (inc (metro))) ms-per-pattern *beats))
    (start-live-sequencer (metro (next-beat (metro) 0 beat-per-pattern)) (* 16 ms-per-pattern) *beats16))
  (let [ms-per-pattern (* 1000 dur-per-pattern)]
    ;;(live-sequencer (now) ms-per-pattern *beats)
    ;; (live-sequencer (metro (inc (metro))) ms-per-pattern *beats))
    (start-live-sequencer (metro (next-beat (metro) 0 beat-per-pattern)) (* 16 ms-per-pattern) *beats16 "hoge"))

  (print @*live-sequencer-states)
  (stop-live-sequencer "hoge")
  (swap! *live-sequencer-states assoc "hoge" false)
  (reset! *beats16 beats16)
  (stop))


(comment
  (live-sequencer metro (metro) *beats 1/4 0)
  (stop))

(comment
  (odoc metronome)
  (odoc apply-by)
  (odoc apply-at)
  (odoc at-at)
  (odoc trig-id)
  (odoc on-trigger)
  (odoc on-sync-trigger)
  (odoc on-latest-trigger)
  )
