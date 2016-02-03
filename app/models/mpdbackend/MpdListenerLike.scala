package models.mpdbackend

import akka.actor.ActorRef
import scala.collection.mutable.ArrayBuffer
import org.bff.javampd.events.PlayerBasicChangeListener
import org.bff.javampd.events.PlayerBasicChangeEvent

trait MpdListenerLike extends PlayerBasicChangeListener {

  override def playerBasicChange(ev: PlayerBasicChangeEvent): Unit = {
    ev.getStatus() match {
      case PlayerBasicChangeEvent.Status.PLAYER_STOPPED        =>
      case PlayerBasicChangeEvent.Status.PLAYER_STARTED        =>
      case PlayerBasicChangeEvent.Status.PLAYER_PAUSED         =>
      case PlayerBasicChangeEvent.Status.PLAYER_UNPAUSED       =>
      case PlayerBasicChangeEvent.Status.PLAYER_BITRATE_CHANGE =>
    }
  }

  protected def getListeners: ArrayBuffer[ActorRef]
}
