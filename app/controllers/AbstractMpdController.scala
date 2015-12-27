package controllers

import play.api.mvc.Controller

private[controllers] abstract trait AbstractMpdController extends Controller {
  implicit val ctrl: Controller = this
}
