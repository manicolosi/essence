(ns questar.engine.prototype
  (:require [questar.util.core :refer [alter-and-assoc exception!]]
            [questar.engine.component :as c]))

(def ^:private prototypes {})

(defn- defined? [name]
  (prototypes name))

(defrecord EntityPrototype [name parent specs])

(defn- evaluated-specs [specs]
  (for [[comp-name & comp-args] specs]
    (apply list comp-name (map eval comp-args))))

(defn- create [name parent specs]
  (map->EntityPrototype {:name (keyword name)
                         :parent (keyword parent)
                         :specs (evaluated-specs specs)}))

(defmacro defentity
  "Defined an entity prototype with a specification of components:
       (defentity ogre base-mob
         (sprite :ogre)
         (attack 20))"
  [name & [parent & specs]]
  (->> (create name parent specs)
    (alter-and-assoc (var prototypes) (keyword name))))

(defn components [name]
  (when-not (defined? name)
    (exception! (str "Entity prototype is not defined: " name)))
  (let [specs (-> prototypes name :specs)]
    (for [[component-name & args] specs]
      (apply c/component (keyword component-name) args))))

(defn parent [name]
  (-> prototypes name :parent))
