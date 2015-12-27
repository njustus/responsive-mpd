package object controllers {
  import akka.actor.actorRef2Scala
  import akka.actor.ActorRef
  import akka.pattern.ask
  import models.{ MpdStatus, Title }
  import models.mpdbackend.MpdConnector
  import models.mpdbackend.MpdConnector._
  import scala.concurrent.Future
  import play.api.libs.concurrent.Execution.Implicits.defaultContext
  import play.api.mvc.{ Action, AnyContent, Controller }

  private[controllers] def getPlayerStatus[T](mpdConnector:ActorRef)
    (fn: MpdStatus => Future[T]): Future[T] = {
      (mpdConnector ? models.mpdbackend.GetMpdStatus)
        .mapTo[MpdStatus].flatMap(fn)
    }

  private[controllers] def sendToActor(msg:models.mpdbackend.ConnectorMesage)(implicit c:Controller) : Action[AnyContent] = Action {
    implicit request =>
      val mpdActor = MpdConnector.getMpdActor
      mpdActor ! msg
      c.Ok(msg.toString())
  }
}