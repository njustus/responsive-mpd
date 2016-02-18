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
      //TODO: define better search: add artist,album for scoped search
      //match-all regex: artist:([\w\s\d]+)album:([\w\s\d]+)title:([\w\s\d]+)

    console.log("search for "+searchString);
    searchString = toUriComponent(searchString);

    window.location = globalUris.db.search.uri + searchString;
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
