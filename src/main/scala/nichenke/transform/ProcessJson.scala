package nichenke.transform

import java.time.Instant
import java.util.UUID
import scala.util.{Try, Success}
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

import nichenke.transform.models.SimpleFlat

// TODO: pass in Raw and such classes to make this more generic.
// - Do as a base trait/class and extend for each model ?

trait JsonProcessor[A] {

  def fromString(incoming: String): JsResult[A] = {
    val json: JsValue  = Json.parse(incoming)

    /* TODO
     * log all errors
     */
    json.validate[A] match {
      case s: JsSuccess[A] => s
      case e: JsError =>
        throw JsonInvalidFileException(JsError.toJson(e).toString())
    }
  }

  /* TODO
   * Right now we'll throw an exception on the first bad record in this iterator, bailing out there.
   * Do we want to try and process the entire file ?
   */
  def parseMany(many: Iterator[String]): Array[JsResult[A]] = {
    many.map(fromString).toArray
  }

  implicit val reader: Reads[A]

  // TODO: best place for helpers? Look at play-json Reads.scala
  val readIntFromString: Reads[Int] = implicitly[Reads[String]]
    .map(x => Try(x.toInt))
    .collect (JsonValidationError(Seq("Parsing error"))){
      case Success(a) => a
    }
}


class ProcessJson extends JsonProcessor[SimpleFlat] {

  implicit val reader: Reads[SimpleFlat] = (
    // TODO add minlength for string sanity checks
    (JsPath \ "string_item").read[String] and
      ((JsPath \ "int_item").read[Int] or
       (JsPath \ "int_item").read[Int](readIntFromString)) and
      (JsPath \ "long_item").read[Long] and
      (JsPath \ "datetime_item").read[Instant] and
      (JsPath \ "uuid_item").read[UUID](new Reads.UUIDReader(true))
  )(SimpleFlat.apply _)
}


case class JsonInvalidFileException(message: String, cause: Throwable = null)
  extends RuntimeException(message: String, cause: Throwable)
