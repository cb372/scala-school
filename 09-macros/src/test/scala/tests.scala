import org.scalatest._
import scalaschool.Macros._

class Spec extends FlatSpec with Matchers {

  "#doNothing" should "work" in {
    val before = 123
    val after = doNothing(before)
    after should equal(before)
  }

  "#takesATree" should "work" in {
    val before = 123
    val after = takesATree(before)
    after should equal(before)
  }

  //failToCompile

}
