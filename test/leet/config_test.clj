(ns leet.config-test
  (:require [clojure.test :refer :all]
            [leet.config :refer :all]))

(deftest leetconfig-nil-environment-test

  (testing "Must map values by key"
    (is (= {:key "my-value"} (leetconfig nil
                                         (:key "my-value")))))

  (testing "Must support multiple keys"
    (is (= {:key-1 "my-value-1"
            :key-2 "my-value-2"
            :key-3 "my-value-3"
            :key-4 "my-value-4"}
           (leetconfig nil
                       (:key-1 "my-value-1")
                       (:key-2 "my-value-2")
                       (:key-3 "my-value-3")
                       (:key-4 "my-value-4")))))

  (testing "Must take default when nil environment"
    (is (= {:value "my-value"} (leetconfig nil
                                           (:value "my-value"
                                             :test "test-environment-value"))))))

(deftest leetconfig-environment-override-test

  (testing "Must map values by key"
    (is (= {:key "test-value"} (leetconfig :test
                                           (:key "my-value"
                                             :test "test-value")))))

  (testing "Must fallback to default if environment override is not defined"
    (is (= {:key "test-value"
            :key-2 "default-value"}
           (leetconfig :test
                       (:key "my-value"
                         :test "test-value")
                       (:key-2 "default-value")))))

  (testing "Must support multiple environments"
    (is (= {:key "the-production-value"} (leetconfig :production
                                                     (:key "default-value"
                                                       :test "test-value"
                                                       :production "the-production-value"))))))

(deftest leetconfig-initialization-test

  (testing "must evaluate initial environment argument"
    (is (= {:key "test-value"} (leetconfig (identity :test)
                                           (:key "my-value"
                                             :test "test-value"))))))

(defn value-provider-fn
  [key]
  (str "provided-value-for-" (name key)))

(defn two-arg-provider
  [f key]
  (str f "-" (name key)))

(deftest provider-fn-test

  (testing "Must support function as a provider for value"
    (is (= {:key "provided-value-for-key"} (leetconfig nil
                                                       (:key value-provider-fn)))))

  (testing "Must support function as a provider for value in environment override"
    (is (= {:key "provided-value-for-key"} (leetconfig :test
                                                       (:key "default-value"
                                                         :test value-provider-fn)))))

  (testing "Must support partial defs"
    (is (= {:key "partial-key"} (leetconfig nil
                                           (:key (partial two-arg-provider "partial")))))))

(deftest nested-structure-override-test

  (testing "Must support nested structures"
    (is (= {:key {:a "value"}} (leetconfig nil
                                           (:key {:a "value"})))))

  (testing "Must support overriding nested structures"
    (is (= {:key {:a "test value"}} (leetconfig :test
                                           (:key {:a "value"}
                                             :test {:a "test value"})))))

  ;; TODO, not supported currently
  (comment (testing "Must support nested structures partially"
             (is (= {:key {:a "value"}} (leetconfig nil
                                                    (:keys {:a "value"})))))))