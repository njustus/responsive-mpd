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

sealed trait ConnectorMessage

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
/* -------------- search in db */
case class Search(key:String) extends ConnectorMessage
case class SearchTitle(title:String) extends ConnectorMessage
case class SearchAlbum(album:String) extends ConnectorMessage

/* -------------- actions */
case object ClearPlaylist extends ConnectorMessage
case class SavePlaylist(name:String) extends ConnectorMessage
case class ChangePlaylist(name:String) extends ConnectorMessage
case class AddUrl(url:String) extends ConnectorMessage

/*-------------- communication between websocket, mpd-actor and the listener-api from the mpd-monitor. */
/* adds  the sender as a listener */
case object AddSocketListener extends ConnectorMessage
/* removes the sender from the listeners */
case object RemoveSocketListener extends ConnectorMessage
