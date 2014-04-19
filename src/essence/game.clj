(ns essence.game)

(defrecord Game [entities systems])

(defn create []
  (map->Game {:entities {}
              :systems {}}))
