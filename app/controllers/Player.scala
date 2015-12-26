package controllers

import play.api._
import play.api.mvc._
import play.api.Play.current
import models.mpdbackend.MpdConnector
import models._

class Player extends Controller {
  private def sendToActor(msg:models.mpdbackend.ConnectorMesage) : Action[AnyContent] = Action {
    implicit request =>
      val mpdActor = MpdConnector.getMpdActor
      mpdActor ! msg
      Ok(msg.toString())
  }

  def play = sendToActor(mpdbackend.PlaySong)
  def playId(id: Int) = sendToActor(mpdbackend.PlaySongId(id))
  def stop = sendToActor(mpdbackend.Stop)

  def next = sendToActor(mpdbackend.Next)
  def prev = sendToActor(mpdbackend.Prev)

  def volumeUp = sendToActor(mpdbackend.VolumeUp)
  def volumeDown = sendToActor(mpdbackend.VolumeDown)
  def shuffle(flag:Boolean) = sendToActor(mpdbackend.ShuffleSwitch(flag))
  def repeat(flag:Boolean) = sendToActor(mpdbackend.RepeatSwitch(flag))
}
