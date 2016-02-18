package controllers

import akka.pattern.ask

import scala.concurrent.Future

import models.{ Title, mpdbackend }
import models.mpdbackend.MpdConnector.actorTimeout
import play.api.libs.concurrent.Execution.Implicits.defaultContext

class Lib extends AbstractMpdController {
  def addToPlaylist(artist:Option[String], album:Option[String], title: Option[String]) =
    sendToActor(mpdbackend.AddToPlaylist(artist, album, title))

  def search(q: Option[String], artist: Option[String],
            album: Option[String], title: Option[String]) = mpdAction { implicit request => mpdActor =>
              //TODO: implement handling of all options
              println(artist)
              println(album)
              println(title)
    q match {
        case Some(key) =>
          getPlayerStatus(mpdActor) { implicit status =>
            (mpdActor ? mpdbackend.Search(key)).mapTo[List[Title]].map { xs =>
              val sorted = xs.sorted
              Ok(views.html.search_result(sorted))
            }
          }
        case None => Future(Redirect(routes.Application.lib(None, None)))
    }
  }
}
