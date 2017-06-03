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

import akka.actor.Actor
import akka.actor.ActorRef
import akka.pattern.{pipe, ask}
import play.api.Logger
import scala.collection.mutable.ArrayBuffer
import MpdMonitoringActor._
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import models.MpdStatus
import MpdConnector._
import models.Title
import models.JsMessages._
import org.bff.javampd.objects.MPDSong
import play.api.Play

/** A monitor for an mpd-connection represented by the given ActorRef.
 *  All registered listener (actors) will get asynchronous updates([[models.JsMessages.JsAction]]'s) if the status changes.
 *  This monitor should receive [[models.mpdbackend.MpdMonitoringActor.Lookup]] -Messages from the akka-scheduler.
 */
class MpdMonitoringActor(mpd: ActorRef) extends Actor {
  private val log: Logger = Logger("mpdmonitor")
  private val listeningActors = ArrayBuffer.empty[ActorRef]

  //(old) mpd-status
  private var song: MPDSong = null
  private var isPlaying: Boolean = false
  private var isLooping:Boolean = false
  private var isShuffling:Boolean = false
  private var playlist:List[Title] = Nil

  private def fireMsg(m:JsAction): Unit = {
    listeningActors.foreach(x => x ! m)
  }

  private def lookupChanges(): Future[Unit] = {
    for {
      status <- (mpd ? GetMpdStatus).mapTo[MpdStatus]
      newSong <- (mpd ? GetActualSong).mapTo[Option[MPDSong]]
      newList <- (mpd ? GetPlaylist).mapTo[List[Title]]
    } yield {
      if(status.isPlaying != isPlaying) {
        isPlaying = status.isPlaying
        fireMsg( if(isPlaying) JsPlay else JsStop )
        log.info(s"isPlaying changed: $isPlaying")
      }
      if(newSong.isDefined && song != newSong.get) {
          val tmpSong = newSong.get
          song = tmpSong
          fireMsg(JsPlaySong(tmpSong))
          log.info(s"song changed: ${MpdConverters.shortMpdSong(song)}")
      }
      if(status.isLooping != isLooping) {
        isLooping = status.isLooping
        fireMsg(JsRepeating(isLooping))
        log.info(s"looping changed: $isLooping")
      }
      if(status.isShuffling != isShuffling) {
        isShuffling = status.isShuffling
        fireMsg(JsShuffling(isShuffling))
        log.info(s"shuffling changed: $isShuffling")
      }
      if(newList != playlist) {
        playlist = newList
        fireMsg(JsReloadPage)
        log.info("playlist changed")
      }
    }
  }

  def receive = {
    case AddSocketListener =>
      log.info("got new listener")
      listeningActors += sender()
    case RemoveSocketListener =>
      log.info("remove listener")
      listeningActors -= sender()
    case Lookup =>
      lookupChanges()
  }
}

object MpdMonitoringActor {
  sealed trait MonitorMessages
  case object Lookup extends MonitorMessages
}
