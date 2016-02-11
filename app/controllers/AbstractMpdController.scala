package controllers

import akka.actor.ActorRef

import scala.concurrent.Future

import models.mpdbackend.MpdConnector
import play.api.mvc._

private[controllers] abstract trait AbstractMpdController extends Controller {
  implicit val ctrl: Controller = this

  /* Defines a custom action which gets the RequestHeader & MpdConnector */
  def mpdAction(fn : Request[AnyContent] => ActorRef => Future[Result]) = Action.async { request =>
    val mpdConnector = MpdConnector.getMpdActor
    fn(request)(mpdConnector)
  }}
