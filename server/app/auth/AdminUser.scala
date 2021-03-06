package auth

import javax.inject.{Inject, Named}

import com.mohiva.play.silhouette.api.util.{PasswordHasher, PasswordInfo}
import models.time
import models.user.Roles.AdminRole
import models.user.{Email, User, UserId}
import play.api.Configuration

class AdminUser @Inject()(
    @Named("bcryptHasher") hasher: PasswordHasher,
    configuration: Configuration
) {

  private val basePath = "serenity.adminuser"

  lazy val user: User = User(
    userId = UserId.generate(),
    mainEmail = Email(strFromCfg("email"), validated = true),
    createdDate = time.dateTimeNow(),
    firstName = Some("admin"),
    lastName = Some("javaBin"),
    roles = Set(AdminRole)
  )

  lazy val auth: PasswordInfo = hasher.hash(strFromCfg("password"))

  def strFromCfg(path: String): String =
    configuration.underlying.getString(s"$basePath.$path")

}
