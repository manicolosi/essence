(ns essence.entity
  (:require [essence.util.core :refer [field-or-self generate-id exception! dissoc-in]]
            [essence.component :as c]
            [essence.prototype :as p]))

(defrecord Entity [id])

(defn- entity-path [id-or-entity]
  [:entities (field-or-self :id id-or-entity)])

(defn has-component?
  [entity component-name]
  (component-name entity))

(defn has-components?
  [entity component-names]
  (reduce (fn [acc c] (and acc (has-component? entity c))) true component-names))

(defn create-entity
  ([] (map->Entity {:id (generate-id :entity)}))
  ([m] (map->Entity m)))

(defn find-entity
  "Finds an entity by an entity id or by an entity (in which case it will use
  only the entity's id and return the game's copy of entity"
  [game id-or-entity]
  (get-in game (entity-path id-or-entity)))

(defn query
  "Returns a seq of entities with components named by the components vector."
  [game components]
  (filter
    #(has-components? %1 components)
    (vals (:entities game))))

(defn add
  "Adds an entity to game"
  [game entity]
  (assoc-in game (entity-path entity) entity))

(defn attach
  "Attaches a component to an entity"
  [entity component]
  (assoc entity (:name component) component))

(defn detach
  "Detaches a component from an entity"
  [entity component-name]
  (dissoc entity component-name))

(defn data
  "Fetches data from a component attached to an entity"
  [entity component-name]
  (-> entity
      component-name
      :data))

(defn update
  "Updates the data of a component using `fn` and `args`"
  [entity component-name fn & args]
  (when-not (has-component? entity component-name)
    (exception! (str "Component not found: "
                     component-name
                     "\nEntity: " entity)))
  (apply update-in entity [component-name :data] fn args))

(defn spawn
  "Spawns an entity from a prototype definition (defentity)"
  [name]
  (let [parent (p/parent name)
        components (p/components name)
        entity (if parent (spawn parent) (create-entity))]
    (reduce
      attach
      entity
      components)))

(defn despawn
  "Removes an entity from the game"
  [game entity]
  (dissoc-in game (entity-path entity)))
