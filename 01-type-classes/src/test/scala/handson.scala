import org.scalatest._

/** A type class to signify that a type can be serialized to JSON */
trait JSONable[A] {

  def toJson(a: A): String

}

/* Our model classes */
case class Person(name: String, age: Int)
case class Team(people: Seq[Person])

object JSONables {

  // Sorry, should have put this method here from the start, as it's useful to re-use it in the implicits
  def serialize[A : JSONable](a: A): String = implicitly[JSONable[A]].toJson(a)

  implicit val stringJSONable = new JSONable[String]{ def toJson(a: String) = '"' + a + '"' }

  implicit val intJSONable = new JSONable[Int]{ def toJson(a: Int) = a.toString }

  implicit val personJSONable = new JSONable[Person]{ 
    def toJson(p: Person) = 
      s"""{"name":${serialize(p.name)},"age":${serialize(p.age)}}"""
  }

  implicit def seqJSONable[A : JSONable] = new JSONable[Seq[A]] {
    def toJson(xs: Seq[A]) = 
      xs.map(x => serialize(x)).mkString("[", ",", "]")
  }

  implicit val teamJSONable = new JSONable[Team] {
    def toJson(team: Team) = s"""{"people":${serialize(team.people)}}"""
  }

}

class HandsOnSpec extends FlatSpec with Matchers {
  // Import to get the required JSONables into implicit scope
  import JSONables._

  behavior of "JSON serialization"

  it should "work for a String" in {
    serialize("hello") should be("\"hello\"")
  }

  it should "work for an Int" in {
    serialize(123) should be("123")
  }

  it should "work for a Person" in {
    serialize(Person("Chris", 30)) should be("""{"name":"Chris","age":30}""")
  }

  it should "work for a Seq[Int]" in {
    serialize(Seq(1, 2, 3)) should be("[1,2,3]")
  }

  it should "work for a Team" in {
    serialize(Team(Seq(Person("Chris", 30), Person("Dave", 99)))) should be(
      """{"people":[{"name":"Chris","age":30},{"name":"Dave","age":99}]}""")
  }

}
