(ns essence.util.core
  (:require [clojure.java.io :as io]))

(let [next-ids (atom {})]
  (defn generate-id-reset! []
    (reset! next-ids {}))

  (defn generate-id [group]
    (let [id (or (group @next-ids) 0)]
      (swap! next-ids assoc group (inc id))
      id)))

(defmacro exception! [msg]
  `(throw
     (RuntimeException. (str ~msg))))

(defn field-or-self [field self]
  (if (associative? self)
    (field self)
    self))

(defn alter-and-assoc [var key val]
  `(alter-var-root ~var assoc ~(keyword key) ~val))

;; From clojure.core.incubator
(defn dissoc-in
  "Dissociates an entry from a nested associative structure returning a new
  nested structure. keys is a sequence of keys. Any empty maps that result
  will not be present in the new structure."
  [m [k & ks :as keys]]
  (if ks
    (if-let [nextmap (get m k)]
      (let [newmap (dissoc-in nextmap ks)]
        (if (seq newmap)
          (assoc m k newmap)
          (dissoc m k)))
      m)
    (dissoc m k)))

(defn resource->string [filename]
  (-> filename
      io/resource
      slurp))

(defn resource->stream [filename]
  (-> filename
      io/resource
      io/input-stream))

(defmacro tap
  [[binding-form init-expr] & exprs]
  "Like `let` that takes one binding and the evaluates to that binding."
  `(let [~binding-form ~init-expr]
     ~@exprs
     ~binding-form))
