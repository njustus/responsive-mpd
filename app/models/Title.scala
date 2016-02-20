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
