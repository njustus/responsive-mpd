package models

import org.bff.javampd.Player

case class MpdStatus(
    status:Option[Player.Status],
    actualSong:Option[Title],
    volume: Int,
    isShuffling: Boolean,
    isLooping: Boolean
    ) {

    def isPlaying: Boolean = status.isDefined && status.get == Player.Status.STATUS_PLAYING
}
