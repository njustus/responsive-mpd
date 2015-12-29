package controllers

import akka.actor.actorRef2Scala
import models.mpdbackend.MpdConnector
import play.api.data._
import play.api.data.Forms._
import play.api.mvc.{ Action, AnyContent, Controller }

class Player extends AbstractMpdController {

  import models.mpdbackend._

  private val playlistNameForm = Form(
    single( "playlistname" -> nonEmptyText) )

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
    Redirect(routes.Application.index())
  }

  def savePlaylist = Action { implicit request =>
    playlistNameForm.bindFromRequest().fold(
      withErrors => {
        BadRequest
      },
      playlistName => {
        val mpd = MpdConnector.getMpdActor
        mpd ! SavePlaylist(playlistName)

        Redirect(routes.Application.index())
      }
    )

  }

  def changePlaylist(name:Option[String]) = Action { implicit request =>
    name match {
      case Some(n) =>
        val mpd = MpdConnector.getMpdActor
        mpd ! ChangePlaylist(n)
        Redirect(routes.Application.index())
      case None => Redirect(routes.Application.index())
    }
  }
}
