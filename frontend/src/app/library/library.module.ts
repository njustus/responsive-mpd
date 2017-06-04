import { NgModule }      from '@angular/core'
 import { MdListModule } from '@angular/material'
import { LibraryComponent } from './library.component'

@NgModule({
  imports:      [ MdListModule ],
  declarations: [ LibraryComponent ]
})
export class LibraryModule { }
