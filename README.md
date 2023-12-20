# TWX.Core.Extension 
This Thingworx Extension contains helpers and features missing in Thingworx. 
It's under development.

# Getting Started

# Usage

## Concurrency 
The concurrency namespace provides services and objects to synchronise services in thingworx.
It uses the standard Java concurrency features. 
As Thingworx does not allow to attach additional private objects to entities, 
this extension handles the synchronisation primiteves in a concurrent map. 
The features can be accessed by: 
- A Ressource "ConcurrencyServices"
- A script library "TWX.Core.ConcurrencyFunctions" 
- Some scriptable classes, that can be used inside your thingworx scripts.
    - To use scriptables, you must register them to the context first. 
      This can be done by calling the script library function ""
Both internally map to the same functionality, so are only two different way to access it.

### Atomic
Each call to an atomic function creates the named atomic in the background if it does not exist, yet.

#### TWX.Core.ConcurrencyFunctions
- `atomic_get`  -   Returns the current value of the atomic.
- `atomic_set`  -   Sets the value of the atomic
- `atomic_addAndGet`    -   Adds a 
- `atomic_compareAndSet`
- `atomic_decrementAndGet`
- `atomic_incrementAndGet`
- `atomic_delete`
- `atomic_exists`

#### ConcurrencyServices


#### Atomic - Scriptaible Object 
- `get()` -   Returns the current value of the atomic.
- `set(int val)`    
- `incrementAndGet()`
- `decrementAndGet()`
- `addAndGet(int delta)`
- `compareAndSet(int expect, int update)`
- `getAndAdd(int delta)`
- `getAndDecrement()`
- `getAndIncrement()`
- `getAndSet(int newVal)`


#### Example
All call's in this example use the same atomic 

```javascript
let val = 0;
val = atomic_get("exAtomic"); 	// get the value of the atomic, 0 if not defined yet ...
val = atomic_incrementAndGet("exAtomic");
val = Resources["ConcurrencyServices"].atomic_incrementAndGet({ name: "exAtomic" });

// new feature!! Atomic as class in JS:
let atom = core_getAtomic("exAtomic");
val = atom.addAndGet(1);

```


## Date


## Math


## String


## Utils

# Build and Test
To build the extension, use gradle.
The gradle scripts expect the thingworx libraries in the folder [lib/common](lib/common/README.md). 

## Gradle Targets
The gradle script contains some special targets for the Extension.
- extZip:        Creates the extension zip-File

TODO: Create targets for automatic upload to a thingworx server ... 

# Contribute
TODO: Explain how other users and developers can contribute to make your code better. 

