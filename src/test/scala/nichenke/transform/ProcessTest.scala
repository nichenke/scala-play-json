package nichenke.transform

import org.scalatest.WordSpec
import play.api.libs.json._
import nichenke.transform.models.SimpleFlat

class ProcessTestSpec extends WordSpec {

  // TODO: figure out if this subject is correct

  "SimpleFlat" when {

    val processor = new ProcessJson

    "nominal" should {

      val json = """{"string_item": "this is a string", "int_item": 1234, "long_item": 4147483647}"""

      "process correctly" in {

        val parsed = processor.parseRaw(json)
        val wanted_simple: SimpleFlat = SimpleFlat("this is a string",
          1234,
          4147483647L)
        val expectedResult: JsResult[SimpleFlat] = JsSuccess(wanted_simple)

        assert(parsed == expectedResult)
      }

      "int is a string" should {

        val json = """{"string_item": "this is a string", "int_item": "1234", "long_item": 4147483647}"""

        "process correctly" in {
          val parsed = processor.parseRaw(json)
          val wanted_simple: SimpleFlat = SimpleFlat("this is a string",
            1234,
            4147483647L)
          val expectedResult: JsResult[SimpleFlat] = JsSuccess(wanted_simple)

          assert(parsed == expectedResult)
        }
      }

      "int and long are floats" should {
        val json = """{"string_item": "this is a string", "int_item": 1234.455, "long_item": 4.147483647}"""

        // TODO we don't raise an error, so need to do error count checks or something
        // -- starting to get into a proper API space here
        "raise error" in {
          processor.parseRaw(json)
        }
      }

      "int and long are string floats" should {
        val json = """{"string_item": "this is a string", "int_item": "1234.455", "long_item": "4.147483647"}"""

        // TODO we don't raise an error, so need to do error count checks or something
        // -- starting to get into a proper API space here
        "raise error" in {
          processor.parseRaw(json)
        }
      }

    }
  }
}
