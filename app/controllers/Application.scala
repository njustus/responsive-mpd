package controllers

import akka.pattern.ask

import scala.concurrent.Future

import models.Title
import models.mpdbackend._
import models.mpdbackend.MpdConnector.actorTimeout
import play.api.Play
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc.Action
import views._
import html.templates._

class Application extends AbstractMpdController {
  private lazy val playConf = Play.current.configuration

  def index = Action { Redirect(routes.Application.playlist()) }

  /*
   * TODO Change return of GetActualSong to Option[Song] .. this song could be null (if playlist is empty), which breaks akka's message-delivery roules
   * TODO split up connectors behaviour in several traits, mixin in the behaviour and putting the partialfns together
   * TODO add tooltip-texts to savePlaylist, clearPlaylist, deleteSong, shuffle, repeat, + icons in lib/search
   */

  def playlist = mpdAction { implicit request => mpdConnector =>
    getPlayerStatus(mpdConnector){ implicit status =>
      (mpdConnector ? GetPlaylist).mapTo[List[Title]].flatMap { titles =>
          val mappedTitles = titles.map { x =>
            if(status.actualSong.isDefined && x == status.actualSong.get) {
              x.isPlaying = status.isPlaying
              x
            }
            else x
          }

          (mpdConnector ? GetPlaylistNames).mapTo[List[String]].map { playlists =>
            Ok(html.playlist(mappedTitles, playlists))
          }
      }
    }
  }

  def lib(artist: Option[String], album: Option[String]) = mpdAction { implicit request => mpdConnector =>
    (artist, album) match {
      case (Some(art), Some(alb)) =>
        (mpdConnector ? GetAlbumTitles(art, alb)).mapTo[List[String]].map { titles =>
          val libList = lib_list(titles, artist, album, s"$art - $alb") { _ =>
            routes.Application.lib(None, None)
          }
          Ok(html.lib(libList))
        }
      case (Some(art), None) =>
        (mpdConnector ? GetAlbumList(art)).mapTo[List[String]].map { albums =>
          val libList = lib_list(albums, artist, album, art) { s =>
            routes.Application.lib(Some(art), Some(s))
          }
          Ok(html.lib(libList))
        }
      case _ =>
        (mpdConnector ? GetArtistsList).mapTo[List[String]].map { artists =>
          val libList = lib_list(artists, artist, album, "Artists") { s =>
            routes.Application.lib(Some(s), None)
          }
          Ok(html.lib(libList))
        }
    }
  }

  def stream = Action.async { implicit request =>
    Future {
        val adrOpt = playConf.getString("mpd.streaming.ip")
        val ipOpt = playConf.getString("mpd.streaming.port")
        Ok(html.stream_music(adrOpt, ipOpt))
      }
  }

  def about = mpdAction { implicit request => mpdConnector =>
    (mpdConnector ? GetStatistics).mapTo[(Map[String, String], Map[String, String])].map {
      case (general, db) => Ok(html.about(general, db))
    }
  }
}
