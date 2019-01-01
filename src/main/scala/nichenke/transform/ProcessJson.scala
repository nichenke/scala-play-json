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


class ProcessJson {

  def parseRaw(incoming: String): JsResult[SimpleFlat] = {
    val json: JsValue  = Json.parse(incoming)

    /* TODO
     * log all errors
     */
    json.validate[SimpleFlat] match {
      case s: JsSuccess[SimpleFlat] => s
      case e: JsError =>
        throw JsonInvalidFileException(JsError.toJson(e).toString())
    }
  }


  val readIntFromString: Reads[Int] = implicitly[Reads[String]]
    .map(x => Try(x.toInt))
    .collect (JsonValidationError(Seq("Parsing error"))){
      case Success(a) => a
    }


  implicit val simpleReads: Reads[SimpleFlat] = (
    // TODO add minlength for string sanity checks
    (JsPath \ "string_item").read[String] and
      ((JsPath \ "int_item").read[Int] or
       (JsPath \ "int_item").read[Int](readIntFromString)) and
      (JsPath \ "long_item").read[Long] and
      (JsPath \ "datetime_item").read[Instant] and
      (JsPath \ "uuid_item").read[UUID]
  )(SimpleFlat.apply _)
}


case class JsonInvalidFileException(message: String, cause: Throwable = null)
  extends RuntimeException(message: String, cause: Throwable)
