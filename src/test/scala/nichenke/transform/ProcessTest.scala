package nichenke.transform

import java.time.Instant
import org.scalatest.WordSpec
import play.api.libs.json._

import nichenke.transform.models.SimpleFlat

class ProcessTestSpec extends WordSpec {

  // TODO: figure out if this subject is correct

  "SimpleFlat" when {

    val processor = new ProcessJson

    // in epoch: 1536631877423L
    val time_text = "2018-09-11T02:11:17.423Z"
    val datetime = Instant.parse(time_text)

    val wanted_simple: SimpleFlat = SimpleFlat("this is a string",
      1234,
      4147483647L,
      datetime)

    "nominal" should {

      val json =
        """
          |{"string_item": "this is a string",
          |"int_item": 1234,
          |"long_item": 4147483647,
          |"datetime_item":"2018-09-11T02:11:17.423Z"
          |}""".stripMargin

      "process correctly" in {

        val parsed = processor.parseRaw(json)
        val expectedResult: JsResult[SimpleFlat] = JsSuccess(wanted_simple)
        assert(parsed == expectedResult)
      }
    }

    "int is a string" should {

      val json =
        """
          |{"string_item": "this is a string",
          |"int_item": "1234",
          |"long_item": 4147483647,
          |"datetime_item":"2018-09-11T02:11:17.423Z"
          |}""".stripMargin

      "process correctly" in {
        val parsed = processor.parseRaw(json)
        val expectedResult: JsResult[SimpleFlat] = JsSuccess(wanted_simple)

        assert(parsed == expectedResult)
      }
    }

    // TODO, for now we do not parse strings to long
    "long is a string" should {

      val json =
        """
          |{"string_item": "this is a string",
          |"int_item": 1234,
          |"long_item": "4147483647",
          |"datetime_item":"2018-09-11T02:11:17.423Z"
          |}""".stripMargin

      "process correctly" in {
        assertThrows[JsonInvalidFileException] {
          processor.parseRaw(json)
        }
      }
    }

    "int and long are floats" should {
      val json =
        """
          |{"string_item": "this is a string",
          |"int_item": 12.34,
          |"long_item": "4147483647.99",
          |"datetime_item":"2018-09-11T02:11:17.423Z"
          |}""".stripMargin


      "raise error" in {
        assertThrows[JsonInvalidFileException] {
          processor.parseRaw(json)
        }
      }
    }

    "datetime is an epoch" should {
      val json =
        """
          |{"string_item": "this is a string",
          |"int_item": 1234,
          |"long_item": 4147483647,
          |"datetime_item":1536631877423
          |}""".stripMargin

      "process correctly" in {

        val parsed = processor.parseRaw(json)
        val expectedResult: JsResult[SimpleFlat] = JsSuccess(wanted_simple)
        assert(parsed == expectedResult)
      }
    }

    "datetime is not in UTC" should {
      val json =
        """
          |{"string_item": "this is a string",
          |"int_item": 1234,
          |"long_item": 4147483647,
          |"datetime_item":"2018-09-10T19:11:17.423-07:00"
          |}""".stripMargin

      "process correctly" in {

        val parsed = processor.parseRaw(json)
        val expectedResult: JsResult[SimpleFlat] = JsSuccess(wanted_simple)
        assert(parsed == expectedResult)
      }
    }

  }
}
