package scalaschool

import org.json4s.JsonAST

object json {

  val input = """
    [{
      "id": 123,
      "title": "News article",
      "body": "The body",
      "mainImage": "image234",
      "tags": [ "tag345", "tag456", "tag789" ]
    },
    {
      "id": 999,
      "title": "Another news article",
      "body": "The other body",
      "tags": [ ]
    }]
  """

  // case class for input json
  case class InputArticle(id: Int, title: String, body: String, mainImage: Option[String], tags: Seq[String])

  // data to be merged into the input json
  case class MainImage(id: String, filename: String)
  case class Tag(id: String, name: String)

  val mainImage = MainImage("image234", "234.png")
  val tags = Map(
    "tag345" -> Tag("tag345", "news"),
    "tag789" -> Tag("tag789", "sport")
  )

  // case classes for output json
  case class OutputArticle(id: Int, title: String, body: String, mainImage: Option[MainImage], tags: Seq[Tag])

  trait JsonProcessor {

    def process(input: String): String

  }

  object Json4sProcessor extends JsonProcessor {
    import org.json4s._
    import org.json4s.jackson.JsonMethods._

    import org.json4s.jackson.Serialization
    implicit val formats = Serialization.formats(NoTypeHints)

    private def serialize[A](a: A) = Extraction.decompose(a)

    private def replaceTagNamesWithTags(tagNames: List[JValue]): JsonAST.JValue = {
      val tagItems: List[Tag] = tagNames.collect {
        case JString(name) => tags.get(name)
      }.flatten
      serialize(tagItems)
    }

    def process(input: String): String = {
      // parse the string as json
      val inputJson: JValue = parse(input)

      // replace all "mainImage" fields with the image object
      val withMainImagesReplaced = inputJson.transformField {
        case ("mainImage", _) => ("mainImage", serialize(mainImage))
      }

      // replace all tag IDs with tag objects, and remove any invalid IDs
      val withTagsReplaced = withMainImagesReplaced.transformField {
        case ("tags", JArray(tagNames)) => ("tags", replaceTagNamesWithTags(tagNames))
      }

      // remove any empty tag lists
      val withEmptyTagsRemoved = withTagsReplaced.removeField {
        case ("tags", JArray(Nil)) => true
        case _ => false
      }

      // return the json as a string
      pretty(withEmptyTagsRemoved)
    }

  }

  object PlayJsonProcessor extends JsonProcessor {
    import play.api.libs.json._
    import Reads._

    implicit val mainImageFormat = Json.format[MainImage]
    implicit val tagFormat = Json.format[Tag]

    // Lots of documentation at
    // https://www.playframework.com/documentation/2.3.x/ScalaJsonTransformers

    private def replaceTagNamesWithTags(tagNames: Seq[JsValue]): JsValue = {
      val tagItems: Seq[Tag] = tagNames.collect {
        case JsString(name) => tags.get(name)
      }.flatten
      Json.toJson(tagItems)
    }

    def process(input: String): String = {
      // parse the string as json
      val inputJson: JsArray = Json.parse(input).as[JsArray]

      // transformer that replaces "mainImage" field with the image object
      val replaceMainImage =
        (__ \ 'mainImage).json.update(of[JsString].map(_ => Json.toJson(mainImage)))

      // transformer that replaces all tag IDs with tag objects, and removes any invalid IDs
      val replaceTags =
        (__ \ 'tags).json.update(of[JsArray].map{
          case JsArray(arr) => replaceTagNamesWithTags(arr)
        })

      // transformer that removes tag list if empty
      val removeEmptyTags =
        (__ \ 'tags).json.update(of[JsArray].flatMap{
          case JsArray(Nil) => (__ \ 'tags).json.prune
          case other => (__ \ 'tags).json.pickBranch
        })

      val result = inputJson.copy(value = inputJson.value.map { obj =>
        Seq(replaceMainImage, replaceTags, removeEmptyTags).foldLeft(obj){ case (o, reads) =>
          o.transform(reads) match {
            case JsSuccess(transformed, _) => transformed
            case _ => o
          }
        }
      })

      // I give up. All this json transformer nonsense is too hard.

      // return the json as a string
      Json.stringify(result)
    }
  }

  // TODO
//  object JawnProcessor extends JsonProcessor {
//
//  }

  // TODO
//  object RaptureJsonProcessor extends JsonProcessor {
//
//  }

  // TODO
//  object ArgonautProcessor extends JsonProcessor {
//
//  }

}
