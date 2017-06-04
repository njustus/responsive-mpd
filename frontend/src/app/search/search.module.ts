import { NgModule }      from '@angular/core'
 import { MdListModule } from '@angular/material'
import { SearchComponent } from './search.component'

@NgModule({
  imports:      [ MdListModule ],
  declarations: [ SearchComponent ]
})
export class SearchModule { }
