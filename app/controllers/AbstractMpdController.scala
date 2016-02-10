package controllers

import play.api.mvc._
import scala.concurrent.Future
import models.MpdStatus
import models.mpdbackend.MpdConnector
import akka.actor.ActorRef

private[controllers] abstract trait AbstractMpdController extends Controller {
  implicit val ctrl: Controller = this

  /* Defines a custom action which gets the RequestHeader & MpdConnector */
  def mpdAction(fn : Request[AnyContent] => ActorRef => Future[Result]) = Action.async { request =>
    val mpdConnector = MpdConnector.getMpdActor
    fn(request)(mpdConnector)
  }}
