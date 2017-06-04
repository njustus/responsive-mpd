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
        MdInputModule,
        MdTooltipModule } from '@angular/material'

import { AppComponent }  from './app.component'
import { PlayerComponent } from './player/player.component'
import { PlaylistComponent } from './playlist/playlist.component'
import { SearchComponent } from './search/search.component'
import { LibraryComponent } from './library/library.component'

import {PlaylistService} from './services/playlist.service'

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
    MdTooltipModule
    ],
  declarations: [
    AppComponent,
    PlaylistComponent,
    LibraryComponent,
    SearchComponent,
    PlayerComponent
  ],
  providers: [
    PlaylistService
  ],
  bootstrap:    [ AppComponent ]
})
export class AppModule { }