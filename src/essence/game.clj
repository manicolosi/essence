(ns questar.engine.game)

(defrecord Game [entities systems])

(defn create []
  (map->Game {:entities {}
              :systems {}}))
