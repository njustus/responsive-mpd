/**
 * Copyright (C) 2015, 2016 Nicola Justus <nicola.justus@mni.thm.de>
 * 
 * This file is part of Responsive mpd.
 * 
 * Responsive mpd is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Responsive mpd is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Responsive mpd.  If not, see <http://www.gnu.org/licenses/>.
 */
package models.mpdbackend

import akka.actor.{ Actor, Props, Terminated }
import akka.routing.{ ActorRefRoutee, RoundRobinRoutingLogic, Router }
import play.api.Logger
import play.api.Play
import play.api.Play.current
import play.api.libs.concurrent.Akka
import play.api.libs.concurrent.Execution.Implicits._
import scala.concurrent.duration._
import scala.language.postfixOps
import akka.actor.PoisonPill

class MpdMaster extends Actor with MpdSupervisor {

  private val lookupInterval = playConf.getInt("mpd.lookup-interval").getOrElse(5) seconds

  private val monitorActor = context.actorOf( Props(classOf[MpdMonitoringActor], MpdConnector.getMpdActor), "monitoringActor")
  private val scheduledTask = Akka.system.scheduler.schedule(10 milliseconds, lookupInterval, monitorActor, MpdMonitoringActor.Lookup)

  private var router = {
    val routes = Vector.fill(sizeOfPool) {
      val child = context.actorOf(Props[MpdConnector])
      context.watch(child)
      ActorRefRoutee(child)
    }
    log.debug(s"Created $sizeOfPool clients - starting supervisor")
    Router(RoundRobinRoutingLogic(), routes)
  }

  override def postStop(): Unit = {
    scheduledTask.cancel()
    monitorActor ! PoisonPill
    log.debug("stopped: task-scheduling, monitorActor")
  }

  def receive = {
    case AddSocketListener => monitorActor forward AddSocketListener
    case RemoveSocketListener => monitorActor forward RemoveSocketListener
    case msg:ConnectorMessage => router.route(msg, sender())
    case Terminated(actor) =>
      //one actor terminated, start new one
      log.warn(s"A working actor terminated - creating new one")
      router.removeRoutee(actor)
      val child = context.actorOf(Props[MpdConnector])
      context.watch(child)
      router.addRoutee(child)
    case any:Any => log.warn(s"Can't handle: $any")
  }

  private def sizeOfPool: Int = playConf.getInt("mpd.clientcount").getOrElse(4)
}
