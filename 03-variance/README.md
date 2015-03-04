# Variance

When using Scala libraries, you sometimes see generic classes whose type signatures include `+` and `-`. For example, `scala.collection.immutable.List` looks like this:

```scala
sealed abstract class List[+A] extends AbstractSeq[A]
                                  with LinearSeq[A]
                                  with Product
                                  with GenericTraversableTemplate[A, List]
                                  with LinearSeqOptimized[A, List[A]]
                                  with Serializable
```

The `+` means that `List` is **covariant** in the type of its elements.

and `scala.Function1` looks like this:

```scala
trait Function1[-T1, +R] extends AnyRef
```

This means that `Function1` IS **covariant** in the type of its argument, and **contravariant** in the type of its result.

## Covariance and contravariance

**Covariance** means that subtype relationships are preserved. If A is a subclass of B, then `List[A]` is a subtype of `List[B]`.

For example, Java's `InputStream` is a subclass of `Closeable`, so you can pass a `List[InputStream]` to any function that wants a `List[Closeable]`.

**Contravariance** is the opposite: subtype relationships are reversed. So, if A is a subclass of B, then `Function1[B, String]` is a subclass of `Function1[A, String]`.

For example, if you have a function:

```scala
def processResource(f: InputStream => String): Unit = { ... }
```

then you can pass it a `Closeable => String`.

## Invariance

If you don't add either a `+` or a `-` to the type signature, then you won't get any subtyping relationship. For example,

```scala
class Parent
class Child extends Parent

class Container[A](value: A)
val cChild = new Container(new Child)

def doSomething(container: Container[Parent]) = println("hello")
doSomething(cChild)
```

produces the following compilation error:

```
[error] /Users/cbirchall/code/scala-school/03-variance/src/main/scala/variance.scala:16: type mismatch;
[error]  found   : Container[Child]
[error]  required: Container[Parent]
[error] Note: Child <: Parent, but class Container is invariant in type A.
[error] You may wish to define A as +A instead. (SLS 4.5)
[error]   doSomething(container)
[error]               ^
```

## Immutability

Classes with covariance must be immutable. If they have any mutable fields with the covariant type, really weird things can happen. To avoid this, the compiler checks that your class doesn't include any invalid `var`s.

TODO example

## Comparison with Java

TODO
