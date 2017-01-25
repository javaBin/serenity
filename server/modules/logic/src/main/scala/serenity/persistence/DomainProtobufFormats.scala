package serenity.persistence

import java.time.{Instant, LocalDateTime, ZoneOffset}
import java.util.UUID

import com.google.protobuf.Message
import com.google.protobuf.timestamp.Timestamp
import serenity.cqrs.EventMeta
import serenity.persistence.protobuf.ProtobufFormat
import serenity.protobuf.Userevents
import serenity.protobuf.Userevents.BasicAuthMessage.AuthSourceEnum
import serenity.protobuf.userevents._
import serenity.protobuf.uuid.{UUID => PUUID}
import serenity.users.UserWriteProtocol._
import serenity.users.domain.Email

object DomainProtobufFormats {
  implicit def javaUuidToProtoUuid(id: UUID): Option[PUUID] =
    Some(PUUID(id.getMostSignificantBits, id.getLeastSignificantBits))

  implicit def protoUuidToJavaUuid(id: Option[PUUID]): UUID = id match {
    case Some(v) => new UUID(v.msb, v.lsb)
    case None => throw new IllegalStateException("id is required and isn't allowed to be None")
  }

  implicit def strToOptionString(str: String): Option[String] = Option(str)

  implicit def optionStrToString(opt: Option[String]): String = opt.getOrElse("")

  implicit def toEventMeta(m: EventMeta): Option[EventMetaMessage] = {
    val instant = m.created.toInstant(ZoneOffset.UTC)
    Some(EventMetaMessage(Some(Timestamp(instant.getEpochSecond, instant.getNano))))
  }

  implicit def fromEventMeta(m: Option[EventMetaMessage]): EventMeta =
    m.map(em => EventMeta(LocalDateTime.ofInstant(
      Instant.ofEpochSecond(
        em.created.get.seconds.toLong,
        em.created.get.nanos.toLong),
      ZoneOffset.UTC))).get

  implicit val basicAuthPBP = new ProtobufFormat[BasicAuthEvt] {
    override def read(proto: Message): BasicAuthEvt = proto match {
      case jm: Userevents.BasicAuthMessage =>
        val m = BasicAuthMessage.fromJavaProto(jm)
        BasicAuthEvt(
          m.id,
          m.password,
          m.salt,
          m.source.value match {
            case AuthSourceEnum.HOSPES_VALUE => HospesAuthSource
            case AuthSourceEnum.SERENITY_VALUE => SerenityAuthSource
          },
          m.meta
        )
    }

    override def write(e: BasicAuthEvt): Message = {
      BasicAuthMessage.toJavaProto(BasicAuthMessage(
        e.id,
        e.password,
        e.salt,
        e.source match {
          case HospesAuthSource => BasicAuthMessage.AuthSourceEnum.HOSPES
          case SerenityAuthSource => BasicAuthMessage.AuthSourceEnum.SERENITY
        },
        e.meta
      ))
    }
  }

  implicit val hospesUserImportPBP = new ProtobufFormat[HospesUserImportEvt] {
    override def read(proto: Message): HospesUserImportEvt = proto match {
      case jm: Userevents.HospesUserImportMessage =>
        val m = HospesUserImportMessage.fromJavaProto(jm)
        HospesUserImportEvt(
          m.id,
          m.originId.toList,
          m.emails.map(em => Email(em.address, em.validated)).toList,
          m.firstName,
          m.lastName,
          m.address,
          m.phoneNumber,
          m.meta
        )
    }

    override def write(e: HospesUserImportEvt): Message =
      HospesUserImportMessage.toJavaProto(HospesUserImportMessage(
        e.id,
        e.originId,
        e.email.map(em => EmailMessage(em.address, em.validated)),
        e.firstName,
        e.lastName,
        e.address,
        e.phoneNumber,
        e.meta
      ))
  }

  implicit val userRegisteredPBP = new ProtobufFormat[UserRegisteredEvt] {
    override def read(proto: Message): UserRegisteredEvt = proto match {
      case jm: Userevents.UserRegisteredMessage =>
        val m = UserRegisteredMessage.fromJavaProto(jm)
        UserRegisteredEvt(
          m.id,
          m.email.map(em => Email(em.address, em.validated)).get.address,
          m.firstName,
          m.lastName,
          m.meta
        )
    }

    override def write(e: UserRegisteredEvt): Message =
      UserRegisteredMessage.toJavaProto(UserRegisteredMessage(
        e.id,
        Some(EmailMessage(e.email, true)),
        e.firstName,
        e.lastName,
        e.meta
      ))
  }

}
