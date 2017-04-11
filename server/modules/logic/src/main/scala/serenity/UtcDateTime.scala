package serenity

import java.time._

object UtcDateTime {

  def nowUTC(): LocalDateTime = time.dateTimeNow()

}

object time {

  def dateTimeNow(): LocalDateTime =
    LocalDateTime.now(Clock.systemUTC())

  def toUtcMillis(ld: LocalDate) =
    ld.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli

  def instanceToUtcLocalDate(ins: Instant): LocalDate =
    LocalDateTime.ofEpochSecond(ins.getEpochSecond, ins.getNano, ZoneOffset.UTC).toLocalDate

  def instanceToUtcLocalDateTime(ins: Instant): LocalDateTime =
    LocalDateTime.ofEpochSecond(ins.getEpochSecond, ins.getNano, ZoneOffset.UTC)

}