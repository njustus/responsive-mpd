package models.mpdbackend

import play.api._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.concurrent.Akka
import play.api.Play.current
import akka.actor.Actor
import scala.collection.JavaConversions._
import org.bff.javampd.MPD
import akka.actor.ActorSelection
import akka.actor.ActorRef
import akka.actor.Props
import akka.util.Timeout
import akka.pattern.{ask, pipe}
import scala.concurrent.duration._
import scala.language.postfixOps
import models._
import scala.concurrent.Future

class MpdConnector extends Actor {
  import MpdConnector._
  import play.api.Play

  private lazy val playConf = Play.current.configuration
  private var mpd: MPD = null
  private val volumeStep:Int = 10

  def receive = {
    case mpdbackend.Connect =>
      println(s"Connect ${playConf.getString("mpd.servername")} ${playConf.getInt("mpd.port")}")
      for {
        server <- playConf.getString("mpd.servername")
        port <- playConf.getInt("mpd.port")
      } {
        println(s"connect to $server : $port")
        playConf.getString("mpd.password") match {
          case Some(pw) => mpd = new MPD.Builder().server(server).port(port).password(pw).build()
          case None => mpd = new MPD.Builder().server(server).port(port).build()
        }
      }
    case mpdbackend.PlaySong => mpd.getPlayer.play()
    case mpdbackend.Stop => mpd.getPlayer.stop()
    case mpdbackend.Next => mpd.getPlayer.playNext()
    case mpdbackend.Prev => mpd.getPlayer.playPrev()
    case mpdbackend.VolumeUp => mpd.getPlayer.setVolume(mpd.getPlayer.getVolume + volumeStep)
    case mpdbackend.VolumeDown => mpd.getPlayer.setVolume(mpd.getPlayer.getVolume - volumeStep)
    case mpdbackend.ShuffleSwitch(b) => mpd.getPlayer.setRandom(b)
    case mpdbackend.RepeatSwitch(b) => mpd.getPlayer.setRepeat(b)
    case mpdbackend.GetMpdStatus =>
      Future {
        MpdStatus(
          mpd.getPlayer.getStatus,
          MpdConverters.mpdSongToTitle(mpd.getPlayer.getCurrentSong),
          mpd.getPlayer.getVolume,
          mpd.getPlayer.isRandom,
          mpd.getPlayer.isRepeat
        )
      } pipeTo(sender)
    case mpdbackend.PlaySongId(id) =>
      Future {
        mpd.getPlaylist.getSongList.find(_.getPosition == id) match {
          case Some(song) => mpd.getPlayer.playId(song)
          case None => //ignore
        }
      }
    case mpdbackend.GetActualSong =>
      sender ! mpd.getPlayer.getCurrentSong
    case mpdbackend.GetPlaylist =>
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
