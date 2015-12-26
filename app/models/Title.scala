package models

case class Title(name:String, artist:String,
      album:String, length: (Int, Int), isPlaying:Boolean = false)
