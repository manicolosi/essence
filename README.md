## Essence

Essence is an [entity-component-system](http://en.wikipedia.org/wiki/Entity_component_system)
(ECS) library for Clojure.

Documentation is forthcoming!

### Features

* Entities are maps of components
* Components have constructors and support most of clojure function definition
  (`defn`) features)
* Entity prototypes all defining what components an entity will have. They also
  support [prototypal inheritance](http://javascript.crockford.com/prototypal.html)]
* Systems can filter to entities they should process
* Systems can have their own state (i.e. mappings between entities and OpenGL
  VBOs)
* Everything is immutable

## Documentation

### `essence.component`

### `essence.entity`

### `essence.prototype`

### `essence.system`
