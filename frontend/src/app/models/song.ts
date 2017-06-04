export class Song {
	readonly title:string
	readonly artist:string
	readonly album:string
	readonly length:number
	readonly playing:boolean

	constructor(title:string,artist:string,album:string,length:number, playing:boolean=false) {
			this.title = title
			this.artist = artist
			this.album = album
			this.length = length
			this.playing = playing
		}
}
