package models.mpdbackend

import akka.actor.{ Actor, OneForOneStrategy }
import akka.actor.SupervisorStrategy.{ Escalate, Restart }

import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

import org.bff.javampd.exception.MPDException

import play.api.{ Logger, Play }

trait MpdSupervisor extends Actor {
  protected val log: Logger = Logger("mpdsupervisor")
  protected lazy val playConf = Play.current.configuration

  protected lazy val maxRetrys:Int = playConf.getInt("mpd.max-trys").getOrElse(4)

  override val supervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = maxRetrys, withinTimeRange = 1 minute) {
      case exc: MPDException =>
        log.warn(exc.getMessage + " - restarting actor")
        Restart
//      case npe: NullPointerException =>
//        log.error("NullPointerException occured - try solving with restarting actor")
//        Restart
      case exc: Exception =>
        log.error(s"EXCEPTION: $exc - escalating actor")
        Escalate
  }
}
