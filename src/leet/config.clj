(ns leet.config)

(defn- env-override
  [env-key env-overrides]
  (some (fn [[env value]] (when (= env env-key) value)) (partition 2 env-overrides)))


(defn- resolve-value
  [env-key env-defs]
  (if (sequential? env-defs)
    (let [[default-value & env-overrides] env-defs]
        (or (env-override env-key env-overrides) default-value))
    env-defs))

(defn- apply-if-fn
  [key val]
  (if (fn? val)
    (val key)
    val))

(defn leetconfig
  "Creates map..."
  [env-key def-map]
  (let [resolve-with-env (partial resolve-value env-key)]
    (reduce-kv #(assoc %1 %2 (apply-if-fn %2 (resolve-with-env %3))) {} def-map)))
