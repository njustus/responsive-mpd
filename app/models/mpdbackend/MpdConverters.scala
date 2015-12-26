package models.mpdbackend

import java.util.concurrent.TimeUnit

import org.bff.javampd.objects.MPDSong

import models.Title

object MpdConverters {
  private def secondsToMinutesAndSeconds(seconds:Int): (Int, Int)= {
    val minutes = TimeUnit.SECONDS.toMinutes(seconds).toInt
    (
        minutes,
        seconds - TimeUnit.MINUTES.toSeconds(minutes).toInt
    )
  }

  def mpdSongToTitle(song:MPDSong, isPlaying:Boolean = false): Title =
    Title(song.getTitle, song.getArtistName, song.getAlbumName, secondsToMinutesAndSeconds(song.getLength), isPlaying)
}
