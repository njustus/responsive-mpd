import { Component } from '@angular/core'
import { Song } from '../models/song'
import { PlaylistService } from '../services/playlist.service'

@Component({
  selector: 'playlist',
  templateUrl: './playlist.html'
})
export class PlaylistComponent  {
  playlistService:PlaylistService

  constructor(playlistService:PlaylistService) {
    this.playlistService = playlistService
    //this.playlistService.currentSongEmitter.subscribe((song:Song) => console.log("Song changed to: ", song))
  }

  getSongs():[Song] {
    return this.playlistService.getCurrentPlaylist()
  }
  playingSong():Song {
    return this.playlistService.getPlayingSong()
  }

  playSong(song:Song):void {
    this.playlistService.setCurrentSong(song)
  }
}
