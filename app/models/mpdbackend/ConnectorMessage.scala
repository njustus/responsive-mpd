package models.mpdbackend

sealed trait ConnectorMessage
abstract class MessageWithId(id:Int)

/* -------------- player messages ---------- */
/* -------------- actions */
case object PlaySong extends ConnectorMessage
case class PlaySongId(id:Int) extends ConnectorMessage
case object Stop extends ConnectorMessage
case object Next extends ConnectorMessage
case object Prev extends ConnectorMessage

case object VolumeUp extends ConnectorMessage
case object VolumeDown extends ConnectorMessage

case class RepeatSwitch(f:Boolean) extends ConnectorMessage
case class ShuffleSwitch(f:Boolean) extends ConnectorMessage

case class RemoveSong(id:Int) extends ConnectorMessage
case class AddToPlaylist(artist:Option[String], album:Option[String], title: Option[String]) extends ConnectorMessage

/* -------------- getters */
case object GetPlaylist extends ConnectorMessage
case object GetActualSong extends ConnectorMessage
case object GetStatus extends ConnectorMessage
case object GetMpdStatus extends ConnectorMessage
case object IsShuffling extends ConnectorMessage
case object IsLooping extends ConnectorMessage

/* -------------- database messages ---------- */
/* -------------- getters */
case object GetArtistsList extends ConnectorMessage
case class GetAlbumList(artist:String) extends ConnectorMessage
case class GetAlbumTitles(artist:String, album:String) extends ConnectorMessage
case object GetStatistics extends ConnectorMessage
case object GetPlaylistNames extends ConnectorMessage
case class Search(key:String) extends ConnectorMessage

/* -------------- actions */
case object ClearPlaylist extends ConnectorMessage
case class SavePlaylist(name:String) extends ConnectorMessage
case class ChangePlaylist(name:String) extends ConnectorMessage

/*-------------- communication between websocket, mpd-actor and the listener-api from the mpd-monitor. */
/* add a websocket-actor as a listener */
case object AddSocketListener extends ConnectorMessage
