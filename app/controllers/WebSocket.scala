package controllers

import akka.actor.Props
import play.api.libs.json.JsValue
import play.api.mvc._
import play.api.Play.current
import play.api.Play.materializer

class WebSocket extends Controller {
  type Message = String

  def socket = WebSocket.acceptWithActor[Message, Message] { request => out =>
    Props(new WebSocketActor(out))
  }
}
