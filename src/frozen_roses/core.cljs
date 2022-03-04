(ns frozen-roses.core
  (:require [frozen-roses.util :as u]
            [frozen-roses.graphics :as g]
            ["pixi.js" :as pixi]
            ["d3-delaunay" :rename {Delaunay del}]
            [frozen-roses.fxhash-util :refer [fxrand
                                        fxrand-int
                                        fxchoice]]
            [frozen-roses.shaders :refer [conv-source
                                    colormap-source]]))

(def features
  {:layers (fxrand-int 3 7)
   :blur-level (fxchoice {:none 1
                          :low 2
                          :medium 2
                          :high 1})})

(set! (.-$fxhashFeatures js/window)
      (clj->js features))

(def divs (fxrand-int 10 50)) ;10-50
(def petals (fxrand-int 3 50)) ;3-50
(def layer-count (:layers features)) ;2-7
(def inner-factor-max (fxrand 0.5 1.1)) ;0.5-1.1
(def inner-factor-min (fxrand 0.2 0.4)) ;0.2-0.4
(def outer-factor-max (fxrand 0.9 1.3)) ;0.9-1.3
(def outer-factor-min (fxrand 0.4 0.9)) ;0.4-0.9
(def color-min (fxrand 0 0.6)) ;0-0.9
(def angle-offset (fxrand (* Math/PI 2)))
(def blur-strength ({:none 0
                     :low (fxrand 0 0.6)
                     :medium (fxrand 1 2.5)
                     :high (fxrand 3 4.5)}
                    (:blur-level features)))

(def layers (mapv (fn [p]
                    [(u/scale inner-factor-max inner-factor-min
                              p)
                     (u/scale outer-factor-max outer-factor-min
                              p)
                     (g/rgb->hex [(u/scale 1 color-min 
                                           p)
                                  0 0])])
                  (u/prop-range layer-count)))

(defn point-placer [p]
  (let [a (+ angle-offset
             (* Math/PI 2 p))
        r (u/scale -1 1
                   0.1 0.45
                   (Math/cos (* (+ a (* Math/PI 0.5))
                                petals)))]
    (mapv (u/scale -1 1
                   (- 0.5 r) (+ 0.5 r))
          [(Math/cos a)
           (Math/sin a)])))

(def loop-time 1800)

(defonce started? (atom false))
(defonce pixi-canvas (atom nil))
(defonce app (atom nil))
(defonce main-graphics (atom nil))
(defonce uniforms (atom nil))
(defonce current-time (atom 0))
(defonce recording? (atom false))
(defonce record-limit (atom nil))

(defn project-point [origin factor point]
  (mapv #(+ %1 (* factor (- %2 %1)))
        origin
        point))

(defn expand-polygon [origin factor polygon]
  (mapv (partial project-point origin factor)
        polygon))

(defn draw-polygon [graphics points color]
  (.lineStyle graphics 0 0)
  (.beginFill graphics
              color)
  (.drawPolygon graphics
                (clj->js
                 (mapv (fn [point]
                         (let [[x y] (mapv (partial * (g/app-size))
                                           point)]
                           (pixi/Point. x y)))
                       points)))
  (.endFill graphics))

(defn set-uniform-size! [x y]
  (aset (.-size @uniforms) 0 x)
  (aset (.-size @uniforms) 1 y))

(defn update-page []
  (when @started?
    (g/center-canvas! @pixi-canvas)
    (swap! current-time inc)
    (when (= @current-time 2)
      (js/fxpreview))
    (.clear @main-graphics)
    (draw-polygon @main-graphics
                  [[0 0]
                   [0 1]
                   [1 1]
                   [1 0]]
                  0)
    (set-uniform-size! (g/app-size) (g/app-size))
    (let [points (conj (mapv (comp point-placer
                                   (partial + (/ @current-time
                                                 (* divs loop-time))))
                             (u/prop-range divs true))
                       [0.5 0.5])
          triangulation (.from del (clj->js points))
          voronoi (.voronoi triangulation (clj->js [-5
                                                    -5
                                                    6
                                                    6]))
          cells (js->clj (vec (.cellPolygons voronoi)))]
      (doseq [[inner-factor outer-factor color] layers]
        (doseq [[cell center] (mapv vector cells points)]
          (draw-polygon @main-graphics
                        (expand-polygon [0.5 0.5]
                                        outer-factor
                                        (expand-polygon center
                                                        inner-factor
                                                        cell))
                        color)))))
  (when @recording?
    (g/save-frame @pixi-canvas @current-time)
    (when (= @record-limit @current-time)
      (reset! recording? false))))

(defn record! [& [limit]]
  (reset! recording? true)
  (reset! current-time 0)
  (reset! record-limit limit))

(defn init []
  (reset! current-time 0)
  (reset! started? true)
  (reset! main-graphics (pixi/Graphics.))
  (reset! uniforms
          (clj->js
           {"size" [0 0]
            "colorSpace" (pixi/Texture.from
                          (js/document.getElementById "color-space"))}))
  (set! (.-filters @main-graphics)
        (clj->js [(pixi/filters.BlurFilter. blur-strength)
                  (pixi/Filter. nil
                                conv-source
                                @uniforms)
                  (pixi/Filter. nil
                                colormap-source
                                @uniforms)]))
  (let [pixi-canvas-results (g/create-pixi-canvas! "voronoi-smoke")]
    (reset! pixi-canvas (:canvas pixi-canvas-results))
    (set! (.-style @pixi-canvas)
          "position:absolute;x:0px;y:0px;")
    (reset! app (:app pixi-canvas-results)))
  (.addChild (.-stage @app)
             @main-graphics)
  (js/console.log "Initializing...")
  (u/log "Features:" features)
  (g/init)
  (g/set-update-fn! update-page)
  (.dispatchEvent js/window
                  (js/Event. "resize")))

(defn ^:dev/after-load reload []
  (g/set-update-fn! update-page))

(comment
  (record! (inc loop-time))
  )