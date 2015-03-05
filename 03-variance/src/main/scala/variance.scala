import java.io._

class Parent
class Child extends Parent

class Container[A](value: A)

object variance extends App {

  def doSomething(container: Container[Parent]) = println("hello")

  val container = new Container(new Child)

  // doesn't compile
  //doSomething(container)

  // doesn't compile
  //class Mutable[+A] {
    //var foo: A = _
  //}

  
  // doesn't compile
  //class MissingTypeBound[+A] {
    //def foo(a: A): Unit = {}
  //}

  class WithTypeBound[+A] {
    def foo[B >: A](b: B): Unit = {}
  }

}
