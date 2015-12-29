package models.mpdbackend

import akka.actor.Actor
import play.api.Play
import akka.actor.Props
import akka.routing.ActorRefRoutee
import akka.routing.Router
import akka.routing.RoundRobinRoutingLogic
import play.api.Logger
import akka.actor.Terminated
import akka.actor.PoisonPill

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
    case msg:ConnectorMesage => router.route(msg, sender())
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
