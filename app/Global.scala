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
import akka.actor.{ PoisonPill, actorRef2Scala }

import models.mpdbackend.MpdConnector
import play.api.{ Application, GlobalSettings, Logger }

object Global extends GlobalSettings {

  override def onStart(app: Application): Unit = {
    MpdConnector.getMpdActor //create actor
    Logger.info("Application has started")
  }

  override def onStop(app: Application): Unit = {
    val mpdConnector = MpdConnector.getMpdActor
    mpdConnector ! PoisonPill
    Logger.info("Application shutdown...")
  }
}
