import play.api._
import play.api.libs.concurrent.Akka
import play.api.Play.current
import akka.actor.Props
import akka.actor.ActorRef
import models.mpdbackend.MpdConnector
import models.mpdbackend.MpdConnector._

object Global extends GlobalSettings {

  override def onStart(app: play.api.Application): Unit = {
    val mpdConnector = MpdConnector.getMpdActor
    mpdConnector ! models.mpdbackend.Connect
    Logger.info("Application has started")
  }

  override def onStop(app: play.api.Application): Unit = {
    val mpdConnector = MpdConnector.getMpdActor
    mpdConnector ! models.mpdbackend.Disconnect
    Logger.info("Application shutdown...")
  }
}
