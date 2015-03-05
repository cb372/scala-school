import org.scalatest._

class Animal
class Cat extends Animal

class Covariant[+A](value: A)
class Contravariant[-A](value: A)

class HandsOnSpec extends FlatSpec with Matchers {

  def doSomethingWithCovAnimal(cov: Covariant[Animal]): Unit = println("hello animal")
  def doSomethingWithCovCat(cov: Covariant[Cat]): Unit = println("hello cat")

  "Covariant" should "be covariant" in {
    """
      val cov: Covariant[Cat] = new Covariant(new Cat)
      doSomethingWithCovAnimal(cov)
    """ should compile
  }

  "Covariant" should "not be contravariant" in {
    """
      val cov: Covariant[Animal] = new Covariant(new Animal)
      doSomethingWithCovCat(cov)
    """ shouldNot compile
  }

  def doSomethingWithContraAnimal(contra: Contravariant[Animal]): Unit = println("hello animal")
  def doSomethingWithContraCat(contra: Contravariant[Cat]): Unit = println("hello cat")

  "Contravariant" should "be contravariant" in {
    """
      val contra: Contravariant[Animal] = new Contravariant(new Animal)
      doSomethingWithContraCat(contra)
    """ should compile
  }

  "Contravariant" should "not be covariant" in {
    """
      val contra: Contravariant[Cat] = new Contravariant(new Cat)
      doSomethingWithContraAnimal(contra)
    """ shouldNot compile
  }

}
