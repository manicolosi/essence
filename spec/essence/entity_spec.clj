(ns questar.engine.entity-spec
  (:require [speclj.core :refer :all]
            [questar.engine.entity :refer :all]
            [questar.engine.component :as c]
            [questar.engine.prototype :as p]
            [questar.engine.game :as g]))

(p/defentity entity-test)

(describe "entity"
  (describe "add"
    (it "can add an entity to a game"
      (let [entity (create-entity)
            game (g/create)]
        (should= 1
                 (-> game
                     (add entity)
                     :entities
                     count))))
    (it "adding the same entity will overwrite it"
      (let [entity-a (create-entity)
            game (-> (g/create) (add entity-a))
            entity-b (assoc entity-a :modified true)]
        (should (-> game
                    (add entity-b)
                    :entities
                    first
                    second
                    :modified)))))

  (describe "spawn"
    (it "creates an entity with an id from a prototype definition"
      (should (:id (spawn :entity-test)))))

  (describe "despawn"
    (it "removes an entity from the game using an id"
      (let [entity (create-entity)
            game (-> (g/create)
                     (add entity))]
        (should-be empty?
                   (-> game
                       (despawn (:id entity))
                       :entities)))))

  (describe "find-entity"
    (it "will return an entity in the game by id"
      (let [entity (create-entity)
            game (-> (g/create) (add entity))]
        (should= entity
                 (find-entity game (:id entity)))))
    (it "will return an entity in the game by an existing entity"
      (let [entity (create-entity)
            game (-> (g/create) (add entity))]
        (should= entity
                 (find-entity game entity))))
    (it "will return the game's version of an entity"
      (let [entity-a (create-entity)
            game (-> (g/create) (add entity-a))
            entity-b (assoc entity-a :modified true)]
        (should= entity-a
                 (find-entity game entity-b)))))

  (describe "attach"
    (it "attaches a component to an entity"
      (let [entity (create-entity)
            component (c/create :test-component :my-data)]
        (should= component
                 (:test-component (attach entity component))))))

  (describe "detach"
    (it "detaches a component from an entity"
      (let [component (c/create :test-component :my-data)
            entity (attach (create-entity) component)]
        (should-be nil?
                   (:test-component (detach entity :test-component))))))

  (describe "data"
    (it "gets the data out of a entity's component"
      (let [component (c/create :test-component :my-data)
            entity (attach (create-entity) component)]
        (should= :my-data
                 (data entity :test-component)))))

  (describe "update"
    (it "updates a component with a fn and no args"
      (let [component (c/create :hitpoints 5)
            entity (attach (create-entity) component)]
        (should= 4
                 (-> entity
                     (update :hitpoints dec)
                     (data :hitpoints)))))
    (it "updates a component with a fn and some args"
      (let [component (c/create :hitpoints 5)
            entity (attach (create-entity) component)]
        (should= 3
                 (-> entity
                     (update :hitpoints - 2)
                     (data :hitpoints)))))
    (it "throws an exception if the component is not on the entity"
      (let [entity (create-entity)]
        (should-throw RuntimeException
                      (update entity :does-not-exist - 2)))))

  (do
    (c/defcomponent component-1)
    (c/defcomponent component-2)
    (c/defcomponent component-3)

    (p/defentity querying-test-base)
    (p/defentity with-1-and-2 querying-test-base
      (component-1 :data)
      (component-2 :data))
    (p/defentity with-only-1 querying-test-base
      (component-1 :data)
      (component-3 :data))
    :not-nil)

  (describe "has-component?"
    (it "should be truthy when the entity has the component"
        (let [entity (-> (create-entity)
                         (attach (c/create :component-1 :data))
                         (attach (c/create :component-2 :data)))]
          (should (has-component? entity :component-1))))
    (it "should be falsey when the entity doesn't have the component"
        (let [entity (-> (create-entity)
                         (attach (c/create :component-1 :data))
                         (attach (c/create :component-2 :data)))]
          (should-not (has-component? entity :doesnt-have-this-compnent)))))

  (describe "has-components?"
    (it "should be truthy when the entity has all the components"
      (let [entity (-> (create-entity)
                       (attach (c/create :component-1 :data))
                       (attach (c/create :component-2 :data)))]
        (should (has-components? entity [:component-1 :component-1]))))
    (it "should be falsey when the entity doesn't have any the components"
      (let [entity (-> (create-entity)
                       (attach (c/create :component-1 :data))
                       (attach (c/create :component-2 :data)))]
        (should-not (has-components? entity [:dont-have-1 :dont-have-2]))))
    (it "should be falsey when the entity doesn't have all the components"
        (let [entity (-> (create-entity)
                         (attach (c/create :component-1 :data))
                         (attach (c/create :component-2 :data)))]
          (should-not (has-components? entity [:dont-have-1 :component-1])))))

  (describe "query"
    (it "should be able to filter the entities in the game based on components"
      (let [entities (vec (map spawn [:with-1-and-2 :with-only-1 :with-1-and-2]))
            game (reduce add (g/create) entities)]
        (should= (sort [(:id (entities 0)) (:id (entities 2))])
                 (sort (map :id (query game [:component-1 :component-2]))))))
    (it "returns all entities with an empty vector of components"
      (let [entities (vec (map spawn [:with-1-and-2 :with-only-1 :with-1-and-2]))
            game (reduce add (g/create) entities)]
        (should= (sort (map :id entities))
                 (sort (map :id (query game []))))))))
