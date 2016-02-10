package controllers

import akka.pattern.ask
import models.{ MpdStatus, Title }
import models.mpdbackend.MpdConnector.{ actorTimeout, getMpdActor }
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc.{ Action, Controller }
import scala.concurrent.Future
import models.mpdbackend.MpdConnector
import akka.actor.ActorRef
import models.mpdbackend._
import play.api.Play

class Application extends AbstractMpdController {
  def index = Action { Redirect(routes.Application.playlist()) }

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
            Ok(views.html.playlist(mappedTitles, playlists))
          }
      }
    }
  }

  def lib(artist: Option[String], album: Option[String]) = mpdAction { implicit request => mpdConnector =>
    (artist, album) match {
      case (Some(art), Some(alb)) =>
        (mpdConnector ? GetAlbumTitles(art, alb)).mapTo[List[String]].map { titles =>
          val libList = views.html.templates.lib_list(titles, artist, album, s"$art - $alb") { _ =>
            controllers.routes.Application.lib(None, None)
          }
          Ok(views.html.lib(libList))
        }
      case (Some(art), None) =>
        (mpdConnector ? GetAlbumList(art)).mapTo[List[String]].map { albums =>
          val libList = views.html.templates.lib_list(albums, artist, album, art) { s =>
            controllers.routes.Application.lib(Some(art), Some(s))
          }
          Ok(views.html.lib(libList))
        }
      case _ =>
        (mpdConnector ? GetArtistsList).mapTo[List[String]].map { artists =>
          val libList = views.html.templates.lib_list(artists, artist, album, "Artists") { s =>
            controllers.routes.Application.lib(Some(s), None)
          }
          Ok(views.html.lib(libList))
        }
    }
  }

  def stream = Action.async { implicit request =>
    Future {
        val adrOpt = Play.current.configuration.getString("mpd.streaming.ip")
        val ipOpt = Play.current.configuration.getString("mpd.streaming.port")
        Ok(views.html.stream_music(adrOpt, ipOpt))
      }
  }

  def about = mpdAction { implicit request => mpdConnector =>
    (mpdConnector ? GetStatistics).mapTo[(Map[String, String], Map[String, String])].map {
      case (general, db) => Ok(views.html.about(general, db))
    }
  }
}
