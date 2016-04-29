(ns overtone-workspace.ambi-pano
  (:use [overtone.live]))

;; https://twitter.com/kn1kn1/status/624227547660726272
;; loop{use_bpm 70;sample:ambi_piano,sustain:0.25,rate:pitch_to_ratio([-5,-2,0,3].choose)*[1,2,4].choose*[1,-1].choose,start:rand;sleep 0.25}

(def amb-pfb (load-sample "resources/ambi_piano.wav"))
(definst amb-pf [start 0 rate 1.0 dur 0.25]
  (let [frames (num-frames amb-pf)
        trig (env-gen:kr (lin 0 dur 0))
        src (play-buf 1 amb-pfb rate trig (* frames start) 0 FREE)
        env (env-gen:kr (lin 0 dur 0))]
    (* src env)))
;;(amb-pf (rand) (rand) 0.25)
;;(odoc load-sample)

(defn exponential [x n]
  (reduce * (repeat n x)))
(defn pitch-to-ratio [m]
  (exponential 2.0 (/ m 12.0)))
(defn ambpf-player [metro beat]
  (let [start (rand)
        rate (* (pitch-to-ratio (choose [-5 -3 0 2])) (choose [1 2 4]))
        dur (/ 60 (metro :bpm))]
    (at (metro beat) (amb-pf start rate (/ dur 2)))
    (apply-by (metro (+ beat 1/2)) #'ambpf-player [metro (+ beat 1/2)])))

(comment
  (do
    (def metro (metronome 128))
    (ambpf-player metro (metro)))
  (stop)
  )
