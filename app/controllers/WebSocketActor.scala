package controllers

import akka.actor.{Actor, ActorRef}

class WebSocketActor(out: ActorRef) extends Actor {
  def receive = {
    case _: Any => out ! "nothing implemented yet!"
  }
}
