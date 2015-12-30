package models.mpdbackend

sealed trait ConnectorMesage

/* -------------- player messages ---------- */
/* -------------- actions */
case object PlaySong extends ConnectorMesage
case class PlaySongId(id:Int) extends ConnectorMesage
case object Stop extends ConnectorMesage
case object Next extends ConnectorMesage
case object Prev extends ConnectorMesage

case object VolumeUp extends ConnectorMesage
case object VolumeDown extends ConnectorMesage

case class RepeatSwitch(f:Boolean) extends ConnectorMesage
case class ShuffleSwitch(f:Boolean) extends ConnectorMesage

case class RemoveSong(id:Int) extends ConnectorMesage
case class AddToPlaylist(artist:Option[String], album:Option[String], title: Option[String]) extends ConnectorMesage

/* -------------- getters */
case object GetPlaylist extends ConnectorMesage
case object GetActualSong extends ConnectorMesage
case object GetStatus extends ConnectorMesage
case object GetMpdStatus extends ConnectorMesage
case object IsShuffling extends ConnectorMesage
case object IsLooping extends ConnectorMesage

/* -------------- database messages ---------- */
/* -------------- getters */
case object GetArtistsList extends ConnectorMesage
case class GetAlbumList(artist:String) extends ConnectorMesage
case class GetAlbumTitles(artist:String, album:String) extends ConnectorMesage
case object GetStatistics extends ConnectorMesage
case object GetPlaylistNames extends ConnectorMesage
case class Search(key:String) extends ConnectorMesage

/* -------------- actions */
case object ClearPlaylist extends ConnectorMesage
case class SavePlaylist(name:String) extends ConnectorMesage
case class ChangePlaylist(name:String) extends ConnectorMesage
