# Advanced traits

## Early initialization

The problem: traits that use their own vals to instantiate other vals.

```scala
trait DodgyTrait {
  val a: Int
  val b: Int
  val c = a + b
}

val x = new DodgyTrait {
  val a = 1
  val b = 2
}
```

```
scala> x.c
res0: Int = 0 // wut?
```

In this case the Ints were automatically set to zero, so we got the wrong answer. But if we were using other types, we could have ended up throwing a NullPointerException instead.

One solution to this problem is to use so-called early initialization:

```scala
val y = new {
  val a = 1
  val b = 2
} with DodgyTrait
```

```
scala> y.c
res1: Int = 3 // yay!
```

You can do the same thing when defining a subclass:

```scala
class DodgySubclass extends { val a = 1; val b = 2 } with DodgyTrait
```

Disclaimer: Don't ever actually do this! Early initialization is such a horrible language feature that it's being planned for removal in Don Giovanni.

A much better solution is to turn the abstract `val`s into abstract `def`s. This avoids the eager initialization problem, and it also gives the person extending the trait the freedom to implement the abstract members as either `lazy val`s or `def`s as they see fit.

```scala
trait HappyTrait {
  def a: Int
  def b: Int
  val c = a + b
}

val h = new HappyTrait {
  def a = 1      // will be re-evaluated every time it's called
  lazy val b = 2 // will be evaluated once
}
```

```
scala> h.c
res2: Int = 3 // yay!
```

## Stackable traits

This is a geniunely useful language feature that makes it easy to implement the Decorator pattern with little boilerplate.

The idea is to mixin multiple traits that all:
* extend the same base trait
* override a method in that base trait, using the modifiers `abstract` and `override`
* call the `super` implementation in that method

For example, let's say we have a trait for a key-value store, and a concrete implementation that reads the values from Redis:

```scala
trait KeyValueStore {
  def getValue(key: String): String
}

class Redis extends KeyValueStore {
  override def getValue(key: String) = {
    // read value from Redis...
    "hello"
  }
}
```

Now we want to add some decorators to this, so that we can transparently support:
* in-memory caching of values
* logging

We write traits for each decorator:

```
trait Caching extends KeyValueStore {
  val cache = ???

  abstract override def getValue(key: String) = {
    cache.get(key) match {
      case Some(cachedValue) => cachedValue
      case None =>
        val valueFromKVS = super.getValue(key)
        cache.put(key, valueFromKVS)
        valueFromKVS
    }
  }
}

trait Logging extends KeyValueStore {
  abstract override def getValue(key: String) = {
    val value = super.getValue(key)
    println(s"Got a value from the key-value store. Key=$key, Value=$value")
    value
  }
}
```

Note that we have to use the `abstract override` modifier when we want to call the `super`'s method.

We simply mix them into the concrete class to enable caching and logging:

```scala
val kvs = new Redis with Caching with Logging
```

A call to `kvs.getValue("abc")` will perform a Redis lookup, decorated with caching and logging.

Note that the order of the mixins is important. The decorators are executed from **right to left**. So in the above case:

1. `Logging.getValue("abc")` will be executed
2. That method will delegate to `super.getValue("abc")`, which is `Caching.getValue("abc")`.
3. Assuming the value is not in the cache, `Caching.getValue("abc")` will delegate to `super.getValue("abc")`, which is `Redis.getValue("abc")`
4. `Redis.getValue("abc")` will perform a lookup in Redis and return a value
5. `Caching` will put the value in the cache and then return it
6. `Logging` will write a log message and then return the value

If we want to change the order of decorators (e.g. if we don't want to write a log message when we read values from the cache), then we simply change the order of the mixins:

```scala
val kvs2 = new Redis with Logging with Caching
```

## Hands on

Easter themed!

## Further reading

* [FAQ about initialization order](http://docs.scala-lang.org/tutorials/FAQ/initialization-order.html)

* [Scala's stackable trait pattern](http://www.artima.com/scalazine/articles/stackable_trait_pattern.html)
