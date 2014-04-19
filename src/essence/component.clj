(ns essence.component
  (:require [essence.util.core :refer [exception! alter-and-assoc]]))

(defrecord Component [name data])

(defn create
  "Creates a raw component with a name and data"
  [name data]
  (Component. name data))

(defn- wrap-if-first [pred wrap-fn data]
  (if (pred (first data))
    (wrap-fn data)
    data))

(defn- build-bindings-and-exprs [name bindings-and-exprs]
  (for [[bindings & exprs] (wrap-if-first vector? list bindings-and-exprs)]
    `(~bindings (let [data# (do ~@exprs)]
                (if (instance? Component data#)
                  data#
                  (create ~(keyword name) data#))))))

(defn- build-function [name bindings-and-exprs]
  `(fn ~name ~@(build-bindings-and-exprs name bindings-and-exprs)))

(def ^:private components {})

(defmacro defcomponent
  "Defines a component. This is just like defining a function with defn:
       (defcomponent position
         ([x y] (position x y 0)
         ([x y z] (Vector3. x y z))))

   Notice that you can have multiple arities and even call a different arity of
   the same component."
  ([name & bindings-and-exprs]
   (->> (build-function name bindings-and-exprs)
        (alter-and-assoc (var components) name)))
  ([name]
   `(defcomponent ~name [~name] ~name)))

(defn- component-defined? [name]
  (name components))

(defn component
  "Instantiates a component from a component definition"
  [name & args]
  (when-not (component-defined? name)
    (exception! (str "Component not defined: " name)))
  (apply (name components) args))
