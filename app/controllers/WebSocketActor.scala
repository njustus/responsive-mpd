package controllers

import akka.actor.{ Actor, ActorRef, actorRef2Scala }
import akka.pattern.ask

import org.bff.javampd.events.PlayerBasicChangeEvent.Status
import org.bff.javampd.events.PlaylistBasicChangeEvent.Event
import org.bff.javampd.objects.MPDSong

import models.JsMessages.{ JsPlay, JsPlaySong, JsReloadPage, JsStop }
import models.mpdbackend.{ AddSocketListener, GetActualSong, MpdConnector }
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.JsString

class WebSocketActor(out: ActorRef) extends Actor {
  import models.mpdbackend.MpdConnector._
  val mpdConnector = MpdConnector.getMpdActor

  mpdConnector ! AddSocketListener

  def receive = {
    case Status.PLAYER_STOPPED  => out ! JsStop.toJson
    case Status.PLAYER_UNPAUSED => out ! JsPlay.toJson
    case Status.PLAYER_STARTED  => out ! JsPlay.toJson
    case Status.PLAYER_PAUSED   =>  out ! JsStop.toJson
    case Event.SONG_CHANGED =>
      (mpdConnector ? GetActualSong).mapTo[MPDSong].map { song =>
        out ! JsPlaySong(song).toJson
      }
    case Event.SONG_ADDED       => out ! JsReloadPage.toJson
    case Event.SONG_DELETED     => out ! JsReloadPage.toJson
    case Event.PLAYLIST_CHANGED => out ! JsReloadPage.toJson
    case Event.PLAYLIST_ENDED   => out ! JsStop.toJson
    case _: Any => out ! JsString("Can't handle this type of json")
  }
}
