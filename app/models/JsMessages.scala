package models

import play.api.data.validation.ValidationError
import play.api.libs.json._ // JSON library
import play.api.libs.json.Reads._ // Custom validation helpers
import play.api.libs.functional.syntax._ // Combinator syntax

object JsMessages {

  sealed trait JsAction
  case object JsPlay extends JsAction
  case object JsStop extends JsAction
  case object JsNext extends JsAction
  case object JsPrev extends JsAction
  case class  JsPlayId(id:Int) extends JsAction

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
