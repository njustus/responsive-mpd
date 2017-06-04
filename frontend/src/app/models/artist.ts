import {Album} from './album'

export class Artist {
		readonly name:string
		readonly albums:[Album]

		constructor(name:string, albums:[Album]) {
			this.name = name
			this.albums = albums
		}
}
