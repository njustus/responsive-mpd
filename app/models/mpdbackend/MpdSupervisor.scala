package models.mpdbackend

import akka.actor.{ Actor, ActorRef, Props, actorRef2Scala }
import play.api.Logger
import play.api.Play
import org.bff.javampd.exception.MPDException

trait MpdSupervisor extends Actor {

  import akka.actor.OneForOneStrategy
  import akka.actor.SupervisorStrategy._
  import scala.concurrent.duration._
  import scala.language.postfixOps

  protected val log: Logger = Logger("mpdsupervisor")
  protected lazy val playConf = Play.current.configuration

  protected lazy val maxRetrys:Int = playConf.getInt("mpd.max-trys").getOrElse(4)

  override val supervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = maxRetrys, withinTimeRange = 1 minute) {
      case exc: MPDException =>
        Logger.error(exc.getMessage + " - restarting actor")
        Restart
      case npe: NullPointerException =>
        Logger.error("NullPointerException occured - try solving with restarting actor")
        Restart
      case exc: Exception =>
        Logger.error(s"EXCEPTION: $exc - escalating actor")
        Escalate
  }
}
