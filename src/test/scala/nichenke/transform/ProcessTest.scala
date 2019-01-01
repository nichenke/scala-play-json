package nichenke.transform

import java.time.Instant
import java.util.UUID
import org.scalatest.WordSpec
import play.api.libs.json._

import nichenke.transform.models.SimpleFlat

class ProcessTestSpec extends WordSpec {

  // TODO: figure out if this subject is correct

  "SimpleFlat" when {

    val processor = new ProcessJson

    val string_val = "this is a string"
    val int_val = 1234
    val long_val = 4147483647L

    // in epoch: 1536631877423L
    val time_val = "2018-09-11T02:11:17.423Z"
    val datetime = Instant.parse(time_val)

    val uuid_val = "cf85a8da-d051-4f46-97bd-7db0fdc7c3de"
    val uuid = UUID.fromString(uuid_val)

    val wanted_simple: SimpleFlat = SimpleFlat(string_val, int_val, long_val, datetime, uuid)

    "nominal" should {

      val json =
        s"""
          |{"string_item": "$string_val",
          |"int_item": $int_val,
          |"long_item": $long_val,
          |"datetime_item": "$time_val",
          |"uuid_item": "$uuid_val"
          |}""".stripMargin

      "process correctly" in {

        val parsed = processor.parseRaw(json)
        val expectedResult: JsResult[SimpleFlat] = JsSuccess(wanted_simple)
        assert(parsed == expectedResult)
      }
    }

    "int is a string" should {

      val json =
        s"""
           |{"string_item": "$string_val",
           |"int_item": "${int_val.toString}",
           |"long_item": $long_val,
           |"datetime_item": "$time_val",
           |"uuid_item": "$uuid_val"
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
        s"""
           |{"string_item": "$string_val",
           |"int_item": $int_val,
           |"long_item": "${long_val.toString}",
           |"datetime_item": "$time_val",
           |"uuid_item": "$uuid_val"
           |}""".stripMargin

      "process correctly" in {
        assertThrows[JsonInvalidFileException] {
          processor.parseRaw(json)
        }
      }
    }

    "int and long are floats" should {
      val json =
        s"""
           |{"string_item": "$string_val",
           |"int_item": 12.34,
           |"long_item": "4147483647.99",
           |"datetime_item": "$time_val",
           |"uuid_item": "$uuid_val"
           |}""".stripMargin

      "raise error" in {
        assertThrows[JsonInvalidFileException] {
          processor.parseRaw(json)
        }
      }
    }

    "datetime is an epoch" should {
      val json =
        s"""
           |{"string_item": "$string_val",
           |"int_item": $int_val,
           |"long_item": $long_val,
           |"datetime_item":1536631877423,
           |"uuid_item": "$uuid_val"
           |}""".stripMargin

      "process correctly" in {

        val parsed = processor.parseRaw(json)
        val expectedResult: JsResult[SimpleFlat] = JsSuccess(wanted_simple)
        assert(parsed == expectedResult)
      }
    }

    "datetime is not in UTC" should {
      val json =
        s"""
           |{"string_item": "$string_val",
           |"int_item": $int_val,
           |"long_item": $long_val,
           |"datetime_item":"2018-09-10T19:11:17.423-07:00",
           |"uuid_item": "$uuid_val"
           |}""".stripMargin

      "process correctly" in {

        val parsed = processor.parseRaw(json)
        val expectedResult: JsResult[SimpleFlat] = JsSuccess(wanted_simple)
        assert(parsed == expectedResult)
      }
    }

    "malformed uuid" should {
      val json =
        s"""
           |{"string_item": "$string_val",
           |"int_item": $int_val,
           |"long_item": $long_val,
           |"datetime_item":"2018-09-10T19:11:17.423-07:00",
           |"uuid_item": "deadbeef"
           |}""".stripMargin

      "raise error" in {
        assertThrows[JsonInvalidFileException] {
          processor.parseRaw(json)
        }
      }
    }
  }
}
