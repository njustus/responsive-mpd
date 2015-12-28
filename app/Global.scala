import akka.actor.actorRef2Scala
import models.mpdbackend.MpdConnector
import play.api.{ GlobalSettings, Logger, Application }
import akka.actor.PoisonPill

object Global extends GlobalSettings {

  override def onStart(app: Application): Unit = {
    /*TODO
      !!! important !!!
      retry sending connect until no NullPointerException occurs or
      no of retrys exceeds
    */
    MpdConnector.getMpdActor //create actor
    Logger.info("Application has started")
  }

  override def onStop(app: Application): Unit = {
    val mpdConnector = MpdConnector.getMpdActor
    mpdConnector ! PoisonPill
    Logger.info("Application shutdown...")
  }
}
