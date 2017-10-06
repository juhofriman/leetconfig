(ns leet.config)

; TODO: spike implementation

(defn call-if-fn
  [key val]
  (if (fn? val)
    (val key)
    val))

(defn c [env key default & overrides]
  (let [o (into {} (map vec (partition 2 overrides)))]
    (if-let [e (get o env)]
      (if (associative? default)
        (merge default e)
        (call-if-fn key e))
      (call-if-fn key default))))

(defmacro leetconfig [env-key & defs]
  (into {} (map (fn [[key default & overrides]] (vector key `(c ~env-key ~key ~default ~@overrides))) defs)))
