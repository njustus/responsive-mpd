/**
 * Copyright (C) 2015, 2016 Nicola Justus <nicola.justus@mni.thm.de>
 * 
 * This file is part of Responsive mpd.
 * 
 * Responsive mpd is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Responsive mpd is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Responsive mpd.  If not, see <http://www.gnu.org/licenses/>.
 */
package models.mpdbackend

import akka.actor.{ Actor, ActorRef, Props, actorRef2Scala }
import akka.pattern.pipe
import akka.util.Timeout
import scala.collection.JavaConversions._
import scala.collection.mutable.HashMap
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import org.bff.javampd.{ MPD, Player }
import org.bff.javampd.objects.{ MPDAlbum, MPDArtist, MPDSong }
import models.MpdStatus
import models.mpdbackend.MpdConverters.unixTimestampToReadable
import play.api.Logger
import play.api.Play
import play.api.Play.current
import play.api.libs.concurrent.Akka
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import org.bff.javampd.MPDFile

class MpdConnector extends Actor {
  import MpdConnector._

  private lazy val playConf = Play.current.configuration
  private val log: Logger = Logger("mpdconnector")

  private val mpd: MPD = (for {
        server <- playConf.getString("mpd.servername")
        port <- playConf.getInt("mpd.port")
      } yield {
        val client = playConf.getString("mpd.password") match {
          case Some(pw) => new MPD.Builder().server(server).port(port).password(pw).build()
          case None => new MPD.Builder().server(server).port(port).build()
        }
        log.info(s"Client connected to $server : $port")
        client
      }).getOrElse {
        throw new IllegalStateException("Can't create mpd-instance!")
      }

  private val volumeStep:Int = 10

  private[mpdbackend] def getSongById(id:Int): Future[Option[MPDSong]] =
    Future { mpd.getPlaylist.getSongList.find(_.getPosition == id) }

  private[mpdbackend] def ifSongIsDefined[T](id:Int)(fn : MPDSong => T): Future[T] =
    for {
      songOpt <- getSongById(id)
      if songOpt.isDefined
      song = songOpt.get
    } yield { fn(song) }

  private def addSongs(songs:java.util.Collection[MPDSong]): Future[Unit] = Future {
    val list:java.util.List[MPDSong] =
      if(songs.isInstanceOf[java.util.List[MPDSong]]) songs.asInstanceOf[java.util.List[MPDSong]]
      else new java.util.ArrayList[MPDSong](songs)

    mpd.getPlaylist.addSongs(list)
  }

  private def generalStatistic: Future[Map[String, String]] =
    Future {
      Map("Serveraddress" -> mpd.getAddress.getCanonicalHostName,
          "Port" -> mpd.getPort.toString,
          "MPD-Version" -> mpd.getVersion,
          "MPD-Uptime" -> unixTimestampToReadable(mpd.getAdmin.getDaemonUpTime))
    }

  private def dbStatistic: Future[Map[String, String]] =
    Future {
      val db = mpd.getDatabase

      Map("No of artists" -> db.getArtistCount.toString,
          "No of albums" -> db.getAlbumCount.toString,
          "No of songs" -> db.getSongCount.toString,
          "DB update date" -> unixTimestampToReadable(db.getLastUpdateTime))
  }

  private def getPlayersStatus: Future[Option[Player.Status]] =
    Future {
      try {
        //getStatus throws npe if status not found
        Some(mpd.getPlayer.getStatus)
      } catch {
        case _:NullPointerException => None
      }
    }

  override def postStop(): Unit = {
      mpd.close()
      log.info("MPD-Connection closed")
  }

  def receive = {
    case PlaySong => mpd.getPlayer.play()
    case Stop => mpd.getPlayer.stop()
    case Next => mpd.getPlayer.playNext()
    case Prev => mpd.getPlayer.playPrev()
    case VolumeUp => mpd.getPlayer.setVolume(mpd.getPlayer.getVolume + volumeStep)
    case VolumeDown => mpd.getPlayer.setVolume(mpd.getPlayer.getVolume - volumeStep)
    case ShuffleSwitch(b) => mpd.getPlayer.setRandom(b)
    case RepeatSwitch(b) => mpd.getPlayer.setRepeat(b)
    case GetMpdStatus =>
      getPlayersStatus.map { status =>
        val curSongOpt = Option(mpd.getPlayer.getCurrentSong)

        curSongOpt match {
          case Some(song) =>
            MpdStatus(
              status,
              Some(MpdConverters.mpdSongToTitle(song)),
              mpd.getPlayer.getVolume,
              mpd.getPlayer.isRandom,
              mpd.getPlayer.isRepeat
            )
          case None =>
            MpdStatus(
              status,
              None,
              0, //no song = no volume
              mpd.getPlayer.isRandom,
              mpd.getPlayer.isRepeat
            )
        }
      } pipeTo(sender)
    case PlaySongId(id) =>
        ifSongIsDefined(id) { song =>
          mpd.getPlayer.playId(song)
        }
    case RemoveSong(id) =>
      ifSongIsDefined(id) { song =>
          mpd.getPlaylist.removeSong(song)
      }
    case t @ AddToPlaylist(artistOpt, albumOpt, titleOpt) =>
      Future {
        (artistOpt, albumOpt, titleOpt) match {
          case (Some(artist), Some(album), Some(title)) =>
            val albumSongs = mpd.getDatabase.findAlbumByArtist(new MPDArtist(artist), new MPDAlbum(album))
            albumSongs.find(_.getTitle == title).foreach(mpd.getPlaylist.addSong)
          case (Some(artist), Some(album), None) =>
            val songs = mpd.getDatabase.findAlbumByArtist(new MPDArtist(artist), new MPDAlbum(album))
            addSongs(songs)
          case (Some(artist), None, None) =>
            val songs = mpd.getDatabase.findArtist(artist)
            addSongs(songs)
          case _ => //ignore
        }
      }
    case GetActualSong =>
      sender ! Option(mpd.getPlayer.getCurrentSong)
    case GetPlaylist =>
      Future {
         mpd.getPlaylist.getSongList.map(MpdConverters.mpdSongToTitle(_)).toList
      } pipeTo(sender)
    case GetArtistsList =>
      Future {
        mpd.getDatabase.listAllArtists.map(_.getName)
      } pipeTo(sender)
    case GetAlbumList(artist) =>
      Future {
        mpd.getDatabase.listAlbumsByArtist(new MPDArtist(artist)).map(_.getName)
      } pipeTo(sender)
    case GetAlbumTitles(artist, album) =>
      Future {
        mpd.getDatabase.findAlbumByArtist(new MPDArtist(artist), new MPDAlbum(album)).map(_.getName)
      } pipeTo(sender)
    case Search(key) =>
      Future {
        MpdConverters.mpdSongsToTitles( mpd.getDatabase.searchAny(key) )
      } pipeTo(sender)
    case SearchTitle(title) =>
      Future {
        MpdConverters.mpdSongsToTitles( mpd.getDatabase.findTitle(title) )
      } pipeTo(sender)
    case SearchAlbum(album) =>
      Future {
        MpdConverters.mpdSongsToTitles( mpd.getDatabase.findAlbum(album) )
      } pipeTo(sender)
    case GetPlaylistNames =>
      Future {
        mpd.getDatabase.listPlaylists().toList
      } pipeTo(sender)
    case ClearPlaylist =>
      Future {
        mpd.getPlaylist.clearPlaylist()
      }
    case SavePlaylist(name) =>
      Future {
        val dateString = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        val fullName = dateString + "-" + name
        mpd.getPlaylist.savePlaylist(fullName)
      } pipeTo(sender)
    case ChangePlaylist(name) =>
      Future {
        mpd.getPlaylist.clearPlaylist()
        mpd.getPlaylist.loadPlaylist(name)
      }
    case AddUrl(url) =>
      Future {
        log.warn("add-url not supported yet!")
      }
    case GetStatistics =>
      generalStatistic flatMap { general =>
        dbStatistic map { db =>
          (general, db)
        }
      } pipeTo(sender)
    case a:Any => log.warn(s"Can't handle this msg: $a")
  }
}

object MpdConnector {
  private var supervisingActor: Option[ActorRef] = None
//  val mpdActorName: String = "Mpd-Connector"
  val supervisorName: String = "Mpd-Supervising-Router"

  implicit val actorTimeout:Timeout = Timeout(8 seconds)

  def getMpdActor: ActorRef = {
    //supervisor
    if(!supervisingActor.isDefined) {
      val supervisor = Akka.system.actorOf(Props[MpdMaster], name = supervisorName)
      supervisingActor = Some(supervisor)
    }

    supervisingActor.get
  }
}
