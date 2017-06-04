import { Component } from '@angular/core'
import { Song } from '../models/song'

@Component({
  selector: 'playlist',
  templateUrl: './playlist.html'
})
export class PlaylistComponent  {
	songs:[Song] = [
    new Song("Last Hope", "Paramore", "Self-titled", 3.50),
    new Song("Until it's gone", "Linkin Park", "Living Things", 3.24),
    new Song("Last Night", "Blink-182", "Neighbourhoods", 2.33),
    new Song("OK", "Robin Schulz", "Uncovered", 5.40, true),
  ]
}
