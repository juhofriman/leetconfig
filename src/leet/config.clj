(ns leet.config)

; TODO: spike implementation

(defn call-if-fn
  [key val]
  (if (fn? val)
    (val key)
    val))

(defn select-value [env key default & overrides]
  (let [o (into {} (map vec (partition 2 overrides)))]
    (if-let [e (get o env)]
      (if (associative? default)
        (merge default e)
        (call-if-fn key e))
      (call-if-fn key default))))

(defmacro leetconfig
  "Evaluates body to form a map. First argument is the used environment key
   such as :test or :production. Rest arguments are key-value constraints such as
   (:key \"default value\" :env-1 \"Override value\") If value is a fn, it is
   called with key as an argument"
  [env-key & defs]
  (let [ek (eval env-key)]
    (into {} (map
               (fn [[key default & overrides]]
                 (vector key `(select-value ~ek ~key ~default ~@overrides)))
               defs))))
