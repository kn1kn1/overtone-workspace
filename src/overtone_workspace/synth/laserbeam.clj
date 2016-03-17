(ns overtone-workspace.synth.laserbeam
  (:use [overtone.live]))


; // cf. https://github.com/brunoruviaro/SynthDefs-for-Patterns/blob/master/laserbeam.scd
; (
; SynthDef("laserbeam", {
;     arg out = 0, pan = 0.0, freq = 440, amp = 0.1, dur = 0.25;
;     var snd, freqenv, ampenv;
;     // frequency envelope
; 	freqenv = Env.perc(releaseTime: dur/2).kr(doneAction: 2);
; 	// amplitude envelope
; 	ampenv = Env.perc(releaseTime: dur/2).kr(doneAction: 2);
; 	// snd = LFTri.ar(freq: freq * freqenv, mul: ampenv);
; 	// snd = Saw.ar(freq: freq * freqenv, mul: ampenv);
; 	snd = LFSaw.ar(freq: freq * freqenv, mul: ampenv);
; 	// snd = SinOsc.ar(freq: freq * freqenv, mul: ampenv);
;     Out.ar(out, Pan2.ar(snd, pan));
; }).add;
; )
;
; (
; Pbind(
; 	\instrument, "laserbeam",
; 	\pan, Pwhite(-1.0, 1.0),
; 	\midinote, Pwhite(60, 140),
; 	// \midinote, 150,
; 	\amp, 0.25,
; 	\dur, 0.125
; 	// \dur, 1.0/Prand([4,4,4,4,8,16], inf)
; ).play;
; )

; (definst laserbeam [pan 0.0 freq 440 amp 0.1 dur 0.25]
;   (let [src (saw [freq (* freq 1.01) (* 0.99 freq)])
;         low (sin-osc (/ freq 2))
;         filt (lpf src (line:kr (* 10 freq) freq 10))
;         env (env-gen (perc 0.1 dur) :action FREE)]
;     (pan2 (* 0.8 low env filt)))
;   )
(definst laserbeam [pan 0.0 freq 440 amp 1.0 dur 0.25]
  (let [freqenv (env-gen (perc 0.01 (/ dur 4)) :action FREE)
        ampenv (env-gen (perc 0.01 (/ dur 4)) :action FREE)
        src (lf-tri [(* freq freqenv)])]
    (pan2 (* src ampenv amp) pan))
  )

;(volume (/ 40 127))

;;(laserbeam :freq 300 :dur 2)
;;(laserbeam :freq 3000 :dur 1)

;;(stop)
;; (laserbeam)
;; (laserbeam :freq 500 :armp 0.2 :dur 1)


;; (grumble :freq-mul 0.5)
;; (grumble :freq-mul 0.75)
;; (grumble :freq-mul 1)
;; (grumble :freq-mul 1.5)
;; (grumble :freq-mul 2)
;; (ctl grumble :speed 3000)
