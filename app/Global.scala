import play.api._
import play.api.libs.concurrent.Akka
import play.api.Play.current

import akka.actor.Props
import akka.actor.ActorRef

import controllers.MpdConnector
import controllers.MpdConnector._

object Global extends GlobalSettings {

  override def onStart(app: play.api.Application): Unit = {
    val mpdConnector = Akka.system.actorOf(Props[MpdConnector], name = mpdActorName)
    mpdConnector ! models.Connect
    Logger.info("Application has started")
  }

  override def onStop(app: play.api.Application): Unit = {
    val mpdConnector = MpdConnector.getMpdRef
    mpdConnector ! models.Disconnect
    Logger.info("Application shutdown...")
  }
}
