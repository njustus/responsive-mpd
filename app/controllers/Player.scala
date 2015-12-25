package controllers

import play.api._
import play.api.mvc._
import play.api.Play.current

class Player extends Controller {
  def next = Action {
    Ok("next")
  }

  def prev = Action {
    Ok("ölkjasdf")
  }
}
