package models

import play.api.data.validation.ValidationError
import play.api.libs.json._ // JSON library
import play.api.libs.json.Reads._ // Custom validation helpers
import play.api.libs.functional.syntax._ // Combinator syntax

object JsMessages {

  sealed abstract class JsAction(actionId:Int) {
    def toJson: JsValue = Json.parse(s"""
        {
          "action-id": $actionId,
          ${internalJson}
        }
      """)

    /** Define specific json-object attributes as json-string here. */
    protected def internalJson: String = ""
  }
  case object JsPlay extends JsAction(0)
  case object JsStop extends JsAction(1)
  case object JsNext extends JsAction(2)
  case object JsPrev extends JsAction(3)
  case class  JsPlayId(id:Int) extends JsAction(4) {
    override protected def internalJson: String = s""" "id":$id """
  }

  private val parsingError = ValidationError("Can't prove that given json is a JsAction!")

  private val readActionId: Reads[Int] = (JsPath \ "action-id").read[Int]
  val objReads: Reads[JsAction] = readActionId.collect(parsingError)(getMsgByObjectsId)

  val playId: Reads[JsAction] = (
    readActionId and
    (JsPath \ "id").read[Int] ).tupled.collect(parsingError) {
    case (4, id) => JsPlayId(id)
  }

  val reads: Reads[JsAction] = playId.orElse(objReads)

  def getMsgByObjectsId: PartialFunction[Int, JsAction] = {
    case 0 => JsPlay
    case 1 => JsStop
    case 2 => JsNext
    case 3 => JsPrev
  }
}
