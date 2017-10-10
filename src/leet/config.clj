(ns leet.config)


(defmacro leetconfig
  [env-key & defs]
  (into {} (map (fn [[k v & overrides]]
                  (vector k `(cond ~@(reduce (fn [acc [e v]]
                                               (concat acc [`(= ~e (eval ~env-key)) `(if (fn? ~v) (~v ~k) ~v)]))
                                             []
                                             (partition 2 overrides))
                                   :else (if (fn? ~v) (~v ~k) ~v)))) defs)))

