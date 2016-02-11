package models.mpdbackend

import akka.actor.{ Actor, Props, Terminated }
import akka.routing.{ ActorRefRoutee, RoundRobinRoutingLogic, Router }

import play.api.Logger

class MpdMaster extends Actor with MpdSupervisor {

  private var router = {
    val routes = Vector.fill(sizeOfPool) {
      val child = context.actorOf(Props[MpdConnector])
      context.watch(child)
      ActorRefRoutee(child)
    }
    log.info(s"Created $sizeOfPool clients - starting supervisor")
    Router(RoundRobinRoutingLogic(), routes)
  }

  def receive = {
    case msg:ConnectorMessage => router.route(msg, sender())
    case Terminated(actor) =>
      //one actor terminated, start new one
      log.warn(s"A working actor terminated - creating new one")
      router.removeRoutee(actor)
      val child = context.actorOf(Props[MpdConnector])
      context.watch(child)
      router.addRoutee(child)
    case any:Any => Logger.warn(s"Can't handle: $any")
  }

  private def sizeOfPool: Int = playConf.getInt("mpd.clientcount").getOrElse(4)
}
