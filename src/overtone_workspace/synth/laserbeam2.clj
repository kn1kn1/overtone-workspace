(ns overtone-workspace.synth.laserbeam2
  (:use [overtone.live]))

(definst laserbeam2 [rate 9 amp 0.1]
  (let [index (+ 9 (* 8 (lf-noise1 rate)))
        freq (midicps (+ 60 (* 48 (lf-noise1 rate))))
        ratio (+ 5.0 (* 2.0 (lf-noise1 rate)))
        env (env-gen (perc 0.0 (/ (ranged-rand 0.5 4) rate)) :action FREE)
        ;;env (env-gen (perc (/ 5 rate) (/ (ranged-rand 6 10) rate)) :action FREE)
        ]
    (* amp (* env (pm-osc [freq, (* 1.5 freq)] (* freq ratio) index)))
  ))

;;(volume (/ 40 127))

;;(laserbeam2 :rate 10 :amp 0.2)

;;(stop)
;; (laserbeam2)
