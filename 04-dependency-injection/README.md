# Dependency injection

a.k.a. how to structure your Scala application.

First step: make sure every class gets its dependencies passed in as constructor arguments. (Except in the Cake pattern, which we'll come to later.)

## Manual wiring

Simplest solution: just wire everything manually at the application entry point.

```scala
// Example: an app that takes things from an SQS queue and puts them in a DB
object Start {
  // wire everything up
  val db = new Db()
  val repository = new DbRepository(db)
  val sqs = new AmazonSQSQueue()
  val queueConsumer = new QueueConsumer(sqs, repository)

  // start the app
  queueConsumer.start()
}
```

No need to make traits for interfaces, unless you want to (e.g. to make it easier to mock stuff in tests).

In tests, just test each class independently, passing in real or mock dependencies as needed.

## Manual wiring with modules (the "thin cake pattern")

The above is fine for small apps, but if the `Start` gets too big or the wiring gets too complicated, you can split it into modules using traits.

```scala
trait RepoModule {
  lazy val db = new Db()
  lazy val repository = new DbRepository(db)
}

trait AmazonModule {
  lazy val sqs = new AmazonSQSQueue()
}

// Note: MainModule depends on the other 2 modules
trait MainModule {
  self: => RepoModule with AmazonModule

  lazy val queueConsumer = new QueueConsumer(sqs, repository)
}

object Start {
  // wire everything up
  val app = new RepoModule with AmazonModule with MainModule

  // start the app
  app.queueConsumer.start()
}
```

### Dependencies between modules

Note that the `MainModule` depends on the other 2 modules because it needs a `repository` and an `sqs` in order to create its `queueConsumer`.

This dependency relationship can be written in one of 3 ways:

1. Use a self-type (as shown above) to make the module depend on the other 2 modules

2. Use `extends` to achieve the same thing:

    ```scala
    trait MainModule extends AmazonModule with RepoModule { ... }
    ```

3. Add abstract `def`s for the fields that it needs other modules to provide:

    ```scala
    trait MainModule {
      lazy val queueConsumer = new QueueConsumer(sqs, repository)
      
      // dependencies
      def sqs: AmazonSQSQueue
      def repository: Repository
    }
    ```

### Conditional logic

Note that your modules can contain any logic you like, e.g.

```scala
trait RepoModule {
  self: => ConfigModule

  lazy val repository = {
    if (config.useElasticsearch)
      new ElasticsearchRepository(new Elasticsearch())
    else
      new DbRepository(new Db())
  }

}

```

## Cake pattern

This is a Scala classic, but some people find it a bit boilerplatey.

The idea is (unsurprisingly) similar to the "thin Cake pattern" shown above. 

You create a trait for each component of your application, and each component trait has one or more abstract fields. Again, you can specify dependencies between modules using self-types.

You also put the implementation classes inside the component traits.

```scala
trait RepoComponent {
  val repository: Repository

  class Repository {
    // ...
  }

}

trait AmazonComponent {
  val sqs: AmazonSQSQueue

  class AmazonSQSQueue {
    // ...
  }

}

trait MainComponent {
  self: => RepoComponent with AmazonComponent

  val queueConsumer: QueueConsumer

  class QueueConsumer {
    // in here, we can make use of dependencies supplied by other components (sqs and repository)

    def start(): Unit = {
      while (true) {
        Thing thing = sqs.poll()
        repository.insert(thing)
      }
    }

  }

}
```

Note that, unlike in the thin Cake pattern, at this point everything is still abstract. The component traits do not supply the concrete bindings.

We supply the bindings ourselves, like we did in the "Manual wiring" section:

```scala
object Start {
  // wire everything up
  val app = new RepoComponent with AmazonComponent with MainComponent {
    val repository = new DbRepository
    val sqs = new AmazonSQSQueue
    val queueConsumer = new QueueConsumer
  }

  // start the app
  app.queueConsumer.start()
}
```

It's worth noting that we didn't have to pass the `repository` and `sqs` to the `queueConsumer`'s constructor, because it accesses them via its enclosing component trait.

## MacWire

[MacWire](https://github.com/adamw/macwire) is a handy macro-based library that can reduce boilerplace a bit.

You can replace `new MyThing(dependency1, dependency2, ...)` with `wire[MyThing]`. The macro will find the dependencies and generate the constructor call for you.

e.g.

```scala
trait RepoModule {
  lazy val db = new Db()
  lazy val repository = new DbRepository(db)
}
```

becomes

```scala
trait RepoModule {
  lazy val db = wire[Db]
  lazy val repository = wire[DbRepository]
}
```

Not very exciting in this example, but it's useful when you have a lot of constructor arguments.

Scaldi also provides a similar macro.

## Runtime DI

e.g. Scaldi, Guice

Pro: Can reduce boilerplate

Major con: If you mess up your wiring, it blows up at runtim

## Further reading

* [DI in Scala: Guide](http://di-in-scala.github.io/) - excellent introduction to the whole field (but be careful because it's actually a stealth marketing piece written by the creator of MacWire :) )
* [Real world Scala: Dependency Injection](http://jonasboner.com/2008/10/06/real-world-scala-dependency-injection-di/) - thorough intro to the Cake pattern
