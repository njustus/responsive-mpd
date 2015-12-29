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

class Application extends Controller {
  def index = playlist

  def playlist = Action.async {
    val mpdConnector = getMpdActor
    getPlayerStatus(mpdConnector) { implicit status =>
      (mpdConnector ? GetPlaylist).mapTo[List[Title]].flatMap { titles =>
          val mappedTitles = titles.map { x =>
            if(x == status.actualSong) {
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

  def lib(artist:Option[String], album:Option[String]) = Action.async {
    val mpdConnector = getMpdActor
    getPlayerStatus(mpdConnector) { implicit status =>
        (artist, album) match {
          case (Some(art),Some(alb)) =>
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
  }

  /*def lib(artist:Option[String], album:Option[String]) = Action {
    val library = List(
      "Linkin Park" -> "Meteora",
      "Linkin Park" -> "Minutes to midnight",
      "Paramore" -> "Live 2003",
      "Paramore" -> "Self-titled",
      "Blink182" -> "Greatest Hits",
      "Blink182" -> "Enema of the state",
      "Blink182" -> "Take of your pants and jackets"
    )

    println(artist, album)

    (artist, album) match {
      case (Some(art), Some(alb)) =>
        val filteredList = library.filter( x => x._1 == art && x._2 == alb ).map(_._2).sorted
        val artistList = views.html.templates.lib_list(filteredList, s"$art - $alb") { x =>
          controllers.routes.Application.lib(None, None)
        }
        Ok(views.html.lib(artistList))
      case (Some(art), None) =>
        val filteredList = library.filter( x => x._1 == art ).map(_._2).sorted
        val artistList = views.html.templates.lib_list(filteredList, art) { x =>
          controllers.routes.Application.lib(Some(art), Some(x))
        }
        Ok(views.html.lib(artistList))
      case _ =>
        val filteredList = library.map(_._1).sorted
        val artistList = views.html.templates.lib_list(filteredList, "Artist") { x =>
          controllers.routes.Application.lib(Some(x), None)
        }
        Ok(views.html.lib(artistList))
    }
  }

  def about = Action {
    Ok(views.html.about())
  }*/

  def about = Action.async {
    val mpdConnector = getMpdActor
    getPlayerStatus(mpdConnector) { implicit status =>
      Future {
        val general = Map(
          "network" -> "127.0.0.1",
          "system" -> "linux",
          "uptime" -> "5000 h"
        )
        val db = Map(
          "song-count" -> "3321",
          "album-count" -> "978",
          "artist-count" -> "90"
        )
        Ok(views.html.about(general, db))
      }
    }
  }
}
