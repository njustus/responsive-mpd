import {Song} from './song'

export class Album {
	readonly songs:[Song]

	constructor(songs:[Song]) {
		this.songs = songs
	}
}
