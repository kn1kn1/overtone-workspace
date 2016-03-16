(ns overtone-workspace.layers.layer-example
  (:require [quil.core :as q])
  (:use [quil-layer.layer]))

(defn- setup []
  ; ; Set frame rate to 30 frames per second.
  ; (q/frame-rate 30)
  ; ; Set color mode to HSB (HSV) instead of default RGB.
  ; (q/color-mode :hsb)
  ; setup function returns initial state. It contains
  ; circle color and position.
  {:color 0
   :angle 0})

(defn- update-state [state]
  ; Update sketch state by changing circle color and position.
  {:color (mod (+ (:color state) 0.7) 255)
   :angle (+ (:angle state) 0.1)})

(defn- draw-state [state]
  ; Clear the sketch by filling it with light-grey color.
  ;(q/background 240)

  (q/no-stroke)
  (q/color-mode :hsb)
  (q/fill (.backgroundColor (q/current-graphics)) 100)
  (q/rect 0 0 (q/width) (q/height))

  ; ; Set circle color.
  ; (q/fill (:color state) 255 255)
  ; ; Calculate x and y coordinates of the circle.
  ; (let [angle (:angle state)
  ;       x (* 150 (q/cos angle))
  ;       y (* 150 (q/sin angle))]
  ;   ; Move origin point to the center of the sketch.
  ;   (q/with-translation [(/ (q/width) 2)
  ;                        (/ (q/height) 2)]
  ;     ; Draw the circle.
  ;     (q/ellipse x y 100 100)))

  ; Set circle color.
  (q/color-mode :rgb)
  (q/fill 255)
  ; Draw the circle.
  (q/ellipse 0 0 (* 2 (:color state)) (* 2 (:color state)))
  )

(defrecord LayerExample [state]
  Layer
  (setup-layer-state [this]
                     (setup))
  (update-layer-state [this state]
                      (update-state state))
  (draw-layer-state [this state]
                    (draw-state state)))