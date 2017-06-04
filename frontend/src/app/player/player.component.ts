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
    this.currentSong = playlistService.getPlayingSong()
    this.playlistService.currentSongEmitter.subscribe((song:Song) => {
      this.currentSong = song
    })
  }

  togglePlay():void {
    if(this.playlistService.getPlayingSong() === undefined) {
      this.playlistService.setCurrentSong(this.playlistService.getCurrentPlaylist()[0])
    } else {
      this.playlistService.setCurrentSong(undefined)
    }
  }

  nextSong():void {
    this.playlistService.playNextSong()
  }

  prevSong():void {
    this.playlistService.playPrevSong()
  }
}
