package models.mpdbackend

import akka.actor.{ ActorRef, actorRef2Scala }

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.Future

import org.bff.javampd.events.{ PlayerBasicChangeEvent, PlayerBasicChangeListener, PlaylistBasicChangeEvent, PlaylistBasicChangeListener }

import play.api.libs.concurrent.Execution.Implicits.defaultContext

trait MpdListenerLike extends PlayerBasicChangeListener with PlaylistBasicChangeListener {
  //private val log: Logger = Logger("mpdlistenerlike")
  private val listeningActors = ArrayBuffer.empty[ActorRef]

  private def sendToListeners[A](m: A): Future[Unit] = Future {
    listeningActors.foreach { ref =>
      ref ! m
    }
  }

  override def playerBasicChange(ev: PlayerBasicChangeEvent): Unit = sendToListeners(ev)
  override def playlistBasicChange(ev: PlaylistBasicChangeEvent): Unit = sendToListeners(ev)

  protected def addListener(a:ActorRef): Future[Unit] = Future {
    listeningActors.synchronized {
      listeningActors += a
    }
  }
}
