import akka.actor.ActorRef
import akka.pattern.ask

import scala.concurrent.Future

import models.MpdStatus
import models.mpdbackend.MpdConnector.actorTimeout
import play.api.libs.concurrent.Execution.Implicits.defaultContext

package object controllers {

  private[controllers] def getPlayerStatus[T](mpdConnector:ActorRef)
    (fn: MpdStatus => Future[T]): Future[T] = {
      (mpdConnector ? models.mpdbackend.GetMpdStatus).mapTo[MpdStatus].flatMap(fn)
    }
}
