package controllers

import play.api._
import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.concurrent.Akka
import play.api.Play.current
import akka.actor.Props
import akka.pattern.ask
import models._
import models.mpdbackend.MpdConnector
import models.mpdbackend.MpdConnector._
import models.mpdbackend.MpdConverters._
import akka.actor.Identify
import scala.concurrent.Future
import org.bff.javampd.objects.MPDSong
import models.mpdbackend.MpdConverters

class Application extends Controller {

  def index = Action.async {
    val mpdConnector = getMpdActor
    (mpdConnector ? models.mpdbackend.GetMpdStatus) flatMap { anySong =>
      val status = anySong.asInstanceOf[MpdStatus]
      (mpdConnector ? models.mpdbackend.GetPlaylist) map { anyList =>
        val titles = anyList.asInstanceOf[List[Title]].map { x =>
          if(x == status.actualSong) {
            x.isPlaying = status.isPlaying
            x
          }
          else x
        }
        Ok(views.html.playlist(titles)(status))
      }
    }
  }

  def lib(artist:Option[String], album:Option[String]) = TODO

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

  def about = TODO
}
