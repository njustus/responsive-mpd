import { NgModule }      from '@angular/core'
import { BrowserModule } from '@angular/platform-browser'
import { MdSidenavModule, MdButtonModule, MdButtonToggleModule, MdListModule, MdIconModule, MdToolbarModule } from '@angular/material'
import {NoopAnimationsModule} from '@angular/platform-browser/animations';

import { AppComponent }  from './app.component'
import { PlaylistComponent } from './playlist/playlist.component'

@NgModule({
  imports:      [ NoopAnimationsModule, BrowserModule, MdSidenavModule, MdButtonModule, MdIconModule, MdListModule, MdButtonToggleModule, MdToolbarModule ],
  declarations: [ AppComponent, PlaylistComponent ],
  bootstrap:    [ AppComponent ]
})
export class AppModule { }
