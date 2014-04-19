(ns essence.game-spec
  (:require [speclj.core :refer :all]
            [essence.game :refer :all]))

(describe "game"
  (it "has no entities when its created"
    (should-be empty? (:entities (create)))))
