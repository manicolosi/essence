(ns questar.engine.prototype-spec
  (:require [speclj.core :refer :all]
            [questar.engine.prototype :refer :all]
            [questar.engine.component :refer :all]
            [questar.engine.entity :as e]))

(defcomponent hitpoints
  ([cur max] [cur max])
  ([max] (hitpoints max max)))
(defcomponent attack)
(defcomponent mob-ai)
(defcomponent sprite)

(defentity base)
(defentity orc base
  (hitpoints 20 30)
  (attack 5)
  (mob-ai :orc)
  (sprite :orc))
(defentity king-orc orc
  (hitpoints 30)
  (attack 8)
  (sprite :king-orc))
(defentity rand-orc orc
  (hitpoints (rand-int 10) 10))

(describe "prototypes"
  (it "can be spawned"
    (let [entity (e/spawn :base)]
      (should (:id entity))))
  (it "can be defined with simple component specs"
    (let [entity (e/spawn :orc)]
      (should= [[20 30] 5 :orc :orc]
               (map (partial e/data entity)
                    [:hitpoints :attack :sprite :mob-ai]))))
  (it "can be defined with complex component specs (and a parent)"
    (let [entity (e/spawn :king-orc)]
      (should= [[30 30] 8 :king-orc :orc]
               (map (partial e/data entity)
                    [:hitpoints :attack :sprite :mob-ai]))))
  (it "can use a function to define the value of a component"
    (let [entity (e/spawn :rand-orc)]
      (should-be number? (first (e/data entity :hitpoints))))))
