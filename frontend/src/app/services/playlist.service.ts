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
		currentSongEmitter:EventEmitter<Song> = new EventEmitter()

		constructor() {
			this.playingSong = this.currentPlaylist[3]
		}

		getPlayingSong():Song {
			return this.playingSong
		}

		getCurrentPlaylist():[Song] {
			return this.currentPlaylist
		}

		setCurrentSong(song:Song):void {
			this.playingSong = song
			this.currentSongEmitter.emit(this.playingSong)
		}
}
