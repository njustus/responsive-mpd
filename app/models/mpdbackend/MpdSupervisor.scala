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

import akka.actor.{ Actor, OneForOneStrategy }
import akka.actor.SupervisorStrategy.{ Escalate, Restart }

import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

import org.bff.javampd.exception.MPDException

import play.api.{ Logger, Play }

trait MpdSupervisor extends Actor {
  protected val log: Logger = Logger("mpdsupervisor")
  protected lazy val playConf = Play.current.configuration

  protected lazy val maxRetrys:Int = playConf.getInt("mpd.max-trys").getOrElse(4)

  override val supervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = maxRetrys, withinTimeRange = 1 minute) {
      case exc: MPDException =>
        log.warn(exc.getMessage + " - restarting actor")
        Restart
//      case npe: NullPointerException =>
//        log.error("NullPointerException occured - try solving with restarting actor")
//        Restart
      case exc: Exception =>
        log.error(s"EXCEPTION: $exc - escalating actor")
        Escalate
  }
}
