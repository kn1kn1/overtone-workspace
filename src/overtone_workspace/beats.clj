(ns overtone-workspace.beats
  (:use [overtone.live]
        [overtone-workspace sequencer ambi-rand prob-beats]
        [overtone-workspace.synth grumbles laserbeam latchbell]))

(def kick (sample "resources/kick.wav"))
;; (kick)

;; SynthDef (\fmchord01, {|freq=440, amp=0.2, dur=2|
;;                        var index_env, amp_env, out;
;;                        index_env = EnvGen.ar (Env.perc (0.0001, 0.2, 1, -4));
;;                        amp_env = EnvGen.ar (Env.perc (0.0001, 1, dur, -4), doneAction:2);
;;                        out = PMOsc.ar (freq, freq * 1.02, (index_env * 2)) * amp_env;
;;                        out = FreeVerb.ar (out.dup, 0.5, 0.8, 0.9);
;;                        Out.ar (0, out * amp);
;;                        }).add;
(definst fmchord01 [freq 440 amp 0.2 dur 2]
  (let [index-env (env-gen (perc 0.0001 0.2 1 -4))
        amp-env (env-gen (perc 0.0001 dur 1 -4) :action FREE)
        snd (pm-osc freq (* freq 1.02) (* index-env 2))
        reverb (free-verb (* snd amp-env) 0.5 0.8 0.9)]
    (pan2 (* amp reverb))))
;;(fmchord01)

(def logistics-scale (scale :C2 :minor-pentatonic (range 1 15)))
(def logistics-r 3.8)
(do
  ;;(inst-fx! fmchord01 fx-echo)
    (defn logistics-loop [metro beat x]
      (let [idx (Math/floor (* x (count logistics-scale)))
            note (nth logistics-scale idx)
            freq (midi->hz note)
            dur (* 4/8 (/ 60.0 (metro :bpm)))
            next-beat (+ 2/8 beat)
            next-x (* logistics-r x (- 1 x))] ;; x = r * x * (1 - x)
        (if (< 0 (rand-int 3))
          (at (metro beat)
              (fmchord01 :freq freq :dur dur :amp (+ 3 (rand 1)))))
        (apply-by (metro next-beat) #'logistics-loop [metro next-beat next-x])
        )))

;;(inst-fx! fmchord01 fx-echo)
;;(clear-fx fmchord01)
;; (logistics-loop sequencer-metro (sequencer-metro) 0.1)
;; (stop)

(def chord-chain-dict {:I   [:ii :iii :IV :V :vi :vii]
                       :ii  [:V :vii]
                       :iii [:IV :vi]
                       :IV  [:ii :V :vii]
                       :V   [:vi :I]
                       :vi  [:ii :IV :V]
                       :vii [:I]
                       })
(def MAJORDEGREE {:I     :i
                  :II    :ii
                  :III   :iii
                  :IV    :iv
                  :V     :v
                  :VI    :vi
                  :VII   :vii
                  :_     nil})
(defn markov-loop [metro beat dict deg]
  (let [mi (deg DEGREE)
        sc (if mi :minor :major)
        d (if mi deg (deg MAJORDEGREE))
        notes (chord-degree d :C3 sc)
        dur (+ 12/8 (* (rand) 12/8 (/ 60.0 (metro :bpm))))
        next-beat (+ 12/8 beat)
        next-deg (choose (deg dict))]
    (at (metro beat)
        (doseq [n notes]
          (let [freq (midi->hz n)]
            (fmchord01 :freq freq :dur dur :amp 1.0))))
    (apply-by (metro next-beat) #'markov-loop [metro next-beat dict next-deg])
    ))
(defn markov-pat-loop [metro beat dict deg]
  (let [mi (deg DEGREE)
        sc (if mi :minor :major)
        d (if mi deg (deg MAJORDEGREE))
        notes (chord-degree d :C3 sc)
        dur (* 6 (/ 60.0 (metro :bpm)))
        next-beat (+ 4 beat)
        next-deg (choose (deg dict))]
    (doseq [[idx n] (map-indexed vector (take 3 notes))]
      (at (metro (+ (* idx 1/2) beat))
          (let [freq (midi->hz n)]
            (fmchord01 :freq freq :dur dur :amp 1.0))
          ))
    (apply-by (metro next-beat) #'markov-pat-loop [metro next-beat dict next-deg])
    ))
(comment
  (do
    (logistics-loop sequencer-metro (sequencer-metro) 0.1)
    (markov-loop sequencer-metro (sequencer-metro) chord-chain-dict :I)
    (markov-pat-loop sequencer-metro (sequencer-metro) chord-chain-dict :I))
  (stop))

;; (markov-pat-loop sequencer-metro (sequencer-metro) chord-chain-dict :I)
;; (chord-degree :i :c4 :major)
;; (stop)

(volume (/ 50 127))

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
            ;;latchbell [1 _ 1 _ 1 _ 1 _ 1 _ 1 _ 1 _ _ 1]
            })
;; (def beats {laserbeam [1 _ _ _ _ _ _ 1 _ _ 1 _ _ _ _ 1]})
(def *beats (atom beats))

(def a {:rate 2 :amp 0.1 :time-scale-max 5})
(def a {:rate 10 :amp 0.1 :time-scale-max 10})
(def a {:rate 100 :amp 0.4 :time-scale-max 5})
(def b {:pan -1 :freq 1000})
(def c {:freq 25000 :amp 0.2})
(def d {:amp 5.0 :dur 0.25})
(def g {:freq-mul 1})

;;(volume 0.25)
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
  (do
    (swap! *beats assoc laserbeam [_])
    (swap! *beats assoc latchbell [_])
    (swap! *beats16 assoc laserbeam [_]))
  (swap! *beats assoc latchbell [a _ a _ a _ a _ a _ a _ a _ a _])
  (swap! *beats assoc laserbeam [d _ _ _ b _ _ d _ _ d _ b _ _ d])
  (swap! *beats assoc laserbeam [d c c c b c c d c c d c b c c d])
  (swap! *beats assoc laserbeam (vec (repeat 16 c)))
  (swap! *beats assoc grumble [g _ _ _ _ _ _ _ _  _ _ _ _ _ _ _])
  (swap! *beats assoc kick [[1 _ _ [1 1]] [_ _ _ 1] [_ _ 1 _] [_ _ _ [1 1 1]]])
  (swap! *beats assoc kick [1])
  (swap! *beats dissoc kick)
  (stop-live-sequencer "beats")

  (reset! *beats beats)
  (stop))
;;(volume 1.0)

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
               rand-laser [_]
               ;;rand-laser (vec (repeat 16 1))
               ;;rand-latchbell (vec (repeat 16 1))
               })
(def *fn-beats (atom fn-beats))
(comment
  (start-live-fn-sequencer (sequencer-metro (next-beat (sequencer-metro) 0 (* 8 beat-per-pattern))) (* 1 beat-per-pattern) *fn-beats "fn-beats")
  ;;(live-fn-sequencer (now) 4 *fn-beats "fn-beats")
  (swap! *fn-beats assoc ambi-rand [_])
  (swap! *fn-beats assoc ambi-rand [1 _ 1 _ 1 _ 1 _ 1 _ 1 _ 1 _ 1 _])
  (swap! *fn-beats assoc ambi-rand [[1 _ _ [1 1]] [(vec (repeat 8 1)) _ _ 1] [_ _ 1 _] [(vec (repeat 8 1)) _ _ [1 1 1]]])
  (swap! *fn-beats assoc ambi-rand (vec (repeat 16 1)))
  (swap! *fn-beats assoc rand-laser (vec (repeat 16 1)))
  (swap! *fn-beats assoc rand-laser [_])
  (swap! *fn-beats assoc rand-latchbell (vec (repeat 8 1)))
  (swap! *fn-beats assoc rand-latchbell [_])
  (do
    ;; prob-beats
    (swap! *fn-beats assoc prob-kick (take 32 (cycle [10 0 2 0 6 0 2 0 3 0 6 0 1.1 0 2 4])))
    (swap! *fn-beats assoc prob-sd (take 32 (cycle [0 0 1.1 0 1.1 0 6 0 6 0 1.1 0 1.1 0 6 0])))
    (swap! *fn-beats assoc prob-hat (take 32 (cycle [10 2 8 2 1 2])))
    (swap! *fn-beats assoc prob-openhh (take 32 (cycle [0 0 0 0 5 0 0 0 0 0 0 0 0 0 5 0]))))
  (do
    (swap! *fn-beats assoc prob-kick [_])
    (swap! *fn-beats assoc prob-sd [_])
    (swap! *fn-beats assoc prob-hat [_])
    (swap! *fn-beats assoc prob-openhh [_]))

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
  (swap! *beats16 assoc laserbeam [_])
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
