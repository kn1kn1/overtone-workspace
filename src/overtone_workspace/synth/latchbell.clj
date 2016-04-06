(ns overtone-workspace.synth.latchbell
  (:use [overtone.live]))

(definst latchbell [rate 9 amp 0.1]
  (let [latchrate 14.564
        rnd (lf-noise1:kr rate)
        iphase (* 2 rnd)
        trigger (impulse rate)
        index (latch (+ 6 (* 5 (lf-saw:kr latchrate iphase))) trigger)
        mul (max 0 (+ 10 (* 24 (lf-noise1:kr (/ 1.0 5)))))
        add (+ 60 (* 12 (lf-noise0:kr (/ 1.0 7))))
        ;;mul 10
        ;;add 60
        midinote (latch (+ add (* mul (lf-saw:kr latchrate iphase))) trigger)
        freq (midicps midinote)
        ratio 6.43452
        dur (+ 3.25 (* 1.75 rnd))
        env (env-gen (perc 0.0 (/ dur rate)) :action FREE)
        ]
    (* amp (* env (pm-osc [freq, (* 1.5 freq)] (* freq ratio) index)))
  ))

;;(volume (/ 40 127))

;;(latchbell :rate 10 :amp 0.2)

;;(stop)
;; (latchbell)
