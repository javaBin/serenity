package serenity.users

import serenity.cqrs.{Cmd, EventMeta, Evt}
import serenity.users.domain.{Email, UserId}

object UserWriteProtocol {

  case class CreateUserCmd(
      email: String,
      firstName: String,
      lastName: String
  ) extends Cmd

  case class UserRegisteredEvt(
      id: UserId,
      email: String,
      firstName: String,
      lastName: String,
      meta: EventMeta
  ) extends Evt

  case class ValidationFailed(msg: String) extends Exception(msg)

  case class HospesUser(
      originId: List[Int],
      email: List[Email],
      firstname: Option[String],
      lastname: Option[String],
      address: Option[String],
      phonenumber: Option[String],
      password_pw: String,
      password_slt: String,
      memberships: Set[HospesMembership])

  case class HospesMembership(
      id: Int,
      year: Int)

  case class HospesImportCmd(user: HospesUser) extends Cmd

  case class BasicAuthEvt(
      id: UserId,
      password: String,
      salt: String, // todo make optional
      source: AuthSource = SerenityAuthSource,
      meta: EventMeta = EventMeta()
  ) extends Evt

  sealed trait AuthSource

  case object SerenityAuthSource extends AuthSource

  case object HospesAuthSource extends AuthSource

  case class HospesUserImportEvt(
      id: UserId,
      originId: List[Int],
      email: List[Email],
      firstName: Option[String],
      lastName: Option[String],
      address: Option[String],
      phoneNumber: Option[String],
      meta: EventMeta = EventMeta()
  ) extends Evt

  def toHospesUserEvent(id: UserId, hospesUser: HospesUser): HospesUserImportEvt = {
    HospesUserImportEvt(
      id,
      hospesUser.originId,
      hospesUser.email,
      hospesUser.firstname,
      hospesUser.lastname,
      hospesUser.address,
      hospesUser.phonenumber
    )
  }

}

