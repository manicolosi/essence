(defcomponent hit-points
  ([max] (hit-points max max))
  ([current max] {:current current :max max}))

(defcomponent attack-points)
(defcomponent defense-points)

(defcomponent alignment)

(defprototype monster
  (alignment :evil)
  (hit-points 100)
  (attack-points 10)
  (defense-points 10))

(defprototype hero
  (alignment :good)
  (hit-points 10)
  (attack-points 8)
  (defense-points 8))

(defn attack
  "Attacker attacks attackee taking into consideration the attacker's
  attack-points and the attackee's defense-points."
  [game attackee attacker]
  )

(defn deal-death
  "Kills an entity and despawns them from the game."
  [game entity]
  )

(defsystem attack
  [:hit-points]
  (process [game attacker]
    (-> (find-evil game)
        (attack attacker))))

(defsystem death
  [:hit-points]
  (process [game entity]
    (when (should-die? entity)
      (deal-death game entity))))
