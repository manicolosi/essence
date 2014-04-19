(ns questar.engine.system
  (:require [questar.util.core :refer [exception! alter-and-assoc]]
            [questar.engine.entity :as e]))

(ns-unmap *ns* 'System)

(defrecord System [name requirements each-fn all-fn])

(defn- fn-key [{:keys [name]}]
  (keyword (str name "-fn")))

(defn- fn-decl [{:keys [name bindings body]}]
  `(fn ~name ~bindings (do ~@body)))

(defn- build-function [[name bindings & body]]
  ((juxt fn-key fn-decl)
         {:name name :bindings bindings :body body}))

(defn- create [system-name requirements fn-specs]
  `(map->System
    ~(merge {:name system-name :requirements requirements}
            (into {} (map build-function fn-specs)))))

(def ^:private systems {})

(defmacro defsystem
  "Define a system. required-components is a vector of components an entity must
   have to be processed by this system. fn-specs can contain specification for
   either an each-fn, all-fn or both."
  [system-name required-components & fn-specs]
  (->> (create (keyword system-name) required-components fn-specs)
       (alter-and-assoc (var systems) system-name)))

(defn- defined? [system-name]
  (system-name systems))

(defn- process-all-fn [game all-fn entities]
  (if all-fn
    (all-fn game entities)
    game))

(defn- process-each-fn [game each-fn entities]
  (if each-fn
    (reduce each-fn game entities)
    game))

(defn- get-functions [system entities]
  (map (fn [[k f]] #(f %1 (k system) entities))
       {:all-fn process-all-fn
        :each-fn process-each-fn}))

(defn process
  "Processes system with system-name, returning the game."
  [game system-name]
  (when-not (defined? system-name)
    (exception! (str "System is not defined: " system-name)))
  (let [system (systems system-name)
        entities (e/query game (:requirements system))
        functions (get-functions system entities)]
    (reduce (fn [game f] (f game)) game functions)))

(defn initialize
  "Initializes a system."
  [game system-name]
  (when-not (defined? system-name)
    (exception! (str "System is not defined: " system-name)))
  (if-let [init-fn (-> system-name systems :init-fn)]
    (init-fn game)
    game))
