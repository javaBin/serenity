package repository.view

import java.util.UUID

import org.scalatest.Inside
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import serenity.UtcDateTime
import serenity.users.domain._

class UserRepositorySpec extends PlaySpec with GuiceOneAppPerSuite with ScalaFutures with Inside {

  val repo = app.injector.instanceOf(classOf[UserRepository])

  def uniqueEmail = Email(s"${UUID.randomUUID()}@java.no", validated = true)

  "user repository" should {
    "insert basic user without fault" in {
      val usr = User(
        uuid = UUID.randomUUID(),
        mainEmail = uniqueEmail,
        createdDate = UtcDateTime.nowUTC()
      )
      repo.saveUser(usr).futureValue
    }

    "insert and get user" in {
      val usr = User(
        uuid = UUID.randomUUID(),
        firstName = Some("first"),
        lastName = Some("last"),
        mainEmail = uniqueEmail,
        createdDate = UtcDateTime.nowUTC(),
        emails = Seq(uniqueEmail.copy(validated = false)),
        phone = Some("12300123"),
        address = Some("teknologihuset, 0000 oslo"),
        roles = Set(AdminRole),
        memberships = Set(
          Membership(
            from = UtcDateTime.nowUTC().minusDays(20).toLocalDate,
            to = UtcDateTime.nowUTC().plusDays(30).toLocalDate,
            issuer = MembershipIssuer.JavaBin,
            eventbriteMeta = None
          ),
          Membership(
            from = UtcDateTime.nowUTC().minusDays(20).toLocalDate,
            to = UtcDateTime.nowUTC().plusDays(30).toLocalDate,
            issuer = MembershipIssuer.JavaZone,
            eventbriteMeta = Some(EventbriteMeta("123", "234", "354"))
          )
        )
      )

      repo.saveUser(usr).futureValue
      val res = repo.fetchUserById(UserId(usr.uuid)).futureValue

      inside(res) { case Some(u) => matchUser(u, usr) }
    }

    "find credentials by email" in {
      val usr = User(
        uuid = UUID.randomUUID(),
        mainEmail = uniqueEmail,
        createdDate = UtcDateTime.nowUTC()
      )
      val auth = HospesAuth("pwd", Some("salt"))

      repo.saveUser(usr).futureValue
      repo.saveCredentials(UserId(usr.uuid), auth).futureValue

      val res = repo.credentialsByEmail(usr.mainEmail.address).futureValue

      res mustBe Some(auth)
    }

    def matchUser(actual: User, expected: User) = {
      actual.uuid mustBe expected.uuid
      actual.firstName mustBe expected.firstName
      actual.lastName mustBe expected.lastName
      actual.phone mustBe expected.phone
      actual.createdDate mustBe expected.createdDate
      actual.address mustBe expected.address
      actual.mainEmail mustBe expected.mainEmail
      actual.roles must contain theSameElementsAs expected.roles
      actual.memberships must contain theSameElementsAs expected.memberships
      actual.emails must contain theSameElementsAs expected.emails
    }
  }
}
