(ns overtone-workspace.grumbles
  ;;(:require  [overtone.at-at :as at-at])
  (:use [overtone.live]
        [overtone-workspace.synth laserbeam latchbell]))

;; Inspired by an example in an early chapter of the SuperCollider book

;; (def kick (sample "resources/kick.wav"))
;; (kick :start 0 :end 0.1)


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
        (laserbeam :pan (- (rand 2.0) 1.0) :freq (+ (rand-int 1000) 12000) :dur 0.25))
    (at (metro (+ beat 0.25))
        (laserbeam :pan (- (rand 2.0) 1.0) :freq (+ (rand-int 1000) 12000) :dur 0.25))
    (at (metro (+ beat 0.5))
        (laserbeam :pan (- (rand 2.0) 1.0) :freq (+ (rand-int 1000) 12000) :dur 0.25))
    (at (metro (+ beat 0.75))
        (laserbeam :pan (- (rand 2.0) 1.0) :freq (+ (rand-int 1000) 12000) :dur 0.25))
    (apply-by (metro (inc beat)) #'laserplayer (inc beat) [])
    ))

;; (laserplayer (metro))

;; (stop)

(def latchbell-arg (atom {:rate 9 :amp 0.2 :max 2.5}))

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

;; (def dirty-kick (freesound 777))
;; (dirty-kick)
;; (def kick (sample (freesound-path 2086)))
;; (kick)
;; (def close-hihat (sample-player (sample (freesound-path 802))))
