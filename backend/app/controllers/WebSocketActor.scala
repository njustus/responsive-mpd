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
package controllers

import akka.actor.{ Actor, ActorRef, actorRef2Scala }
import akka.pattern.ask

import org.bff.javampd.events.{ PlayerBasicChangeEvent, PlaylistBasicChangeEvent }
import org.bff.javampd.objects.MPDSong

import models.JsMessages._
import models.mpdbackend._
import play.api.Logger
import play.api.libs.concurrent.Execution.Implicits.defaultContext

class WebSocketActor(out: ActorRef) extends Actor {
  import models.mpdbackend.MpdConnector._
  private val log: Logger = Logger("websocketActor")
  private val mpdConnector = MpdConnector.getMpdActor

  mpdConnector ! AddSocketListener
  log.debug("send mpdconnector request for adding as socketlistener")

  //handle closing of socket: logout from listeners
  override def postStop():Unit = {
    mpdConnector ! RemoveSocketListener
    log.debug("logged out from listeners")
  }

  def receive = {
    case js:JsAction =>
      out ! js.toJson
      log.debug(s"send to client: $js")
    case a: Any => log.warn(s"Can't handle this $a")
  }
}
