(ns overtone-workspace.ambi-rand
  (:use [overtone.live]))

(def ambi-piano-b (load-sample "resources/ambi_piano.wav"))
(def ambi-choir-b (load-sample "resources/ambi_choir.wav"))
(def ambi-dark-woosh-b (load-sample "resources/ambi_dark_woosh.wav"))
(def ambi-drone-b (load-sample "resources/ambi_drone.wav"))
(def ambi-glass-hum-b (load-sample "resources/ambi_glass_hum.wav"))
(def ambi-haunted-hum-b (load-sample "resources/ambi_haunted_hum.wav"))
(def ambi-lunar-land-b (load-sample "resources/ambi_lunar_land.wav"))
(def ambi-soft-buzz-b (load-sample "resources/ambi_soft_buzz.wav"))
(def ambi-swoosh-b (load-sample "resources/ambi_swoosh.wav"))
(definst play-buf-lin [buff ambi-choir-b frames 0 start 0 rate 1.0 attack 0 sustain 0.25 release 0 amp 3]
  (let [
        trig (env-gen:kr (lin attack sustain release))
        src (play-buf 1 buff rate trig (* frames start) 0 FREE)
        env (env-gen:kr (lin attack sustain release))]
    (* src env amp)))
;;(ambi ambi-choir-b (num-frames ambi-choir-b) (rand) (rand) 0.25)
;;(ambi ambi-piano-b (num-frames ambi-piano-b) (rand) (rand) 0.25)
;;(odoc load-sample)
;;(odoc lin)

(defn- exponential [x n]
  (reduce * (repeat n x)))
(defn- pitch-to-ratio [m]
  (exponential 2.0 (/ m 12.0)))
(defn ambi-rand []
  (let [dur 0.5
        ;;dur (/ 60 (metro :bpm))
        start (rand)
        rate (* (pitch-to-ratio (choose [-5 -3 0 2])) (choose [1/8 1/4 1/2 1]))
        buf (choose [ambi-piano-b
                     ;;ambi-choir-b
                     ambi-dark-woosh-b
                     ;;ambi-drone-b
                     ambi-glass-hum-b
                     ;;ambi-haunted-hum-b
                     ;;ambi-lunar-land-b
                     ambi-soft-buzz-b
                     ambi-swoosh-b
                     ])]
    (play-buf-lin buf (num-frames buf) start rate 0 (/ dur (choose [1 2 3 4])) 0)))

(defn ambi-rand-loop [metro beat]
  (apply-at (metro beat) #'ambi-rand [])
  (apply-by (metro (+ beat 1/4)) #'ambi-rand-loop [metro (+ beat 1/4)]))

(comment
  (do
    (def metro (metronome 128))
    (ambi-rand-loop metro (metro)))
  (stop)
  )
