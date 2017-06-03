/**
 * Copyright (C) 2015, 2016 Nicola Justus <nicola.justus@mni.thm.de>
 * 
 * This file is part of Responsive mpd.
 * 
 * Responsive mpd is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Responsive mpd is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Responsive mpd.  If not, see <http://www.gnu.org/licenses/>.
 */
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

  def ifEmptyString(str:String)(fn: => String) = if(str.isEmpty || str.matches("^\\s+$")) fn else str

  def mpdSongsToTitles(songs:Collection[MPDSong]): List[Title] = songs.map(mpdSongToTitle(_)).toList
  def mpdSongToTitle(song:MPDSong, isPlaying:Boolean = false): Title = {
    val title  = ifEmptyString(song.getTitle)("unknown title")
    val artist = ifEmptyString(song.getArtistName)("unknown artist")
    val album  = ifEmptyString(song.getAlbumName)("unknown album")
    Title(title, artist, album, secondsToMinutesAndSeconds(song.getLength), isPlaying)
  }

  def shortMpdSong(song:MPDSong):String = s"${song.getArtistName} - ${song.getTitle}"
}
