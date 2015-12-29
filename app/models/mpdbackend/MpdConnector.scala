package models.mpdbackend

import scala.collection.JavaConversions._
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps
import org.bff.javampd.MPD
import akka.actor.{ Actor, ActorRef, Props, actorRef2Scala }
import akka.pattern.{pipe, ask}
import akka.util.Timeout
import models.MpdStatus
import play.api.Play.current
import play.api.libs.concurrent.Akka
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import org.bff.javampd.objects.MPDArtist
import org.bff.javampd.objects.MPDAlbum
import org.bff.javampd.objects.MPDSong
import org.bff.javampd.Player
import play.api.Logger
import akka.actor.TypedActor.PostStop
import scala.concurrent.Await
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MpdConnector extends Actor {
  import MpdConnector._
  import play.api.Play

  private lazy val playConf = Play.current.configuration
  private val mpd: MPD = (for {
        server <- playConf.getString("mpd.servername")
        port <- playConf.getInt("mpd.port")
      } yield {
        val client = playConf.getString("mpd.password") match {
          case Some(pw) => new MPD.Builder().server(server).port(port).password(pw).build()
          case None => new MPD.Builder().server(server).port(port).build()
        }
        Logger.info(s"Client connected to $server : $port")
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

  private def repeatUntilSuccess[T](fn: => Option[T]): Option[T] = {
    var res:Option[T] = fn
    var i = 0
    var maxTrys = playConf.getInt("mpd.max-trys").getOrElse(4)
    while(i<maxTrys && !res.isDefined) {
      res = fn
      i += 1
    }
    res
  }

  private def addSongs(songs:java.util.Collection[MPDSong]): Future[Unit] = Future {
    val list:java.util.List[MPDSong] =
      if(songs.isInstanceOf[java.util.List[MPDSong]]) songs.asInstanceOf[java.util.List[MPDSong]]
      else new java.util.ArrayList[MPDSong](songs)

    mpd.getPlaylist.addSongs(list)
  }

  private def getPlayersStatus: Future[Option[Player.Status]] =
    Future {
        Logger.info("mpd is null: " + (mpd == null))
        Logger.info("play is null: " + (mpd.getPlayer == null))
        Logger.info("status is null: " + (mpd.getPlayer.getStatus == null))
        Option(mpd.getPlayer.getStatus)
    }

  override def postStop(): Unit = {
      mpd.close()
      Logger.info("MPD-Connection closed")
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
              mpd.getPlayer.getVolume,
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
      sender ! mpd.getPlayer.getCurrentSong
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
        mpd.getDatabase.searchAny(key).map(MpdConverters.mpdSongToTitle(_)).toList
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
        val fullName = dateString + name
        mpd.getPlaylist.savePlaylist(fullName)
      } pipeTo(sender)
    case s:String => println(s"Got msg $s!")
  }
}

object MpdConnector {
  private var mpdActor: Option[ActorRef] = None
  private var supervisingActor: Option[ActorRef] = None
//  val mpdActorName: String = "Mpd-Connector"
  val supervisorName: String = "Mpd-Supervisor"

  implicit val actorTimeout:Timeout = Timeout(8 seconds)

  def getMpdActor: ActorRef = {
    mpdActor.getOrElse {
        //supervisor
        val visor = supervisingActor.getOrElse {
          val supervisor = Akka.system.actorOf(Props[MpdSupervisor], name = supervisorName)
          supervisingActor = Some(supervisor)
          supervisor
        }

        //mpd actor
        val future = (visor ? Props[MpdConnector]).mapTo[ActorRef]

        val actor = Await.result(future, 2 minutes)
        mpdActor = Some(actor)

        actor
    }
  }
}
