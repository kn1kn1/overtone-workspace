(ns overtone-workspace.hello-overtone)

;;;;;;
;; Clojure

;; hello world
;;  System.out.println("Hello Clojure!")
(println "Hello Clojure!")

;; 関数
(defn hello-fn [msg]
  (println msg))
(hello-fn "Hello Clojure function!")

;; 変数
(def message "Hello Clojure variable!")
(hello-fn message)

;; Javaを呼出す
;;  java.lang.Math.PI
(. Math PI)


;;;;;;
;; Overtone
(use 'overtone.live)

(volume 0.8)

;; demo function
(demo (sin-osc))

;; defsynth
(defsynth synth-beep [freq 440 amp 0.1] (out 0 (pan2 (* amp (sin-osc freq)))))
(synth-beep)
(stop)

;; definst
(definst inst-beep [freq 440 amp 0.1] (pan2 (* amp (sin-osc freq))))
(inst-beep)
(stop)

;; simple-beep
(definst simple-beep [freq 440 amp 0.1]
  (let [env (env-gen (perc 0.1 0.2) :action FREE)]
    (* amp (* env (sin-osc freq)))))

;; 減衰後Freeして開放してるので、(stop)する必要は無い
(simple-beep)

;; now function
;;  Returns the current time in ms
(now)
(odoc now)

;; at function
;; 2秒後にsimple-beepシンセをトリガー
(at (+ (now) 2000) (simple-beep))

;; apply-at function
;; 2秒後にfn-beep関数を起動
(defn fn-beep [] (simple-beep))
(apply-at (+ (now) 2000) fn-beep [])

;; apply-by function
;; 処理に時間が掛かりそうな場合とかに使用
;; 1.7(2 - 0.3)秒後にfn-beep関数を起動
(apply-by (+ (now) 2000) fn-beep [])

;; loop
(defn scheduled-beep [curr-t sep-t]
  (let [new-t (+ curr-t sep-t)]
    (at new-t (simple-beep))
    (apply-by new-t scheduled-beep [new-t sep-t])))
(scheduled-beep (now) 1000)
(stop)

;; Steve Reich's Piano Phase
(def piece [:E4 :F#4 :B4 :C#5 :D5 :F#4 :E4 :C#5 :B4 :F#4 :D5 :C#5])
(definst beep [freq 440 amp 0.1]
  (let [env (env-gen (perc 0.1 0.2) :action FREE)]
    (* amp (* env (sin-osc freq)))))
(defn piano-phase
  [t speed notes]
  (let [n      (first notes)
        notes  (next notes)
        t-next (+ t speed)]
    (when n
      (at t
        (beep (midi->hz (note n))))
      (apply-by t-next piano-phase [t-next speed notes]))))
(let [t (now)]
  (piano-phase t (/ 338 2) (cycle piece))
  (piano-phase t (/ 335 2) (cycle piece)))
(stop)
