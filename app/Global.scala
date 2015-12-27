import akka.actor.actorRef2Scala
import models.mpdbackend.MpdConnector
import play.api.{ GlobalSettings, Logger }

object Global extends GlobalSettings {

  override def onStart(app: play.api.Application): Unit = {
    /*TODO
      !!! important !!!
      retry sending connect until no NullPointerException occurs or
      no of retrys exceeds
    */
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
