package models

sealed trait ConnectorMesage
case object Connect extends ConnectorMesage
case object Disconnect extends ConnectorMesage
case object Play extends ConnectorMesage
case object Stop extends ConnectorMesage
case object Next extends ConnectorMesage
case object Prev extends ConnectorMesage
case object VolumeUp extends ConnectorMesage
case object VolumeDown extends ConnectorMesage
case object GetPlaylist extends ConnectorMesage
case class PlaySongId(id:Int) extends ConnectorMesage
