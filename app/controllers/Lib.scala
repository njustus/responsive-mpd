package controllers

import akka.actor.actorRef2Scala
import models.mpdbackend
import models.mpdbackend.MpdConnector
import play.api.mvc.{ Action, AnyContent, Controller }

class Lib extends AbstractMpdController {
  def addToPlaylist(artist:Option[String], album:Option[String], title: Option[String]) =
    sendToActor(mpdbackend.AddToPlaylist(artist, album, title))
}
