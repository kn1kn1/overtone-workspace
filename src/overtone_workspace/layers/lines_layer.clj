(ns overtone-workspace.layers.lines-layer
  (:require [quil.core :as q])
  (:use [quil-layer.layer]))

(def num-of-lines 5)

(defn- setup [] {})
(defn- update-state [state] {})

(defn- draw-state [state]
  (let [r (/ (q/width) num-of-lines)
        rr (- r)]
    (q/smooth)

    (q/color-mode :rgb)
    (q/background 200)
    (q/fill 220)
    (q/stroke 220)

    (dorun
      (for [i (range 0 num-of-lines)]
        (let [x (+ (* i (/ (q/width) num-of-lines)) (q/random rr r))
            y 2
            x2 (+ (* i (/ (q/width) num-of-lines)) (q/random rr r))
            y2 (- (q/height) 2)]
          (q/stroke-weight r)
          (q/line x y x2 y2))
      )))
  )

(defrecord LinesLayer [state]
  Layer
  (setup-layer-state [this]
                     (setup))
  (update-layer-state [this state]
                      (update-state state))
  (draw-layer-state [this state]
                    (draw-state state)))
