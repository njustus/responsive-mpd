package models

case class Title(name:String, artist:String,
      album:String, length: (Int, Int), var isPlaying:Boolean = false)
