package controllers

import akka.actor.{ Actor, ActorRef, actorRef2Scala }
import akka.pattern.ask

import org.bff.javampd.events.{ PlayerBasicChangeEvent, PlaylistBasicChangeEvent }
import org.bff.javampd.objects.MPDSong

import models.JsMessages._
import models.mpdbackend._
import play.api.Logger
import play.api.libs.concurrent.Execution.Implicits.defaultContext

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
    case js:JsAction =>
      out ! js
      log.info(s"send to client: $js")
    case a: Any => log.warn(s"Can't handle this $a")
  }
}
