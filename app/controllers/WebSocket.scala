package controllers

import akka.actor.Props

import play.api.Play.current
import play.api.libs.json.JsValue
import play.api.mvc._

class WebSocket extends Controller {
  type Message = JsValue

  def socket = WebSocket.acceptWithActor[Message, Message] { request => out =>
    Props(new WebSocketActor(out))
  }
}
