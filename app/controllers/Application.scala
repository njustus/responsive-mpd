package controllers

import play.api._
import play.api.mvc._
import models.Title

class Application extends Controller {
  val titles = List(
    Title("In the end", "Linkin Park", "Meteora", 3.14, false),
    Title("Numb", "Linkin Park", "Meteora", 4.20, false),
    Title("No more sorrow", "Linkin Park", "Minutes to Midnight", 1.50, false),
    Title("Hands held high", "Linkin Park", "Minutes to Midnight", 2.50, true),
    Title("Papercut", "Linkin Park", "Meteora", 3.20, false)
    )


  implicit val playingSong = titles.find(_.isPlaying)

  def index = Action {
    Ok(views.html.playlist(titles))
  }

  def lib(artist:Option[String], album:Option[String]) = Action {
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
  }

}
