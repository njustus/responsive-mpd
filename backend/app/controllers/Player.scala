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

import akka.actor.actorRef2Scala

import scala.concurrent.Future

import models.mpdbackend._
import play.api.data.Form
import play.api.data.Forms.{ nonEmptyText, single }
import play.api.libs.concurrent.Execution.Implicits.defaultContext

class Player extends AbstractMpdController {

  private val playlistNameForm = Form(
    single( "playlistname" -> nonEmptyText)
  )
  private val addUrlForm = Form(
    single( "url" -> nonEmptyText )
  )

  def play = sendToActor(PlaySong)
  def playId(id: Int) = sendToActor(PlaySongId(id))
  def stop = sendToActor(Stop)

  def next = sendToActor(Next)
  def prev = sendToActor(Prev)

  def volumeUp = sendToActor(VolumeUp)
  def volumeDown = sendToActor(VolumeDown)
  def shuffle(flag:Boolean) = sendToActor(ShuffleSwitch(flag))
  def repeat(flag:Boolean) = sendToActor(RepeatSwitch(flag))
  def removeId(idx: Int) = sendToActor(RemoveSong(idx))
  def clearPlaylist = withActorMsg(ClearPlaylist) {
    Redirect(routes.Application.playlist())
  }

  def savePlaylist = mpdAction { implicit request => mpd =>
    Future {
      playlistNameForm.bindFromRequest().fold(
        withErrors => {
          BadRequest
        },
        playlistName => {
          mpd ! SavePlaylist(playlistName)
          Redirect(routes.Application.playlist())
        })
    }
  }

  def addUrl = mpdAction { implicit request => mpd =>
    Future {
      addUrlForm.bindFromRequest().fold(
        withErrors => {
          BadRequest
        },
        url => {
          println(s"got $url")
          Redirect(routes.Application.playlist())
        })
    }
  }

  def changePlaylist(name: Option[String]) = mpdAction { implicit request => mpd =>
    Future.successful(NotImplemented)
  }
}
