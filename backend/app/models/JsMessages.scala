/**
 * Copyright (C) 2015, 2016 Nicola Justus <nicola.justus@mni.thm.de>
 * 
 * This file is part of Responsive mpd.
 * 
 * Responsive mpd is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Responsive mpd is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Responsive mpd.  If not, see <http://www.gnu.org/licenses/>.
 */
package models

import org.bff.javampd.objects.MPDSong

import play.api.data.validation.ValidationError
import play.api.libs.json.{ JsValue, Json }

import mpdbackend.MpdConverters

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

    override def toString(): String = s"JsPlaySong(${MpdConverters.shortMpdSong(s)})"
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
}
