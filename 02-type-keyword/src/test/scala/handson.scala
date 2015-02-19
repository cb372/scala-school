import org.scalatest._
import org.json4s._
import org.json4s.jackson.JsonMethods._
import scala.xml._

trait Traverser {
  type Node

  def getChildren(parent: Node): Seq[Node]

  def countElements(root: Node): Int = {
    ??? // TODO implement
  }
}

object JSONTraverser extends Traverser {
  type Node = JValue

  def getChildren(parent: JValue): Seq[JValue] = parent match {
    case JObject(fields) => fields.map(_._2)
    case _ => Nil
  }
}

object XMLTraverser extends Traverser {
  type Node = scala.xml.Node

  def getChildren(parent: Node): Seq[Node] = {
    parent.nonEmptyChildren
  }
}

class HandsOnSpec extends FlatSpec with Matchers {

  "Counting JSON nodes" should "work" in {
    val json = parse("""{ "a": { "b": true, "c": false }, "d": 123 }""")
    JSONTraverser.countElements(json) should be(5)
  }

  "Counting XML nodes" should "work" in {
    val xml = XML.loadString("""<root><a><b /><c /></a><d /></root>""")
    XMLTraverser.countElements(xml) should be(5)
  }


}
