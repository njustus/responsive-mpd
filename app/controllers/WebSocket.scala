package controllers

import akka.actor.Props

import play.api.Play.current
import play.api.libs.json.JsValue
import play.api.mvc._

class WebSocket extends Controller {

  def socket = WebSocket.acceptWithActor[JsValue, JsValue] { request => out =>
    Props(new WebSocketActor(out))
  }
}
