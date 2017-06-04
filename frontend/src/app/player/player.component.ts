import { Component } from '@angular/core'
import { Song } from '../models/song'
import { PlaylistService } from '../services/playlist.service'

@Component({
  selector: 'player',
  templateUrl: './player.html'
})
export class PlayerComponent  {
  playlistService:PlaylistService
  currentSong:Song

  constructor(playlistService:PlaylistService) {
    this.playlistService = playlistService
    console.log("current song: ", playlistService.getPlayingSong())
    this.currentSong = playlistService.getPlayingSong()
    this.playlistService.currentSongEmitter.subscribe((song:Song) => this.currentSong = song)
  }
}
