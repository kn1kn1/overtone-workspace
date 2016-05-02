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
(definst ambi [buff ambi-choir-b frames 0 start 0 rate 1.0 dur 0.25 amp 3]
  (let [
        trig (env-gen:kr (lin 0 dur 0))
        src (play-buf 1 buff rate trig (* frames start) 0 FREE)
        env (env-gen:kr (lin 0 dur 0))]
    (* src env amp)))
;;(ambi ambi-choir-b (num-frames ambi-choir-b) (rand) (rand) 0.25)
;;(ambi ambi-piano-b (num-frames ambi-piano-b) (rand) (rand) 0.25)
;;(odoc load-sample)

(defn exponential [x n]
  (reduce * (repeat n x)))
(defn pitch-to-ratio [m]
  (exponential 2.0 (/ m 12.0)))
(defn ambi-player [metro beat]
  (let [start (rand)
        rate (* (pitch-to-ratio (choose [-5 -3 0 2])) (choose [1/8 1/4 1/2 1]))
        dur (/ 60 (metro :bpm))
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
    (at (metro beat) (ambi buf (num-frames buf) start rate (/ dur (choose [1 2 3 4]))))))

(defn ambi-loop [metro beat]
  (apply-at (metro beat) #'ambi-player [metro beat])
  (apply-by (metro (+ beat 1/4)) #'ambi-loop [metro (+ beat 1/4)]))

(comment
  (do
    (def metro (metronome 128))
    ;;(ambi-player metro (metro)))
    (ambi-loop metro (metro)))
  (stop)
  )
