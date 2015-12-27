package models.mpdbackend

sealed trait ConnectorMesage
case object Connect extends ConnectorMesage
case object Disconnect extends ConnectorMesage
case object PlaySong extends ConnectorMesage
case class PlaySongId(id:Int) extends ConnectorMesage
case object Stop extends ConnectorMesage
case object Next extends ConnectorMesage
case object Prev extends ConnectorMesage
case object VolumeUp extends ConnectorMesage
case object VolumeDown extends ConnectorMesage
case object GetPlaylist extends ConnectorMesage
case object GetActualSong extends ConnectorMesage
case object GetStatus extends ConnectorMesage
case object GetMpdStatus extends ConnectorMesage
case object GetArtistsList extends ConnectorMesage
case class GetAlbumList(artist:String) extends ConnectorMesage
case class GetAlbumTitles(artist:String, album:String) extends ConnectorMesage
case object IsShuffling extends ConnectorMesage
case object IsLooping extends ConnectorMesage
case class RepeatSwitch(f:Boolean) extends ConnectorMesage
case class ShuffleSwitch(f:Boolean) extends ConnectorMesage
case class RemoveSong(id:Int) extends ConnectorMesage
