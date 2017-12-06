(ns leet.config-test
  (:require [clojure.test :refer :all]
            [leet.config :refer :all]))

(deftest leetconfig-with-nil-environment

  (testing "Must support direct literal value for default"
    (is (= {:key "val"}
           (leetconfig nil {:key "val"})))
    (is (= {:key "val"}
           (leetconfig nil {:key ["val"]}))))

  (testing "Must support multiple keys"
    (is (= {:key1 "val1" :key2 "val2"}
           (leetconfig nil {:key1 "val1" :key2 "val2"})))
    (is (= {:key1 "val1" :key2 "val2"}
           (leetconfig nil {:key1 ["val1"] :key2 ["val2"]}))))

  (testing "Without environment, selects first as a value"
    (is (= {:key1 "val1"}
           (leetconfig nil {:key1 ["val1" :test "testval"]})))))

(deftest leetconfig-with-environment

  (testing "Must override with matching environment"
    (is (= {:key "test-env-val"}
           (leetconfig :test {:key ["defaultval" :test "test-env-val" :prod "prod-env-val"]})))
    (is (= {:key "prod-env-val"}
           (leetconfig :prod {:key ["defaultval" :test "test-env-val" :prod "prod-env-val"]}))))

  (testing "Must fallback if no matching environment"
    (is (= {:key "default-value"}
           (leetconfig :prod {:key ["default-value" :test "test-value"]})))))

(defn config-key->str
  ":key -> \"key\""
  [key]
  (str (name key)))

(deftest leetconfig-fn-resolve-support

  (testing "Must resolve by calling if value is fn"
    (is (= {:key "key"}
           (leetconfig nil {:key config-key->str}))))

  (testing "Must resolve by calling if value is fn on the default vector"
    (is (= {:key "key"}
           (leetconfig nil {:key [config-key->str]}))))

  (testing "Must resolve by calling if value is fn on the overrides"
    (is (= {:key "key"}
           (leetconfig :test {:key ["default-key" :test config-key->str]})))))
