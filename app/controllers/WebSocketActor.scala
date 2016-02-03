package controllers

import akka.actor.{Actor, ActorRef}
import models.JsMessages
import models.JsMessages._
import play.api.data.validation.ValidationError
import play.api.libs.json._
import models.mpdbackend.MpdConnector

class WebSocketActor(out: ActorRef) extends Actor {

  val mpdConnector = MpdConnector.getMpdActor

  def toJsResult: PartialFunction[Any, JsResult[JsAction]] = {
    case js:JsValue => js.validate(reads)
  }

  def parsedJsonMessage: PartialFunction[Any, JsAction] = toJsResult.andThen {
    case JsSuccess(elem,_) => elem
  }

  def receive = parsedJsonMessage.andThen {
    case JsPlay => println("got play msg")
    case JsStop => println("got stop msg")
    case _: Any => out ! JsString("Can't handle this type of json")
  }
}
