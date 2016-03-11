(ns overtone-workspace.grumbles
  (:use [overtone.live]))

;; Inspired by an example in an early chapter of the SuperCollider book

(definst grumble [speed 6 freq-mul 1 attack 10 release 50]
  (let [snd (mix (map #(* (lf-tri (* % freq-mul 100))
                          (max 0 (+ (lf-noise1:kr speed)
                                    (env-gen (perc attack release) :action FREE))))
                      [1 (/ 2 3) (/ 3 2) 2]))]
    (pan2 snd (sin-osc:kr 0.125))))

;;(stop)
;; (grumble)
;; (grumble :freq-mul 0.5)
;; (grumble :freq-mul 0.75)
;; (grumble :freq-mul 1)
;; (grumble :freq-mul 1.5)
;; (grumble :freq-mul 2)
;; (ctl grumble :speed 3000)

(volume (/  10  127))

(def metro (metronome 128))

(defn player [beat rates]
  (let [rates (if (empty? rates)
                [4 8 2 2 1.5 1.5]
                rates)
        bpm (metro :bpm)
        dur (/ 60.0 bpm)
        attack (* dur 4) ; 1bar
        rel (* dur (* 4 7))]  ; 7bars
    (at (metro beat)
        (do
          (if (zero? (mod beat 8)) (grumble :freq-mul 0.5 :attack attack :release rel))
          (if (zero? (mod beat 8)) (grumble :freq-mul 1 :attack attack :release rel))
          (if (zero? (mod beat 16))
            (grumble :freq-mul (choose rates) :attack attack :release rel))
          ))
    (apply-by (metro (inc beat)) #'player (inc beat) (next rates) [])
    ))

;;(player (metro) [])
;;(stop)

;; (def dirty-kick (freesound 777))
;; (dirty-kick)
;; (def kick (sample (freesound-path 2086)))
;; (kick)
;; (def close-hihat (sample-player (sample (freesound-path 802))))
