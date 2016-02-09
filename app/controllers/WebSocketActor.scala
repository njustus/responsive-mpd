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
import org.bff.javampd.events.PlayerBasicChangeEvent
import org.bff.javampd.events.PlaylistBasicChangeEvent
import models.mpdbackend.RemoveSocketListener

class WebSocketActor(out: ActorRef) extends Actor {
  import models.mpdbackend.MpdConnector._
  private val log: Logger = Logger("websocketActor")
  private val mpdConnector = MpdConnector.getMpdActor

  mpdConnector ! AddSocketListener
  log.info("send mpdconnector request for adding as socketlistener")

  //handle closing of socket: logout from listeners
  override def postStop():Unit = {
    mpdConnector ! RemoveSocketListener
    log.info("logged out from listeners")
  }

  def receive = {
    case PlayerBasicChangeEvent.Status.PLAYER_STOPPED  => out ! JsStop.toJson
    case PlayerBasicChangeEvent.Status.PLAYER_UNPAUSED => out ! JsPlay.toJson
    case PlayerBasicChangeEvent.Status.PLAYER_STARTED  => out ! JsPlay.toJson
    case PlayerBasicChangeEvent.Status.PLAYER_PAUSED   =>  out ! JsStop.toJson
    case PlaylistBasicChangeEvent.Event.SONG_CHANGED =>
      (mpdConnector ? GetActualSong).mapTo[MPDSong].map { song =>
        out ! JsPlaySong(song).toJson
      }
    case _:PlaylistBasicChangeEvent.Event  => out ! JsReloadPage.toJson
    case a: Any => log.warn(s"Can't handle this msg $a")
  }
}
