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
   * TODO check js errors in chrome, (let isn't supported outside strict-mode)!
   * TODO split up connectors behaviour in several traits, mixin in the behaviour and putting the partialfns together
   * TODO add tooltip-texts to savePlaylist, clearPlaylist, deleteSong, shuffle, repeat, + icons in lib/search
   */

  def playlist = mpdAction { implicit request => mpdConnector =>
    getPlayerStatus(mpdConnector){ implicit status =>
      (mpdConnector ? GetPlaylist).mapTo[List[Title]].flatMap { titles =>
          val mappedTitles = titles.map { x =>
            if(status.actualSong.isDefined && x == status.actualSong.get) {
              new Title(x.name, x.artist, x.album, x.length, true)
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
