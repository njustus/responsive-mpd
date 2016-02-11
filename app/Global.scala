import akka.actor.{ PoisonPill, actorRef2Scala }

import models.mpdbackend.MpdConnector
import play.api.{ Application, GlobalSettings, Logger }

object Global extends GlobalSettings {

  override def onStart(app: Application): Unit = {
    MpdConnector.getMpdActor //create actor
    Logger.info("Application has started")
  }

  override def onStop(app: Application): Unit = {
    val mpdConnector = MpdConnector.getMpdActor
    mpdConnector ! PoisonPill
    Logger.info("Application shutdown...")
  }
}
