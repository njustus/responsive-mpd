package object controllers {
  import akka.actor.actorRef2Scala
  import models.mpdbackend
  import models.mpdbackend.MpdConnector
  import play.api.mvc.{ Action, AnyContent, Controller }

  private[controllers] def sendToActor(msg:models.mpdbackend.ConnectorMesage)(implicit c:Controller) : Action[AnyContent] = Action {
    implicit request =>
      val mpdActor = MpdConnector.getMpdActor
      mpdActor ! msg
      c.Ok(msg.toString())
  }
}
