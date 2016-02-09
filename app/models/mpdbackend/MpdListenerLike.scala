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
import org.bff.javampd.monitor.MPDStandAloneMonitor
import org.bff.javampd.StandAloneMonitor

trait MpdListenerLike extends PlayerBasicChangeListener with PlaylistBasicChangeListener {
  private val log: Logger = Logger("mpdlistenerlike")
  private val listeningActors = ArrayBuffer.empty[ActorRef]

  protected val monitor:StandAloneMonitor

  private def sendToListeners[A](m: A): Future[Unit] = Future {
    log.info("got msg "+m)
    listeningActors.foreach { ref =>
      ref ! m
    }
  }

  override def playerBasicChange(ev: PlayerBasicChangeEvent): Unit = sendToListeners(ev.getStatus)
  override def playlistBasicChange(ev: PlaylistBasicChangeEvent): Unit = sendToListeners(ev.getEvent)

  protected def addListener(a:ActorRef): Future[Unit] = Future {
    listeningActors.synchronized {
      listeningActors += a
    }
  }

  protected def removeListener(a:ActorRef): Future[Unit] = Future {
    listeningActors.synchronized {
      listeningActors -= a
    }
  }

  protected def setupMonitor():Unit = {
    monitor.addPlayerChangeListener(this)
    monitor.addPlaylistChangeListener(this)
    monitor.start()
    log.info("setup & started monitor-thread")
  }

  protected def stopMonitor(): Unit = {
    monitor.stop()
    log.info("monitor stopped")
  }
}
