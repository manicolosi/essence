(ns questar.engine.system-spec
  (:require [speclj.core :refer :all]
            [questar.engine.system :refer :all]
            [questar.engine.component :as c]
            [questar.engine.entity :as e]
            [questar.engine.game :as g]
            [questar.engine.prototype :as p]))

(c/defcomponent component-a)
(c/defcomponent component-b)
(c/defcomponent component-c)
(c/defcomponent component-d)

(p/defentity base)
(p/defentity child-1 base
  (component-a :data)
  (component-b :data))
(p/defentity child-2 base
  (component-a :data))
(p/defentity grandchild-1 child-1
  (component-d :data))

(defn capture-all [game entities]
  (->> entities
       (map :id)
       (assoc game :processed-by-all)))

(defn capture-one [game entity]
  (update-in game [:processed-by-each] conj (:id entity)))

(defn create-game-and-spawn [prototypes]
  (let [entities (map e/spawn prototypes)]
    [(into {} (map (fn [p e] [p (:id e)]) prototypes entities))
     (reduce e/add (g/create) entities)]))

(defsystem system-with-no-requirements []
  (all [game entities] (capture-all game entities))
  (each [game entity] (capture-one game entity)))

(defsystem system-with-requirements [:component-a :component-b]
  (all [game entities] (capture-all game entities))
  (each [game entity] (capture-one game entity)))

(defsystem system-with-all-fn [:component-d]
  (all [game entities] (capture-all game entities)))

(defsystem system-with-each-fn [:component-d]
  (each [game entity] (capture-one game entity)))

(defsystem system-with-init-fn []
  (init [game] (assoc game :initialized true)))

(defsystem system-without-init-fn [])

(describe "questar.engine.system"
  (describe "process"
    (describe "with no requirements gets all entities"
      (it "in its all function"
        (let [[_ game] (create-game-and-spawn [:child-1 :child-2 :grandchild-1 :child-2])
              game-processed (process game :system-with-no-requirements)]
          (should= (-> game :entities keys sort)
                   (-> game-processed :processed-by-all sort))))
      (it "in its each function"
        (let [[_ game] (create-game-and-spawn [:child-1 :child-2 :grandchild-1 :child-2])
              game-processed (process game :system-with-no-requirements)]
          (should= (-> game :entities keys sort)
                   (-> game-processed :processed-by-each sort)))))

    (describe "with requirements gets entities that have required components"
      (it "in its all function"
        (let [[ids game] (create-game-and-spawn [:child-1 :child-2 :grandchild-1 :child-2])
              game-processed (process game :system-with-requirements)]
          (should= (sort (map ids [:child-1 :grandchild-1]))
                   (-> game-processed :processed-by-all sort))))
      (it "in its each function"
        (let [[ids game] (create-game-and-spawn [:child-1 :child-2 :grandchild-1 :child-2])
              game-processed (process game :system-with-requirements)]
          (should= (sort (map ids [:child-1 :grandchild-1]))
                   (-> game-processed :processed-by-each sort)))))

    (describe "with only an all function"
      (it "gets its all function invoked"
        (let [[ids game] (create-game-and-spawn [:child-1 :child-2 :grandchild-1 :child-2])
              game-processed (process game :system-with-all-fn)]
          (should= (sort (map ids [:grandchild-1]))
                   (-> game-processed :processed-by-all sort))))
      (it "does not get its each function invoked"
        (let [[ids game] (create-game-and-spawn [:child-1 :child-2 :grandchild-1 :child-2])
              game-processed (process game :system-with-all-fn)]
          (should-be empty? (:processed-by-each game-processed)))))

    (describe "with only an each function"
      (it "gets its each function invoked"
        (let [[ids game] (create-game-and-spawn [:child-1 :child-2 :grandchild-1 :child-2])
              game-processed (process game :system-with-each-fn)]
          (should= (sort (map ids [:grandchild-1]))
                   (-> game-processed :processed-by-each sort))))
      (it "does not get its all function invoked"
        (let [[ids game] (create-game-and-spawn [:child-1 :child-2 :grandchild-1 :child-2])
              game-processed (process game :system-with-each-fn)]
          (should-be empty? (:processed-by-all game-processed))))))

  (describe "initialize"
    (describe "when it has a init-fn"
      (it "gets invoked"
        (let [game (-> (g/create) (initialize :system-with-init-fn))]
          (should (:initialized game)))))
    (describe "when it doesn't have an init-fn"
      (it "gets ignored"
        (let [game (-> (g/create) (initialize :system-without-init-fn))]
          (should-not (:initialized game)))))))
