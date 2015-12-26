package models

import org.bff.javampd.Player

case class MpdStatus(
    status:Player.Status,
    actualSong:Title,
    volume: Int,
    isShuffling: Boolean,
    isLooping: Boolean
    ) {

    def isPlaying: Boolean = status == Player.Status.STATUS_PLAYING
}
