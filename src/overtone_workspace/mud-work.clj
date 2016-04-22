(use '[overtone.live])
(use '[mud.core])
(require '[mud.timing :as time])

;;set the rate
(ctl time/root-s :rate 8.)

;;Set timing controls on a Synth

(defsynth seqer
"Plays a single channel audio buffer."
[buf 0 rate 1 out-bus 0 beat-num 0 pattern 0  num-steps 8
 beat-bus (:count time/main-beat)     ;; Our beat count
 beat-trg-bus (:beat time/main-beat)  ;; Our beat trigger
 amp 0.7
 rate-start 0.1
 rate-limit 0.9]
(let [cnt      (in:kr beat-bus)
      rander (mod cnt 1)
      beat-trg (in:kr beat-trg-bus)
      bar-trg  (and (buf-rd:kr 1 pattern cnt)
                    (= beat-num (mod cnt num-steps))
                    beat-trg)
      vol      (set-reset-ff bar-trg)]
  (out out-bus (* vol amp (scaled-play-buf :num-channels 2 :buf-num buf :rate (t-rand:kr rate-start rate-limit rander) :trigger bar-trg)))))


(defonce kick-seq (buffer 256))
(doall (map #(seqer :beat-num %1 :pattern kick-seq :num-steps 8 :buf (freesound-sample 194114)) (range 0 8)))

(pattern! kick-seq [1 0 0 1 0 0 0]
                   [1 0 0 1 0 1 0]
                   [1 0 0 1 0 0 0]
                   [1 0 0 1 0 1 0])

(pattern! kick-seq [1 1 1 1 [0 1] 1 1 1])

(stop)

(defonce pulse-s (freesound-sample 194114))

;;Every 256 beat trigger sample
(on-beat-trigger 256 #(mono-player pulse-s))

;;Dividing beats into 32 parts play sample at 31st beat
(sample-trigger 31 32 #(mono-player pulse-s))

;;Or express as a list pattern with 1 => hit.
(sample-trigger [1 0 0 0 1 0 1 0] #(mono-player pulse-s))

;;Fire once on beat 0 dividing beats into 128 parts and then remove callback.
(one-time-beat-trigger 0 128 #(mono-player pulse-s))

;;Callbacks can take the current beat as an argument, useful for varying a sample:
(one-time-beat-trigger 0 128 (fn [beat] (mono-player pulse-s :rate (if (= 0 (mod beat 256)) 1.0 1.1))))

;;Remove all registered triggers
(remove-all-sample-triggers)
(remove-all-beat-triggers)
