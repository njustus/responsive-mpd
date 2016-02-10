package controllers

import akka.pattern.ask
import models.mpdbackend
import models.mpdbackend.MpdConnector
import play.api.mvc.{ Action, AnyContent, Controller }
import play.api.Play.current
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.Future
import models.Title
import models.mpdbackend.MpdConnector._

class Lib extends AbstractMpdController {
  def addToPlaylist(artist:Option[String], album:Option[String], title: Option[String]) =
    sendToActor(mpdbackend.AddToPlaylist(artist, album, title))

  def search(q: Option[String]) = mpdAction { implicit request => mpdActor =>
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
