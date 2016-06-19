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

import play.api.libs.json.Json

case class Title(
  name:String,
  artist:String,
  album:String,
  length: (Int, Int),
  isPlaying:Boolean = false) extends Ordered[Title] {
      def orderingString: String = s"$artist - $album - $name"
      def compare(that: Title) =
        this.orderingString compare that.orderingString
}
