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

Mutable data types do not play well with covariance and contravariance. Stealing the explanaition from Wikipedia,

>Consider the array type constructor: from the type Animal we can make the type Animal[] ("array of animals"). Should we treat this as
>
>* Covariant: a Cat[] is an Animal[]
>* Contravariant: an Animal[] is a Cat[]
>* or neither (invariant)?
>
>If we wish to avoid type errors, and the array supports both reading and writing elements, then only the third choice is safe. Clearly, not every Animal[] can be treated as if it were a Cat[], since a client reading from the array will expect a Cat, but an Animal[] may contain e.g. a Dog. So the contravariant rule is not safe.
>
>Conversely, a Cat[] can not be treated as an Animal[]. It should always be possible to put a Dog into an Animal[]. With covariant arrays this can not be guaranteed to be safe, since the backing store might actually be an array of cats. So the covariant rule is also not safe—the array constructor should be invariant. Note that this is only an issue for mutable arrays; the covariant rule is safe for immutable (read-only) arrays.
>
>This illustrates a general phenomenon. Read-only data types (sources) can be covariant; write-only data types (sinks) can be contravariant. Mutable data types which act as both sources and sinks should be invariant.

So, classes with covariance must be immutable. If they have any mutable fields with the covariant type, really weird things can happen. To avoid this, the compiler checks that your class doesn't include any invalid `var`s.

For example, if you write this:

```scala
class Mutable[+A] {
  var foo: A = _
}
```

then the compiler will complain with `covariant type A occurs in contravariant position in type A of value foo_=`.

You have the same problem if you don't set type bounds on methods correctly. For example

```
class MissingTypeBound[+A] {
  def foo(a: A): Unit = {}
}
```

doesn't compile. This is in order to preserve the Liskov Substitution Principle: if S is a subtype of T, then objects of type T may be replaced with objects of type S (i.e., objects of type S may substitute objects of type T) without altering any of the desirable properties.

If we add a super-type bound to the method

```
class WithTypeBound[+A] {
  def foo[B >: A](b: B): Unit = {}
}
```

then it will compile fine.

## Hands on

As usual, please make the tests pass.
