/**
 * Copyright (C) 2015, 2016 Nicola Justus <nicola.justus@mni.thm.de>
 * 
 * This file is part of Responsive mpd.
 * 
 * Responsive mpd is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Responsive mpd is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Responsive mpd.  If not, see <http://www.gnu.org/licenses/>.
 */
var libHandler = {
  addToPlaylist: function(trElem) {
    var artist = $('table').attr('data-artist')
    var album = $('table').attr('data-album')
    var selectedData = trElem.attr('data-name')

    if(artist !== undefined) {
      if(album !== undefined) {
        //uri: lib?artist=...,album=...
        artist = toUriComponent(artist);
        album = toUriComponent(album);
        selectedData = toUriComponent(selectedData);
        fireAjax({
          method: globalUris.playlist.addToPlaylist.method,
          uri: globalUris.playlist.addToPlaylist.uri +
            "?artist="+artist+"&album="+album+"&title="+selectedData
        });
      } else {
        //uri: lib?artist=...
        artist = toUriComponent(artist);
        selectedData = toUriComponent(selectedData);
        fireAjax({
          method: globalUris.playlist.addToPlaylist.method,
          uri: globalUris.playlist.addToPlaylist.uri +
            "?artist="+artist+"&album="+selectedData
        });
      }
    } else {
      //uri: lib
      selectedData = toUriComponent(selectedData);
      fireAjax({
        method: globalUris.playlist.addToPlaylist.method,
        uri: globalUris.playlist.addToPlaylist.uri + "?artist="+selectedData
      });
    }

    animateActive(trElem);
  },
  search: function(searchString) {
      let createSearchUri = () => {
        let uri = globalUris.db.search.uri
        let parameters = {}
        let splitted = searchString.split("$");

        if(searchString.length <= 0) {
          return uri;
        } else if(containsOnlyWhitespace(searchString)) {
          return uri;
        }

        for(let item of splitted) {
          let matchObj = /([a-z]+):\s*([\+\.\w\d\s]+)/g.exec(item);
          if(matchObj !== null) {
            switch(matchObj[1]) {
              case "artist": parameters.artist = matchObj[2].trim(); break;
              case "album": parameters.album = matchObj[2].trim(); break;
              case "title": parameters.title = matchObj[2].trim(); break;
            }
          }
        }

        if($.isEmptyObject(parameters)) {
          return uri + "?q=" + toUriComponent(searchString);
        } else {
          let str = Object.keys(parameters).reduce( (prev, key) => {
            return prev + key + "=" + toUriComponent(parameters[key]) + "&"
          }, uri+"?");

          return str.substring(0, str.length-1);
        }
      };

    let uri = createSearchUri();
    console.log(uri);

    window.location = uri;
  },
  addSearchResultToPlaylist: function(trElem) {
	  var href = $(trElem).data('target-ref');
	  animateActive(trElem);
	  fireAjax({
		 method: globalUris.playlist.addToPlaylist.method,
		 uri: href
	  });
  }
}
