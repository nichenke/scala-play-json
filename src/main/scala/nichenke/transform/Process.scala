package nichenke.transform

import scala.util.{Try, Success}
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

import nichenke.transform.models.SimpleFlat

// TODO: pass in Raw and such classes to make this more generic.
// - Do as a base trait/class and extend for each model ?


class ProcessJson {

  def parseRaw(incoming: String): JsResult[SimpleFlat] = {
    // TODO catch parse error and don't print the contents
    val json: JsValue  = Json.parse(incoming)

    /* TODO Neat, we can match on the errors but what should we be doing in both cases for a production app?
     * Things to consider:
     *  - logging all errors from each record (will see multiple validations if there are problems)
     *  - What is the best way for us to error out if we see errors? If we can't read/validate the incoming data
     *    it seems like we should drop the entire thing, perhaps with an error code to route to DLQ ?
     */
    json.validate[SimpleFlat] match {
      case s: JsSuccess[String] =>
        println("Name: " + s.get)
        s
      case e: JsError =>
        e.errors.map(println)
        println("Errors: " + JsError.toJson(e).toString())
        e
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
      (JsPath \ "long_item").read[Long]
  )(SimpleFlat.apply _)
}
