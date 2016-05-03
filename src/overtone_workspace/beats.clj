(ns overtone-workspace.beats
  (:use [overtone.live]
        [overtone-workspace sequencer]
        [overtone-workspace ambi-rand]
        [overtone-workspace.synth grumbles laserbeam latchbell]))


(def kick (sample "resources/kick.wav"))
;; (kick)

;; (ambi-loop sequencer-metro (sequencer-metro))

(volume (/ 25 127))

(defn player [metro beat rates]
  (let [rates (if (empty? rates)
                [5 4 3 3 2 2 1.5 1.5 0.75 0.75 0.5]
                rates)
        freq (midi->hz (+ (note :C3) -1))
        dur (/ 60.0 (metro :bpm))
        attack (* dur 4) ; 1bar
        rel (* dur (* 4 7)) ; 7bars
        amp 0.2]
    (at (metro beat)
        (do
          (if (zero? (mod beat 8)) (grumble :freq freq :freq-mul 0.5 :attack attack :release rel :amp amp))
          (if (zero? (mod beat 16)) (grumble :freq freq :freq-mul 1 :attack attack :release rel :amp amp))
          (if (zero? (mod beat 48)) (grumble :freq freq :freq-mul (choose rates) :attack attack :release rel :amp amp))
          ))
    (apply-by (metro (inc beat)) #'player [sequencer-metro (inc beat) rates] )
    ))
;; (player sequencer-metro (sequencer-metro) nil)

(comment
  (inst-fx! latchbell fx-chorus)
  (inst-fx! latchbell fx-distortion2)
  (clear-fx latchbell)

  (inst-fx! grumble fx-chorus)
  (clear-fx grumble)

  (inst-fx! laserbeam fx-distortion2)
  (inst-fx! laserbeam fx-distortion-tubescreamer)
  (inst-fx! laserbeam fx-chorus)
  (clear-fx laserbeam))


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

(volume 0.25)
(sequencer-metro :bpm)

(def beat-per-pattern 4)
(defn dur-per-pattern [] (* (dur-per-beat) beat-per-pattern))

(comment
  ;;(live-sequencer (now) 2000 *beats)
  (start-live-sequencer (sequencer-metro (next-beat (sequencer-metro) 0 (* 8 beat-per-pattern))) (* 1 beat-per-pattern) *beats "beats")
  (apply-by (sequencer-metro (- (next-beat (sequencer-metro) 0 (* 8 beat-per-pattern)) 1/256))
            #'init-latchbell-mod [(:rate @latchbell-arg)])
  (apply-by (sequencer-metro (- (next-beat (sequencer-metro) 0 (* 8 beat-per-pattern)) 1/256))
            #'swap! [*beats assoc laserbeam [d _ _ [_ d d] (vec (repeat 64 0.5)) _ _ d _ _ d _ b _ _ [d d d]]])
  (apply-by (sequencer-metro (- (next-beat (sequencer-metro) 0 (* 8 beat-per-pattern)) 1/256))
            #'swap! [*beats assoc latchbell (vec (repeat 24 a))])

  (init-latchbell-mod (:rate @latchbell-arg))
  (swap! *beats assoc laserbeam [d _ _ [d d] (vec (repeat 64 0.8)) _ _ d _ _ d _ b _ _ [d d d]])
  (swap! *beats assoc laserbeam [_ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _])
  (swap! *beats assoc latchbell [a _ a _ a _ a _ a _ a _ a _ a _])
  (swap! *beats assoc latchbell [_])
  (swap! *beats assoc laserbeam [d _ _ _ b _ _ d _ _ d _ b _ _ d])
  (swap! *beats assoc laserbeam [d c c c b c c d c c d c b c c d])
  (swap! *beats assoc grumble [g _ _ _ _ _ _ _ _  _ _ _ _ _ _ _])
  (swap! *beats assoc kick [[1 _ _ [1 1]] [_ _ _ 1] [_ _ 1 _] [_ _ _ [1 1 1]]])
  (swap! *beats assoc kick [_])
  (stop-live-sequencer "beats")

  (reset! *beats beats)
  (stop))

(defn rand-laser []
  (let [dur (/ 60.0 (sequencer-metro :bpm))]
    (laserbeam :pan (- (rand 2.0) 1.0) :freq (+ (rand-int 1000) 100) :dur 0.25)))

(def latchbell-arg (atom {:rate 100 :amp 0.4 :max 5}))
(init-latchbell-mod (:rate @latchbell-arg))
(defn rand-latchbell []
  (let [dur (/ 60.0 (sequencer-metro :bpm))]
        (latchbell :rate (:rate @latchbell-arg) :amp (:amp @latchbell-arg) :time-scale-max (:max @latchbell-arg))))

(def fn-beats {ambi-rand [_]
               ;;ambi-rand (vec (repeat 16 1))
               rand-laser (vec (repeat 16 1))
               rand-latchbell (vec (repeat 16 1))})
(def *fn-beats (atom fn-beats))
(comment
  (start-live-fn-sequencer (sequencer-metro (next-beat (sequencer-metro) 0 (* 8 beat-per-pattern))) (* 1 beat-per-pattern) *fn-beats "fn-beats")
  ;;(live-fn-sequencer (now) 4 *fn-beats "fn-beats")
  (swap! *fn-beats assoc ambi-rand [_])
  (swap! *fn-beats assoc ambi-rand [1 _ 1 _ 1 _ 1 _ 1 _ 1 _ 1 _ 1 _])
  (swap! *fn-beats assoc ambi-rand [[1 _ _ [1 1]] [(vec (repeat 8 1)) _ _ 1] [_ _ 1 _] [(vec (repeat 8 1)) _ _ [1 1 1]]])
  (swap! *fn-beats assoc ambi-rand (vec (repeat 16 1)))
  (swap! *fn-beats assoc rand-laser (vec (repeat 16 1)))
  (swap! *fn-beats assoc rand-latchbell (vec (repeat 16 1)))

  (apply-by (sequencer-metro (- (next-beat (sequencer-metro) 0 (* 8 beat-per-pattern)) 1/256))
            #'stop-live-sequencer ["fn-beats"])
  (println @*fn-beats)
  (stop)
  )

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

(comment
  (update-bpm 128)
  (start-live-sequencer (sequencer-metro (next-beat (sequencer-metro) 0 (* 8 beat-per-pattern))) (* 16 beat-per-pattern) *beats16 "beats16")
  (print @*live-sequencer-states)
  (stop-live-sequencer "beats16")
  (swap! *live-sequencer-states assoc "hoge" false)
  (apply-by (sequencer-metro (- (next-beat (sequencer-metro) 0 (* 16 beat-per-pattern)) 1/256))
            #'update-bpm [144])
  (reset! *beats16 beats16)
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
