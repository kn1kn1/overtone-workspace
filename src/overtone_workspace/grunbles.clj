(ns overtone-workspace.grumbles
  (:use [overtone.live]))

;; Inspired by an example in an early chapter of the SuperCollider book

(definst grumble [speed 6 freq-mul 1]
  (let [snd (mix (map #(* (lf-saw (* % freq-mul 100))
                          (max 0 (+ (lf-noise1:kr speed)
                                    (env-gen (perc 20 100) :action FREE))))
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

(volume (/  8  127))

(def metro (metronome 10))

(defn player [beat notes]
  (let [notes (if (empty? notes)
                [4 8 2 1.5]
                notes)]
    (at (metro beat)
        (grumble :freq-mul 0.5))
    (at (metro beat)
        (if (zero? (mod beat 5))
          (grumble :freq-mul 1)))
    (at (metro (+ 0.5 beat))
        (if (zero? (mod beat 6))
          (grumble :freq-mul (choose notes))))
    (apply-by (metro (inc beat)) #'player (inc beat) (next notes) [])
    ))

;;(player (metro) [])
;;(stop)

;; (def dirty-kick (freesound 777))
;; (dirty-kick)
;; (def kick (sample (freesound-path 2086)))
;; (kick)
;; (def close-hihat (sample-player (sample (freesound-path 802))))
