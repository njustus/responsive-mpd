import { Component } from '@angular/core'
import { Song } from '../models/song'

@Component({
  selector: 'library',
  templateUrl: './library.html'
})
export class LibraryComponent  {
  getItems():[any] {
    return [
      {title: "blup A"},
      {title: "blup B"},
      {title: "blup C"},
      {title: "blup C", subtitle:"bla"}]
  }
}
