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
  }
}
