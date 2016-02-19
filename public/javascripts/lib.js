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
