package models.mpdbackend

import akka.actor.{ Actor, ActorRef, Props, actorRef2Scala }
import play.api.Logger
import play.api.Play
import org.bff.javampd.exception.MPDException

class MpdSupervisor extends Actor {

  import akka.actor.OneForOneStrategy
  import akka.actor.SupervisorStrategy._
  import scala.concurrent.duration._
  import scala.language.postfixOps

  private lazy val playConf = Play.current.configuration

  private lazy val maxRetrys:Int = playConf.getInt("mpd.max-trys").getOrElse(4)

  override val supervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = maxRetrys, withinTimeRange = 1 minute) {
      case exc: MPDException =>
        Logger.error(exc.getMessage + " - restarting actor")
        Restart
      case exc: Exception =>
        Logger.error(s"EXCEPTION: $exc.getMessage - escalating actor")
        Escalate
  }

  def receive = {
    case p: Props =>
      //create a child with the given props
      sender ! context.actorOf(p)
    case a: Any => Logger.error(s"MpdSupervisor got unknown msg: $a")
  }
}
