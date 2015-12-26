package controllers

import play.api._
import play.api.mvc._
import play.api.Play.current

class Player extends Controller {
  private def sendToActor(msg:models.ConnectorMesage) : Action[AnyContent] = Action {
    implicit request =>
      val mpdActor = MpdConnector.getMpdRef
      mpdActor ! msg
      Ok(msg.toString())
  }

  def play = sendToActor(models.Play)
  def stop = sendToActor(models.Stop)

  def next = sendToActor(models.Next)
  def prev = sendToActor(models.Prev)

  def volumeUp = sendToActor(models.VolumeUp)
  def volumeDown = sendToActor(models.VolumeDown)
}
