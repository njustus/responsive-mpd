package controllers

import akka.pattern.ask
import models.{ MpdStatus, Title }
import models.mpdbackend.MpdConnector.{ actorTimeout, getMpdActor }
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc.{ Action, Controller }
import scala.concurrent.Future
import models.mpdbackend.MpdConnector
import akka.actor.ActorRef

class Application extends Controller {
  private def getPlayerStatus[T](mpdConnector:ActorRef)
    (fn: MpdStatus => Future[T]): Future[T] = {
      (mpdConnector ? models.mpdbackend.GetMpdStatus)
        .mapTo[MpdStatus].flatMap(fn)
    }

  def index = Action.async {
    val mpdConnector = getMpdActor
    getPlayerStatus(mpdConnector) { implicit status =>
      (mpdConnector ? models.mpdbackend.GetPlaylist).mapTo[List[Title]].map { titles =>
          val mappedTitles = titles.map { x =>
            if(x == status.actualSong) {
              x.isPlaying = status.isPlaying
              x
            }
            else x
          }
        Ok(views.html.playlist(mappedTitles))
      }
    }

  }

  def lib(artist:Option[String], album:Option[String]) = Action.async {
    val mpdConnector = getMpdActor
    getPlayerStatus(mpdConnector) { implicit status =>
      Future {
        val libList = views.html.templates.lib_list(Nil, "Artists") { s =>
          controllers.routes.Application.lib(None,None)
        }
        Ok(views.html.lib(libList))
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
        Ok(views.html.about())
      }
    }
  }
}
