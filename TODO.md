## TODO

- Create a few example games
- Move all functions that are needed all the time to one namespace (mostly
  combine game, entity, and component). Namespaces containing `defcomponent`,
  `defprototype`, and `defsystem` can be in different namespace(s).
- Consider moving to an approach where the changes are managed by `ref`s or at
  least an option for it. The immutable and ref implementation can adhere to
  some protocol.
