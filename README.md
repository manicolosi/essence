## Essence

Essence is an [entity-component-system](http://en.wikipedia.org/wiki/Entity_component_system)
(ECS) library for Clojure.

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

## Usage

Essence is not yet available on Clojars. It will be available after I clean it
up a bit and improve the documentation.

Remember, Essence is immutable, so all functions that "modify" a value,
actually returns the modified value.

### `essence.game`

A game is a simple wrapper around entities and systems. I game can be created
using the `create` function:

```
(require [essence.game as g])

(let [game (g/create)]
  ...)
```

### `essence.entity`

An entity is a map of of components. Components can be added or removed.
Entities can be created directly or from a prototype (see `essence.prototype`
section).

`(create-entity)`
`(create-entity component-map)`

Creates a new entity, suitable for `add`ing to a game.

`(add game entity)`

Adds an entity to the game.

`(attach entity component)`

Attaches a component to an entity.

`(detach entity component-name)`

Detaches a component from an entity.

`(data entity component-name)`

Fetches data from a component attached to an entity.

`(find-entity game entity-id)`

Finds an entity in a game by id.

`(query game components)`

Return a sequence of entities with components named by the `components` vector.

`(update entity component name fn & args)`

Updates the data of a cojponent using `fn` and `args`. Like Clojure's
`update-in`.

`(spawn protoype-name)`

Spawns an entity using a prototype as a template.

`(despawn game entity)`

Removes an entity from the game.

### `essence.component`

This namespace is primarily responsible for allowing you to define new
components.

`(create name data)`

Creates a raw component with `name` and `data`. This doesn't have much use and
you should probably use `defcomponent` to define components instead.

`(defcomponent name [params] body)`

Defines a new component with `name`. Instantiating this component using
`component` calls `body` with the passed parameters.

Simple example:

```
(defcomponent sprite)
```

No function body or parameters means this component simply passing the arguments
passed to `component` straight through.

Multiple arity example:

```
(defcomponent position
  ([x y] (position x y 0))
  ([[x y z] {:x x :y y :z z}))
```

Here, two the `position` component supports an arity of two or three. The two
arity version simply calls the three arity version with 0 as its third
parameter.

`(component name & args)`

Instantiates a component with `name` passing `args` to the component
constructor. Here's how you would instantiate components from the two previous
examples:

`(component sprite :bad-guy)`
`(component position 2 3)`

### `essence.prototype`

### `essence.system`
