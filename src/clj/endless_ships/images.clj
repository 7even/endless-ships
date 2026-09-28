(ns endless-ships.images
  "Resolves sprite names to image files the same way the game does
  (see source/image/ImageFileData.cpp and ImageSet.cpp in the game sources)."
  (:require [clojure.java.io :refer [file resource]]
            [clojure.string :as str]))

(def ^:private images-dir
  (-> "game/images" resource file))

(def ^:private image-extensions
  #{"png" "jpg" "jpeg" "jpe" "avif" "avifs"})

(defn- drop-suffix [s suffix]
  (if (str/ends-with? s suffix)
    [(subs s 0 (- (count s) (count suffix))) true]
    [s false]))

(defn- parse-image-path
  "Splits an image path like `ship/archon-0@2x` into the sprite name and frame info.
  The name format is `<sprite>[<blending mode><frame>][@sw][@1x|@2x]`."
  [path]
  (let [[path is-2x?] (drop-suffix path "@2x")
        [path _] (drop-suffix path "@1x")
        [path swizzle-mask?] (drop-suffix path "@sw")
        [_ sprite-name _ frame] (re-matches #"(.+)([-=^+~])([0-9]*)" path)]
    {:name (or sprite-name path)
     :frame (if (str/blank? frame)
              0
              (Long/parseLong frame))
     :2x? is-2x?
     :swizzle-mask? swizzle-mask?}))

(defn- relative-path [f]
  (-> (.toPath images-dir)
      (.relativize (.toPath f))
      str))

(def sprite-files
  "Map from sprite name to the image file (relative to images/) of its first frame.
  Normal resolution images are preferred over @2x ones."
  (->> images-dir
       file-seq
       (filter #(.isFile %))
       (keep (fn [f]
               (let [path (relative-path f)
                     [_ base extension] (re-matches #"(.+)\.([^./]+)" path)]
                 (when (contains? image-extensions (some-> extension str/lower-case))
                   (assoc (parse-image-path base)
                          :file
                          path)))))
       (filter #(and (= (:frame %) 0)
                     (not (:swizzle-mask? %))))
       (sort-by :2x?)
       (reduce (fn [files {:keys [name file]}]
                 (if (contains? files name)
                   files
                   (assoc files name file)))
               {})))

(defn image-file
  "Returns the image file for the given sprite name, or nil if there is none."
  [sprite-name]
  (let [image (get sprite-files sprite-name)]
    (when (nil? image)
      (binding [*out* *err*]
        (println (format "warning: no image found for sprite \"%s\"" sprite-name))))
    image))
