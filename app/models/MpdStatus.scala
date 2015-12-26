package models

import org.bff.javampd.Player.Status

case class MpdStatus(status:Status, actualSong:Option[Title]) {

}
