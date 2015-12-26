package controllers

import play.api._
import play.api.mvc._
import play.api.Play.current
import models.mpdbackend.MpdConnector

class Player extends Controller {
  private def sendToActor(msg:models.mpdbackend.ConnectorMesage) : Action[AnyContent] = Action {
    implicit request =>
      val mpdActor = MpdConnector.getMpdActor
      mpdActor ! msg
      Ok(msg.toString())
  }

  def play = sendToActor(models.mpdbackend.PlaySong)
  def stop = sendToActor(models.mpdbackend.Stop)

  def next = sendToActor(models.mpdbackend.Next)
  def prev = sendToActor(models.mpdbackend.Prev)

  def volumeUp = sendToActor(models.mpdbackend.VolumeUp)
  def volumeDown = sendToActor(models.mpdbackend.VolumeDown)
}
