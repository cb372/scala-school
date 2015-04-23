package scalaschool

import org.scalatest._
import play.api.libs.json.Json

class jsonSpec extends FlatSpec with Matchers {
  import json._

  val processors = Seq(
    Json4sProcessor,
    PlayJsonProcessor
  )

  processors foreach { proc =>
    proc.toString should "output the expected json" in {
      val outputString = proc.process(input) 
      Json.parse(outputString) should be(expectedOutput)
    }
  }

  val expectedOutputString = """
    [{
      "id": 123,
      "title": "News article",
      "body": "The body",
      "mainImage": {
        "id": "image234",
        "filename": "234.png"
      },
      "tags": [{
        "id": "tag345",
        "name": "news"
      }, {
        "id": "tag789",
        "name": "sport"
      }]
    },
    {
      "id": 999,
      "title": "Another news article",
      "body": "The other body"
    }]
  """

  val expectedOutput = Json.parse(expectedOutputString)
}
