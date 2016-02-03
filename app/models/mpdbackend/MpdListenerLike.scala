package models.mpdbackend

import akka.actor.{ ActorRef, actorRef2Scala }
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.Future
import org.bff.javampd.events.{ PlayerBasicChangeEvent, PlayerBasicChangeListener, PlaylistBasicChangeEvent, PlaylistBasicChangeListener }
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.Logger
import org.bff.javampd.events.PlayerChangeListener
import org.bff.javampd.events.PlaylistChangeListener
import org.bff.javampd.events.PlayerChangeEvent
import org.bff.javampd.events.PlaylistChangeEvent

trait MpdListenerLike extends PlayerChangeListener with PlaylistChangeListener {
  private val log: Logger = Logger("mpdlistenerlike")
  private val listeningActors = ArrayBuffer.empty[ActorRef]

  private def sendToListeners[A](m: A): Future[Unit] = Future {
    log.info("got msg "+m)
    listeningActors.foreach { ref =>
      ref ! m
    }
  }

  override def playerChanged(ev: PlayerChangeEvent): Unit = sendToListeners(ev.getEvent)
  override def playlistChanged(ev: PlaylistChangeEvent): Unit = sendToListeners(ev.getEvent)

  protected def addListener(a:ActorRef): Future[Unit] = Future {
    listeningActors.synchronized {
      listeningActors += a
    }
  }
}
