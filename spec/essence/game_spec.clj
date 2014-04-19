(ns questar.engine.game-spec
  (:require [speclj.core :refer :all]
            [questar.engine.game :refer :all]))

(describe "game"
  (it "has no entities when its created"
    (should-be empty? (:entities (create)))))
