(ns frozen-roses.graphics
  (:require [frozen-roses.util :as u]
            ["pixi.js" :as pixi]))

(defonce update-fn (atom nil))

(def fixed-size nil)

(defn app-width [] (or fixed-size (.-innerWidth js/window)))
(defn app-height [] (or fixed-size (.-innerHeight js/window)))
(defn app-size [] (min (app-width) (app-height)))

(defn rgb->hex [color]
  (let [[r g b] (mapv #(min 255 (int (* % 256)))
                      color)]
    (+ (* 256 256 r)
       (* 256 g)
       b)))

(defn center-canvas! [canvas]
  (let [w (app-width)
        h (app-height)
        size (app-size)]
    (set! (.-width canvas) size)
    (set! (.-height canvas) size)
    (let [style (.-style canvas)]
      (set! (.-left style) (* 0.5 (- w size)))
      (set! (.-top style) (* 0.5 (- h size))))))

(defn create-pixi-canvas! [name]
  (let [canvas (js/document.createElement "canvas")]
    (set! (.-id canvas) (str name))
    (set! (.-position (.-style canvas)) "absolute")
    (js/document.body.appendChild canvas)
    (let [app (pixi/Application.
               (clj->js {:view canvas
                         :width (app-width)
                         :height (app-height)
                         :resizeTo canvas}))]
      {:canvas canvas
       :app app})))

(defn save-frame [canvas & [name]]
  (let [img (.replace (.toDataURL canvas
                                  "image/png")
                      "image/png"
                      "image/octet-stream")
        a (js/document.createElement "a")]
    (doto a
      (.setAttribute "download" (str (or name "out")
                                     ".png"))
      (.setAttribute "href" img)
      (.click))))

(defn update-pages [timestamp]
  (when @update-fn
    (@update-fn))
  (js/window.requestAnimationFrame update-pages))

(defn init []
  (js/window.requestAnimationFrame update-pages))

(defn set-update-fn! [f]
  (reset! update-fn f))