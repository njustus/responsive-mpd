package controllers

import akka.actor.actorRef2Scala
import models.mpdbackend
import models.mpdbackend.MpdConnector
import play.api.mvc.{ Action, AnyContent, Controller }
import models.mpdbackend.ClearPlaylist

class Player extends AbstractMpdController {

  def play = sendToActor(mpdbackend.PlaySong)
  def playId(id: Int) = sendToActor(mpdbackend.PlaySongId(id))
  def stop = sendToActor(mpdbackend.Stop)

  def next = sendToActor(mpdbackend.Next)
  def prev = sendToActor(mpdbackend.Prev)

  def volumeUp = sendToActor(mpdbackend.VolumeUp)
  def volumeDown = sendToActor(mpdbackend.VolumeDown)
  def shuffle(flag:Boolean) = sendToActor(mpdbackend.ShuffleSwitch(flag))
  def repeat(flag:Boolean) = sendToActor(mpdbackend.RepeatSwitch(flag))
  def removeId(idx: Int) = sendToActor(mpdbackend.RemoveSong(idx))
  def clearPlaylist = Action {
    val mpdActor = MpdConnector.getMpdActor
    mpdActor ! ClearPlaylist
    Redirect(routes.Application.index())
  }
}
