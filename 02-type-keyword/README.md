# The `type` keyword

## Type aliases

In Scala you can define aliases for types, e.g.

```
type UserId = Int
type UsersById = Map[UserId, User]
type Aggregate = (Foo, Bar, Baz, Hoge) => Result
```

Aliasing a complex and commonly used type can make your code more readable, e.g.

```
def buildAndAggregateStuff(aggregate: Aggregate): Result
```

rather than

```
def buildAndAggregateStuff(aggregate: (Foo, Bar, Baz, Hoge) => Result): Result
```

Even if the types aren't complex, I find it sometimes makes code more readable if you sprinkle in some domain-specific aliases:

```
type UserId = Int
type ArticleId = Int

def hasUserViewedArticle(user: UserId, article: ArticleId): Boolean
```

### Not type-safe

It should be noted that this type aliasing doesn't give you any type-safety. `UserId` and `ArticleId` are both simply aliases for Int, so you can use either of them anywhere that you need an Int. For example:

```
val user: UserId = 123
val article: ArticleId = 456
hasUserViewedArticle(article, user) // Note: parameters are wrong way round
```

the above code will compile and run, even though we accidentally switched the user and article parameters, because they were both Ints.

If you want a more safe way to do this, you have a few options:

* Always use named parameters. In this case the compiler won't give you any help, but at least somebody reviewing your code will probably spot the typo.

    ```
    hasUserViewedArticle(user = user, article = article)
    ```

* Use case classes to model your domain. A downside of this is that you will need to add custom serialization code if you don't want to end up with weird-looking JSON.

    ```
    case class UserId(value: Int)
    case class ArticleId(value: Int)
    hasUserViewedArticle(UserId(123), ArticleId(456))
    ```

* Use tagged types (available with Scalaz or Shapeless). This gives you type-safety without the case class baggage.

    ``` 
    trait UserId
    trait ArticleId
    
    import shapeless._, tag._
    val user = tag[UserId](123)
    val article = tag[ArticleId](456)

    def hasUserViewedArticle(user: Int @@ UserId, article: Int @@ ArticleId): Boolean = {
      true
    }

    hasUserViewedArticle(user, article) // OK
    hasUserViewedArticle(article, user) // Compile error
    ``` 

## Abstract type members

As you know, traits can contain abstract fields and methods, which have to be implemented by a concrete class:

```
trait Animal {
  val numberOfLegs
  def eat(food: Food): Unit
  def makeNoise(): Noise
}

class Cow extends Animal {
  val numberOfLegs = 4
  def eat(food: Food) = { ... }
  def makeNoise(): Noise = { new Moo() }
}
```

But they can also contain abstract types:

```
trait Animal {
  type FavouriteFood
  type Call

  def eat(food: FavouriteFood): Unit
  def makeNoise(): Call
}

class Cow extends Animal {
  type FavouriteFood = Grass
  type Call = Moo
  def eat(food: Grass) = { ... }
  def makeNoise(): Moo = { new Moo() }
}
```

Of course, you could have done this with generics instead, e.g.

```
trait Animal[FavouriteFood, Call] {
  def eat(food: FavouriteFood): Unit
  def makeNoise(): Call
}

class Cow extends Animal[Grass, Moo] {
  def eat(food: Grass) = { ... }
  def makeNoise(): Moo = { new Moo() }
}
```

In this simple case, both approaches work fine, and which one you choose is a matter of taste. 

One situation where type members can reduce boilerplate and make code more readable is when you want to mixin a trait that provides the concrete types:

```
trait MyTrait[A] { ... }

trait MyIntMixin { self: MyTrait[Int] => ... }

// Using generics, we have to type the `[Int]` bit here as well as in the mixin
class MyConcreteClass extends MyTrait[Int] with MyIntMixin
```

```
// Rewritten using type member approach
trait MyTrait { type A }

trait MyIntMixin { self: MyTrait => 
  type A = Int
  ...
}

class MyConcreteClass extends MyTrait with MyIntMixin
```

Bill Venners gives a good real-world example of this situation (see Further Reading).

Martin Odersky also has this to say, but I don't really understand what he means:

>But in practice, when you [use type parameterization] with many different things, it leads to an explosion of parameters, and usually, what's more, in bounds of parameters. At the 1998 ECOOP, Kim Bruce, Phil Wadler, and I had a paper where we showed that as you increase the number of things you don't know, the typical program will grow quadratically. So there are very good reasons not to do parameters, but to have these abstract members, because they don't give you this quadratic blow up.

## Path dependent types

Now we enter "definitely can't do that in Java" territory...

We can make the type of something depend on the *instance* that contains it.

```
class Service {
  case class User(id: Int, name: String)

  def doStuffWithUser(user: User): Unit = { println(user) }
}

val membership = new Service
val soulmates = new Service

val membershipChris = new membership.User(123, "Chris")
val soulmatesChris = new soulmates.User(123, "Chris")

membership.doStuffWithUser(membershipChris) // OK
soulmates.doStuffWithUser(soulmatesChris) // OK

membership.doStuffWithUser(soulmatesChris) // Doesn't compile
// <console>:12: error: type mismatch;
// found   : soulmates.User
//  required: membership.User
//                membership.doStuffWithUser(soulmatesChris)
//                                           ^
```

## Hands on

Please implement the missing method in `src/test/scala/handson.scala` to make `sbt test` work.

## Further reading

* [StackOverflow answer](http://stackoverflow.com/a/1154727/110856) about abstract type members vs generics
* [Blog post](http://www.artima.com/weblogs/viewpost.jsp?thread=270195) by Bill Venners (ScalaTest lead dev) about abstract type members vs generics
* Blog post: [Typelevel hackery tricks in Scala](http://www.folone.info/blog/Typelevel-Hackery/)
* [Blog post](http://archive.today/2Qco) by Daniel Spiewak about a variant of the Cake pattern using abstract type members
