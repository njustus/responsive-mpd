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

import akka.actor.ActorRef
import scala.concurrent.Future
import models.mpdbackend.MpdConnector
import play.api.mvc._
import models.mpdbackend.ConnectorMessage

private[controllers] abstract trait AbstractMpdController extends Controller {

  /* Defines a custom action which gets the RequestHeader & MpdConnector */
  def mpdAction(fn : Request[AnyContent] => ActorRef => Future[Result]) = Action.async { request =>
    val mpdConnector = MpdConnector.getMpdActor
    fn(request)(mpdConnector)
  }

  def withActorMsg(msg:ConnectorMessage)
    (fn: => Result) : Action[AnyContent] = Action { implicit request =>
      val mpdActor = MpdConnector.getMpdActor
      mpdActor ! msg
      fn
  }

  def sendToActor(msg:ConnectorMessage) : Action[AnyContent] = Action { request =>
      val mpdActor = MpdConnector.getMpdActor
      mpdActor ! msg
      Ok(msg.toString())
  }
}
