package controllers

import play.api.Play
import akka.actor.Actor
import scala.collection.JavaConversions._
import models._
import org.bff.javampd.MPD
import org.bff.javampd.MPD.Builder
import akka.actor.ActorSelection

class MpdConnector extends Actor {
  private lazy val playConf = Play.current.configuration
  private var mpd: MPD = null
  private val volumeStep:Int = 10

  def receive = {
    case Connect =>
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
    case models.Play => mpd.getPlayer.play()
    case models.Stop => mpd.getPlayer.stop()
    case models.Next => mpd.getPlayer.playNext()
    case models.Prev => mpd.getPlayer.playPrev()
    case models.VolumeUp => mpd.getPlayer.setVolume(mpd.getPlayer.getVolume + volumeStep)
    case models.VolumeDown => mpd.getPlayer.setVolume(mpd.getPlayer.getVolume - volumeStep)
    case models.PlaySongId(id) =>
      mpd.getPlaylist.getSongList.find(_.getId == id) match {
        case Some(song) => mpd.getPlayer.playId(song)
        case None => ???
      }
    case models.GetPlaylist => ???
    case s:String => println(s"Got msg $s!")
  }
}

object MpdConnector {
  val mpdActorName: String = "Mpd-Connector"
  val mpdActorPath: String = "/user/"+mpdActorName

  def getMpdRef: ActorSelection = {
    Play.current.actorSystem.actorSelection(MpdConnector.mpdActorPath)
  }
}
