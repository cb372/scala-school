import java.io._


class Parent
class Child extends Parent

class Container[A](value: A)


object variance extends App {

  def doSomething(container: Container[Parent]) = println("hello")

  val container = new Container(new Child)

  doSomething(container)

}
