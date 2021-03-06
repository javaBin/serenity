package repositories.view

import java.sql.{Timestamp => JSqlTimestamp}
import java.time.{LocalDate, LocalDateTime, ZoneOffset}
import java.util.UUID

import models.time
import models.user.Memberships.MembershipIssuer
import models.user.Roles.Role
import models.user.UserId
import play.api.db.slick.HasDatabaseConfig
import slick.jdbc.JdbcProfile

trait ColumnTypeMappers { self: HasDatabaseConfig[JdbcProfile] =>
  import profile.api._

  implicit val uuidMapper: BaseColumnType[UUID] =
    MappedColumnType.base[UUID, String](
      uuid => uuid.toString,
      str => UUID.fromString(str)
    )

  implicit val userIdMapper: BaseColumnType[UserId] =
    MappedColumnType.base[UserId, String](
      uuid => uuid.asString,
      str => UserId.unsafeFromString(str)
    )

  implicit val datetimeMapper: BaseColumnType[LocalDateTime] =
    MappedColumnType.base[LocalDateTime, JSqlTimestamp](
      dt => new JSqlTimestamp(dt.toInstant(ZoneOffset.UTC).toEpochMilli),
      ts => time.instanceToUtcLocalDateTime(ts.toInstant)
    )

  implicit val dateMapper: BaseColumnType[LocalDate] =
    MappedColumnType.base[LocalDate, JSqlTimestamp](
      lt => new JSqlTimestamp(time.toUtcMillis(lt)),
      ts => time.instanceToUtcLocalDate(ts.toInstant)
    )

  implicit val issuerMapper: BaseColumnType[MembershipIssuer.Issuer] =
    MappedColumnType.base[MembershipIssuer.Issuer, Int](
      mi => MembershipIssuer.toInt(mi),
      i => MembershipIssuer.unsafeFromInt(i)
    )

  implicit val roleMapper: BaseColumnType[Role] =
    MappedColumnType.base[Role, String](
      role => role.name,
      str => Role.apply(str)
    )

}
