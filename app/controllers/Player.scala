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
