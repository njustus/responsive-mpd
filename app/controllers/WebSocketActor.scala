package controllers

import akka.actor.{ Actor, ActorRef, actorRef2Scala }
import akka.pattern.ask
import org.bff.javampd.events.PlayerChangeEvent
import org.bff.javampd.events.PlaylistChangeEvent
import org.bff.javampd.objects.MPDSong
import models.JsMessages.{ JsPlay, JsPlaySong, JsReloadPage, JsStop }
import models.mpdbackend.{ AddSocketListener, GetActualSong, MpdConnector }
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.JsString
import play.api.Logger

class WebSocketActor(out: ActorRef) extends Actor {
  import models.mpdbackend.MpdConnector._
  private val log: Logger = Logger("websocketActor")
  private val mpdConnector = MpdConnector.getMpdActor

  mpdConnector ! AddSocketListener
  log.info("send mpdconnector request for adding as socketlistener")

  def receive = {
    case PlayerChangeEvent.Event.PLAYER_STOPPED  => out ! JsStop.toJson
    case PlayerChangeEvent.Event.PLAYER_UNPAUSED => out ! JsPlay.toJson
    case PlayerChangeEvent.Event.PLAYER_STARTED  => out ! JsPlay.toJson
    case PlayerChangeEvent.Event.PLAYER_PAUSED   =>  out ! JsStop.toJson
    case PlayerChangeEvent.Event.PLAYER_SONG_SET =>
      (mpdConnector ? GetActualSong).mapTo[MPDSong].map { song =>
        out ! JsPlaySong(song).toJson
      }
    case _:PlaylistChangeEvent.Event  => out ! JsReloadPage.toJson
    case a: Any => log.warn(s"Can't handle this msg $a")
  }
}
