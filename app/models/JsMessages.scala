package models

import org.bff.javampd.objects.MPDSong

import play.api.data.validation.ValidationError
import play.api.libs.json.{ JsValue, Json }

/**
 * JsMessages are '''only''' used for '''server-to-client communication''' over websockets.
 *
 * NOT for client-to-server, client-to-server should communicate over the RESTfull-API.
 */
object JsMessages {

  sealed abstract class JsAction(actionId:Int) {
    def toJson: JsValue = {
      Json.parse(s"""
        {
          "action_id": ${actionId}
          ${internalJson}
        }
      """)
    }

    /** Define specific json-object attributes as json-string here. */
    protected def internalJson: String = ""
  }
  case object JsPlay extends JsAction(0)
  case object JsStop extends JsAction(1)
  case object JsNext extends JsAction(2)
  case object JsPrev extends JsAction(3)
  case class  JsPlaySong(s:MPDSong) extends JsAction(4) {
    override protected def internalJson: String = s""",
      "position": ${s.getPosition},
      "title":"${s.getTitle}",
      "artist":"${s.getArtistName}",
      "album":"${s.getAlbumName}"
      """
  }
  case object JsReloadPage extends JsAction(5)
  case class JsShuffling(flag:Boolean) extends JsAction(6) {
    override protected def internalJson: String = s""",
      "isShuffling": ${flag}
      """
  }
  case class JsRepeating(flag:Boolean) extends JsAction(7) {
    override protected def internalJson: String = s""",
      "isRepeating": ${flag}
      """
  }

  private val parsingError = ValidationError("Can't prove that given json is a JsAction!")
}
