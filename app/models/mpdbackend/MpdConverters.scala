package models.mpdbackend

import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.TimeUnit
import org.bff.javampd.objects.MPDSong
import models.Title
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Collection
import scala.collection.JavaConversions._

object MpdConverters {
  private val timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

  private def secondsToMinutesAndSeconds(seconds:Int): (Int, Int)= {
    val minutes = TimeUnit.SECONDS.toMinutes(seconds).toInt
    (
        minutes,
        seconds - TimeUnit.MINUTES.toSeconds(minutes).toInt
    )
  }

  def unixTimestampToReadable(timestamp:Long): String = {
    val instant = Instant.ofEpochSecond(timestamp)
    instant.atZone(ZoneId.systemDefault()).format(timeFormatter)
  }

  def mpdSongsToTitles(songs:Collection[MPDSong]): List[Title] = songs.map(mpdSongToTitle(_)).toList
  def mpdSongToTitle(song:MPDSong, isPlaying:Boolean = false): Title =
    Title(song.getTitle, song.getArtistName, song.getAlbumName, secondsToMinutesAndSeconds(song.getLength), isPlaying)
}
