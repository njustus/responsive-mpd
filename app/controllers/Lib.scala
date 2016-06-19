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
import models.{ Title, mpdbackend }
import models.mpdbackend.MpdConnector.actorTimeout
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc.Result
import models.mpdbackend.MpdConnector
import play.api.mvc.RequestHeader

class Lib extends AbstractMpdController {
  def addToPlaylist(artist:Option[String], album:Option[String], title: Option[String]) =
    sendToActor(mpdbackend.AddToPlaylist(artist, album, title))

  def search(q: Option[String], artist: Option[String],
            album: Option[String], title: Option[String]) = mpdAction { implicit request => mpdActor =>
    q match {
        case Some(key) =>
            (mpdActor ? mpdbackend.Search(key)).mapTo[List[Title]].map { xs =>
              val sorted = xs.sorted
              Ok(views.html.search_result(sorted))
            }
        case None => specificSearch(artist, album, title)
    }
  }

  private def specificSearch(artist: Option[String], album: Option[String], title: Option[String])
  (implicit request:RequestHeader):Future[Result] = {
    val mpd = MpdConnector.getMpdActor
    (artist,album,title) match {
      case (None, None, Some(titl)) =>
        (mpd ? mpdbackend.SearchTitle(titl)).mapTo[List[Title]].map { xs =>
          val sorted = xs.sorted
          Ok(views.html.search_result(sorted))
        }
      case (None, Some(alb), None) =>
        (mpd ? mpdbackend.SearchAlbum(alb)).mapTo[List[Title]].map { xs =>
          val sorted = xs.sorted
          Ok(views.html.search_result(sorted))
        }
      case (Some(art), Some(alb), None) => Future.successful(Redirect(routes.Application.lib(Some(art), Some(alb))))
      case (Some(art), None, None) => Future.successful(Redirect(routes.Application.lib(Some(art), None)))
      case (Some(art), Some(alb), Some(titl)) => Future.successful(NotImplemented)
      case (_,_,_) => Future.successful(Redirect(routes.Application.lib(None, None)))
    }
  }
}
