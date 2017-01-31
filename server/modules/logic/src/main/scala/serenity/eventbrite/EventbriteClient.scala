package serenity.eventbrite

import javax.inject.{Inject, Named}

import play.api.libs.ws.WSClient

import scala.concurrent.{ExecutionContext, Future}

class EventbriteClient @Inject() (
    ws: WSClient,
    @Named("eventbrite.token") token: String)
    (implicit ec: ExecutionContext) extends EventbriteApiDtoJson{

  def attendee(url: String): Future[Attendee] = {
    ws.url(url)
        .withQueryString(("token", token))
        .get().map(r => {
      val jsonResponse = r.json
      Attendee(
        (jsonResponse \ "profile").as[Profile],
        jsonResponse.as[AttendeeMeta]
      )
    })
  }
}