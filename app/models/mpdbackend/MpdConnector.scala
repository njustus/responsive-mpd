package models.mpdbackend

import scala.collection.JavaConversions.asScalaBuffer
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

import org.bff.javampd.MPD

import akka.actor.{ Actor, ActorRef, Props, actorRef2Scala }
import akka.pattern.pipe
import akka.util.Timeout
import models.MpdStatus
import play.api.Play.current
import play.api.libs.concurrent.Akka
import play.api.libs.concurrent.Execution.Implicits.defaultContext

class MpdConnector extends Actor {
  import MpdConnector._
  import play.api.Play

  private lazy val playConf = Play.current.configuration
  private var mpd: MPD = null
  private val volumeStep:Int = 10

  def receive = {
    case Connect =>
      for {
        server <- playConf.getString("mpd.servername")
        port <- playConf.getInt("mpd.port")
      } {
        playConf.getString("mpd.password") match {
          case Some(pw) => mpd = new MPD.Builder().server(server).port(port).password(pw).build()
          case None => mpd = new MPD.Builder().server(server).port(port).build()
        }
      }
    case PlaySong => mpd.getPlayer.play()
    case Stop => mpd.getPlayer.stop()
    case Next => mpd.getPlayer.playNext()
    case Prev => mpd.getPlayer.playPrev()
    case VolumeUp => mpd.getPlayer.setVolume(mpd.getPlayer.getVolume + volumeStep)
    case VolumeDown => mpd.getPlayer.setVolume(mpd.getPlayer.getVolume - volumeStep)
    case ShuffleSwitch(b) => mpd.getPlayer.setRandom(b)
    case RepeatSwitch(b) => mpd.getPlayer.setRepeat(b)
    case GetMpdStatus =>
      Future {
        MpdStatus(
          mpd.getPlayer.getStatus,
          MpdConverters.mpdSongToTitle(mpd.getPlayer.getCurrentSong),
          mpd.getPlayer.getVolume,
          mpd.getPlayer.isRandom,
          mpd.getPlayer.isRepeat
        )
      } pipeTo(sender)
    case PlaySongId(id) =>
      Future {
        mpd.getPlaylist.getSongList.find(_.getPosition == id) match {
          case Some(song) => mpd.getPlayer.playId(song)
          case None => //ignore
        }
      }
    case GetActualSong =>
      sender ! mpd.getPlayer.getCurrentSong
    case GetPlaylist =>
      Future {
        mpd.getPlaylist.getSongList.map(MpdConverters.mpdSongToTitle(_)).toList
      } pipeTo(sender)
    case s:String => println(s"Got msg $s!")
  }
}

object MpdConnector {
  private var mpdActor: Option[ActorRef] = None
  val mpdActorName: String = "Mpd-Connector"
  val mpdActorPath: String = "/user/"+mpdActorName

  implicit val actorTimeout:Timeout = Timeout(8 seconds)

  def getMpdActor: ActorRef = {
    if(!mpdActor.isDefined) {
        val actor = Akka.system.actorOf(Props[MpdConnector], name = mpdActorName)
        mpdActor = Some(actor)
    }

    mpdActor.get
  }
}
