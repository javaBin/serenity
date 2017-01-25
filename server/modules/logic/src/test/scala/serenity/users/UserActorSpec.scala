package serenity.users

import java.util.UUID

import akka.actor.ActorRef
import akka.actor.Status.{Failure, Success}
import serenity.akka.{AkkaConfig, AkkaSuite}
import serenity.users.UserReadProtocol._
import serenity.users.UserWriteProtocol.{HospesImportCmd, HospesUser, ValidationFailed}
import serenity.users.domain.{Email, UserId}

class UserActorSpec extends AkkaSuite("UserActorSpec", AkkaConfig.inMemoryPersistence()) {
  val hospesUser: HospesUser = HospesUser(
    List(),
    List(Email("example@java.no", validated = true)),
    None, None, None, None,
    "pw", "salt", Set()
  )

  describe("Persist and Query") {

    it("should handle hospes imports") {
      val userActor: ActorRef = system.actorOf(UserActor(UUID.randomUUID()))
      userActor ! HospesImportCmd(hospesUser)

      expectMsgClass(classOf[Success])
    }

    it("should handle fail when importing same user") {
      val userActor: ActorRef = system.actorOf(UserActor(UUID.randomUUID()))

      userActor ! HospesImportCmd(hospesUser)
      expectMsgClass(classOf[Success])

      userActor ! HospesImportCmd(hospesUser)

      expectMsg(Failure(ValidationFailed("User exist")))
    }

    it("should handle query for user") {
      val userId: UserId = UUID.randomUUID()
      val userActor: ActorRef = system.actorOf(UserActor(userId))
      userActor ! HospesImportCmd(hospesUser)
      expectMsgClass(classOf[Success])

      userActor ! GetUser(userId)
      expectMsgAnyClassOf(classOf[UserResponse])
    }

    it("should read up state after shutdown") {
      val userId: UserId = UUID.randomUUID()
      val originUserActor: ActorRef = system.actorOf(UserActor(userId))
      originUserActor ! HospesImportCmd(hospesUser)
      expectMsgClass(classOf[Success])

      system.stop(originUserActor)

      val restoredUserActor: ActorRef = system.actorOf(UserActor(userId))

      restoredUserActor ! GetUser(userId)
      expectMsgAnyClassOf(classOf[UserResponse])
    }

    it("should return BasicAuth when credentials exists") {
      val plainPwd = "myS3cr3tPwd"
      val userActor: ActorRef = system.actorOf(UserActor(UUID.randomUUID()))
      val usr = hospesUser
      userActor ! HospesImportCmd(usr)

      expectMsgClass(classOf[Success])

      userActor ! GetUserCredentials(usr.email.head.address)

      expectMsgClass(classOf[UserCredentialsResponse])
    }

    it("should return CredentialsNotFound when credentials doesn't exists ") {
      val plainPwd = "myS3cr3tPwd"
      val userActor: ActorRef = system.actorOf(UserActor(UUID.randomUUID()))
      val usr = hospesUser
      userActor ! HospesImportCmd(usr)

      expectMsgClass(classOf[Success])

      userActor ! GetUserCredentials("some@random.user")

      expectMsg(CredentialsNotFound)
    }
  }

}