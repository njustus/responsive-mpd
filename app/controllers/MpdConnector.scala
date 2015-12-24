package controllers

import play.api.Play
import akka.actor.Actor
import scala.collection.JavaConversions._

import models._

import org.bff.javampd.MPD
import org.bff.javampd.MPD.Builder

class MpdConnector extends Actor {
  private lazy val playConf = Play.current.configuration
  private var mpd: MPD = null

  def receive = {
    case Connect =>
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
    case Play => mpd.getPlayer.play()
    case Stop => mpd.getPlayer.stop()
    case Next => mpd.getPlayer.playNext()
    case Prev => mpd.getPlayer.playPrev()
    case PlaySongId(id) =>
      mpd.getPlaylist.getSongList.find(_.getId == id) match {
        case Some(song) => mpd.getPlayer.playId(song)
        case None => ???
      }
    case GetPlaylist => ???
    case s:String => println(s"Got msg $s!")
  }
}
