package controllers

import akka.actor.ActorRef
import scala.concurrent.Future
import models.mpdbackend.MpdConnector
import play.api.mvc._
import models.mpdbackend.ConnectorMessage

private[controllers] abstract trait AbstractMpdController extends Controller {

  /* Defines a custom action which gets the RequestHeader & MpdConnector */
  def mpdAction(fn : Request[AnyContent] => ActorRef => Future[Result]) = Action.async { request =>
    val mpdConnector = MpdConnector.getMpdActor
    fn(request)(mpdConnector)
  }

  def withActorMsg(msg:ConnectorMessage)
    (fn: => Result) : Action[AnyContent] = Action { implicit request =>
      val mpdActor = MpdConnector.getMpdActor
      mpdActor ! msg
      fn
  }

  def sendToActor(msg:ConnectorMessage) : Action[AnyContent] = Action { request =>
      val mpdActor = MpdConnector.getMpdActor
      mpdActor ! msg
      Ok(msg.toString())
  }
}
