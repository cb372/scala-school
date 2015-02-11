# Type classes

## What's a type class

Type classes are somewhat similar to Java interfaces or Scala traits, in that they specify a set of methods that a type must implement in order to be a member of that type class.

For example, you might have a type class called `Compat[A]`, which specifies that classes must define an `isCompatible` method in order to be a member of `Compat`:

```scala
/** The class of types that have some notion of "compatibility" */
trait Compat[A] { 
  /** Checks whether `a1` and `a2` are compatible with each other */
  def isCompatible(a1: A, a2: A): Boolean
}
```

You could use this type class (or rather, evidence of a given type's membership of it) in a function like so:

```scala
def findCompatiblePairs[A](pairs: Seq[(A, A)])(implicit evidence: Compat[A]): Seq[(A, A)] = {
  pairs.filter { case (a1, a2) => evidence.isCompatible(a1, a2) }
}
```

Note that the function accepts any type `A`, *as long as that type is a member of the Compat type class*. There is no need for the type to inherit a particular trait.

Note: In Scala, there is no special syntax or keyword needed to create a type class. It's just a design pattern. The type class itself is usually written as a trait, like `Compat` shown above.

To continue the example, let's say we have a `Widget` type:

```scala
sealed trait Widget
case object RoundPeg extends Widget
case object RoundHole extends Widget
case object SquarePeg extends Widget
case object SquareHole extends Widget
```

If we try to pass some `Widget`s to our function, the compiler will complain that it cannot find any evidence that `Widget` is a member of the `Compat` type class:

```
[error] /Users/cbirchall/code/scala-school/01-type-classes/src/main/scala/typeclasses.scala:18: could not find implicit value for parameter evidence: typeclasses.Compat[Product with Serializable with typeclasses.Widget]
[error]   findCompatiblePairs(Seq(RoundPeg -> RoundHole, SquarePeg -> RoundHole))
[error]                      ^
```

We need to provide evidence that `Widget` is a member of the `Compat` type class. In other words, we need to create a `Compat[Widget]`, and put it in implicit scope where the compiler can find it.

```scala
implicit val widgetCompat = new Compat[Widget] {
  def isCompatible(w1: Widget, w2: Widget) = (w1, w2) match {
    case (RoundPeg, RoundHole) => true
    case (RoundHole, RoundPeg) => true
    case (SquarePeg, SquareHole) => true
    case (SquareHole, SquarePeg) => true
    case _ => false
  }
}
```

With this in place, we can pass a list of `Widget` pairs to our method with no problem. It will find our evidence and use it to check the compabitibility of widgets.

### Common use cases of type classes

* Show, Read (Haskell, Scalaz) - Show prints a thing as a String, Read reads a thing from a String
* Ordering, Equality - used in the std lib
* Serialization codecs, JSON formats, etc - play-json is a famous example

## What's the point of type classes?

So why would we bother doing this? Why not just create a `Compat` trait and make `Widget` extend it?

One reason for using type classes rather than traits is that they are cool and Haskell-y and they make your APIs more sexy.

A more important reason is that they allow **retrospective extension**. TODO explain this.

### Downside of type classes

One argument against excessive use of type classes is that they increase the amount of implicit search that the compiler has to do. This is the number one reason for slow Scala compilation.

## Refresher: implicits

Implicits can be used for 2 different purposes: *implicit parameters* and *implicit conversions*.

Implicit conversion means automagically converting a String to an Int, or whatever. While useful in some cases (e.g. `10.seconds`), overuse of this feature can lead to very unreadable code. Implicit conversion can also be used to generate *extension methods*, often using implicit classes.

When dealing with type classes, we use the other kind of implicits: implicit parameters. Whenever we want to make use of a type class in a function, we need to pass evidence of type class membership, and we usually do this with an implicit parameter:

```scala
def findCompatiblePairs[A](pairs: Seq[(A, A)])(implicit evidence: Compat[A]): Seq[(A, A)] = {
  pairs.filter { case (a1, a2) => evidence.isCompatible(a1, a2) }
}
```

### Context bounds

Typing this `implicit evidence: ...` every time gets pretty tiresome, so there is a piece of syntactic shorthand available:

```scala
def findCompatiblePairs[A : Compat](pairs: Seq[(A, A)]): Seq[(A, A)] = {
  // ...
}
```

This gets rewritten by the compiler, so it behaves exactly the same as the signature above.

The `A : Compat` part is called a Context Bound. It means that there is an instance of `Compat[A]` in implicit scope.

Inside the function, when you want to use the evidence in order to get at the type class, you can use the `implicitly` function:

```scala
def findCompatiblePairs[A : Compat](pairs: Seq[(A, A)]): Seq[(A, A)] = {
  pairs.filter { case (a1, a2) => implicitly[Compat[A]].isCompatible(a1, a2) }
}
```

By the way, you might also sometimes see this, although it's quite rare in the wild:

```scala
def f[A <% B](a : A) = { ... }
```

This is called a View Bound, and it means that the function accepts any type A that can be implicitly converted to a B.

### Implicit search order

### Implicit `val` vs `def`

## Hands on

