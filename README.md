# leetconfig

[![Build Status](https://travis-ci.org/juhofriman/leetconfig.svg?branch=master)](https://travis-ci.org/juhofriman/leetconfig)

[![Clojars Project](https://img.shields.io/clojars/v/leetconfig.svg)](https://clojars.org/leetconfig)

Leetconfig takes your clojure application configuration in different environments into a one singe place.
Additionally it gives you the freedom to retrieve parts of configuration differently in different environtments.
The main idea is to take _all_ application configuration into one single place, where you can see all configuration
instantly. Another main driver for this small project was to be able to use AWS EC2 Systems Manager only to certain configuration
keys when deployed to AWS.

Usage should be simple. First argument to `leetconfig` is the "bootstrap" environment argument, and second is the configuration specification map in following form:

```clojure
{:configuration-key-1 "direct-default-value"
 :key-2 ["if sequential, first is the default"]
 :key-3 ["default" :env-key "Value, when bootstrapped is :env-key"]
 :key-4 ["default" :env1 "value in env1" :env2 "value in env2" :env3 "value in env3"]
 :key-from-fn my-fn ; (defn my-config-fn [key] ... get value for key ... )
 :key-from-fn-2 [my-fn]
 :key-from-fn-3 ["default value" :test my-fn] ; get it?
 :key-from-fn-4 ["default value" :test "hardcoded test" :prod my-fn] ; capiche?
 }
```

Example usage would be something like this:

```clojure
(def config (leetconfig (env :environment)
                        {:appname "my-leet-ap"
                         :db-host ["localhost"
                                   :test "app-db1-test"
                                   :prod "app-db1-prod"]
                         :db-user ["localuser"
                                   :test "test-db-user"
                                   :prod "prod-db-user"]
                         :db-pass ["localpassword"
                                   :test retrieve-from-crypto-storage
                                   :prod retrieve-from-crypto-storage]}))

```

This will merely generate a map, with values selected or fetched via provider function

```clojure
; Calling above like (leetconfig nil ...) or (leetconfig :local ...) will produce a map
(def config {:appname "my-leet-ap"
             :db-host "localhost"
             :db-user "localuser"
             :db-pass "localpassword"})

; Calling with (leetconfig :test ...) will produce
(def config {:appname "my-leet-ap"
             :db-host "app-db1-test"
             :db-user "test-db-user"
             :db-pass "secretsecretsecret"})
; ...when (retrieve-from-crypto-storage :db-pass) returns "secretsecretsecret"
```

The first argument can be fetched by function or given directly. Whatever suits you. We use [environ](https://github.com/weavejester/environ)
to fetch it from system environment and start our applications like `ENVIRONMENT=test java -jar app.jar`, and keep any other environment inside
our config namespace.

Take a note, that leetconfig **does not** enforce how you actually use config map in your application. We use `clojure.spec` to
assert that configuration is actually valid, and then we have something like `(defn get-config-value [key] (key config))`.

## Roadmap

### Partial overrides for nested structures

"Partial override" support for nested structures, such as:

```clojure
(def config (leetconfig :test
                        (:texts {:name "My application"
                                 :environment-label "Local environment"}
                          :test {:environment-label "Test environment"}
                          :prod {:environment-label "Production environment"})))
; Produces
{:texts {:name "My application"
         :environment-label "Test environment"}}
```

## License

Copyright © 2017 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
