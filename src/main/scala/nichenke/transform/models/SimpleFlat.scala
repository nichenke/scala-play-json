package nichenke.transform.models

import java.time.Instant
import java.util.UUID

case class SimpleFlat (
                       a_string: String,
                       a_int: Int,
                       a_long: Long,
                       a_datetime: Instant,
                       a_uuid: UUID
                      )
