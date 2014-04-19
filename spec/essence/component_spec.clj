(ns essence.component-spec
  (:require [speclj.core :refer :all]
            [essence.component :refer :all]
            [essence.entity :as e]))

(defcomponent sprite)

(defcomponent position
  ([x y] (position x y 0))
  ([x y z] {:x x :y y :z z}))

(describe "component definitions"
  (it "instantiates a component using the definition without args"
    (let [entity (e/attach (e/create-entity) (component :sprite :orc))]
      (should= :orc
               (e/data entity :sprite))))
  (it "instantiates a component using the definition with args"
    (let [entity (e/attach (e/create-entity) (component :position 4 3))]
      (should= {:x 4 :y 3 :z 0}
               (e/data entity :position))))
  (it "doesn't throw a crazy error when its not defined"
    (should-throw RuntimeException
                  (component :does-not-exist))))
