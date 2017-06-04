export class Song {
	readonly title:string
	readonly artist:string
	readonly album:string
	readonly length:number

	constructor(title:string,artist:string,album:string,length:number) {
			this.title = title
			this.artist = artist
			this.album = album
			this.length = length
		}
}
