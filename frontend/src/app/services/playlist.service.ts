import { Song } from '../models/song'
import { EventEmitter } from '@angular/core'

export class PlaylistService {
		currentPlaylist:[Song] = [
	    new Song("Last Hope", "Paramore", "Self-titled", 3.50),
	    new Song("Until it's gone", "Linkin Park", "Living Things", 3.24),
	    new Song("Last Night", "Blink-182", "Neighbourhoods", 2.33),
	    new Song("OK", "Robin Schulz", "Uncovered", 5.40),
			new Song("The Sound of Silence", "Disturbed", "Immortalized", 3.20),
	  ]

		playingSong:Song
		currentSongIdx:number
		currentSongEmitter:EventEmitter<Song> = new EventEmitter()

		constructor() {
			this.currentSongIdx = 3
			this.playingSong = this.currentPlaylist[this.currentSongIdx]
		}

		getPlayingSong():Song {
			return this.playingSong
		}

		getCurrentPlaylist():[Song] {
			return this.currentPlaylist
		}

		private songChanged(): void {
			this.currentSongEmitter.emit(this.playingSong)
		}

		setCurrentSong(song:Song):void {
			this.playingSong = song
			this.currentSongIdx = this.currentPlaylist.findIndex((s:Song) => s === song)
			this.songChanged()
		}

		playNextSong(): void {
			this.currentSongIdx+=1
			this.playingSong = this.currentPlaylist[this.currentSongIdx]
			this.songChanged()
		}

		playPrevSong(): void {
			this.currentSongIdx-=1
			this.playingSong = this.currentPlaylist[this.currentSongIdx]
			this.songChanged()
		}
}
