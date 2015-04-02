import org.scalatest._

class HandsOnSpec extends FlatSpec with Matchers {

  trait Messiah { def describe: String }
  class Jesus extends Messiah { def describe = "Jesus" }
  trait LastSupper extends Messiah { 
    abstract override def describe = super.describe + " who had a last supper" 
  }
  trait Crucifixion extends Messiah { 
    abstract override def describe = super.describe + " and was crucified" 
  }
  trait Death extends Messiah { 
    abstract override def describe = super.describe + " and lay dead in a tomb" 
  }
  trait Resurrection extends Messiah { 
    abstract override def describe = super.describe + " and was resurrected" 
  }

  val messiah = new Jesus

  "the Messiah" should "work as expected" in {
    messiah.describe should be("Jesus who had a last supper and was crucified and lay dead in a tomb and was resurrected")
  }

}
