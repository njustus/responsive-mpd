import { NgModule }      from '@angular/core'
import { BrowserModule } from '@angular/platform-browser'
import {NoopAnimationsModule} from '@angular/platform-browser/animations';
import { MdSidenavModule,
        MdButtonModule,
        MdButtonToggleModule,
        MdListModule,
        MdIconModule,
        MdToolbarModule,
        MdTabsModule,
        MdInputModule, } from '@angular/material'

import { AppComponent }  from './app.component'
import { PlayerComponent } from './player/player.component'
import { PlaylistComponent } from './playlist/playlist.component'
import { SearchComponent } from './search/search.component'
import { LibraryComponent } from './library/library.component'

@NgModule({
  imports: [
    NoopAnimationsModule,
    BrowserModule,
    MdSidenavModule,
    MdButtonModule,
    MdIconModule,
    MdListModule,
    MdButtonToggleModule,
    MdToolbarModule,
    MdTabsModule,
    MdInputModule,
    ],
  declarations: [
    AppComponent,
    PlaylistComponent,
    LibraryComponent,
    SearchComponent,
    PlayerComponent
  ],
  bootstrap:    [ AppComponent ]
})
export class AppModule { }
