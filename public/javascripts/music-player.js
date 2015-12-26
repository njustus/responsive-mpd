var playerHandler = {
  volume: {
    down: function(progressbar) {
        var percent = Number(progressbar.css('width').replace("px", ""));
        if(percent >= 10) {
          progressbar.css('width', (percent-10)+"%");
          fireAjax(globalUris.player.volume.down);
        }
      },
    up: function(progressbar) {
      var percent = Number(progressbar.css('width').replace("px", ""));
      if(percent <= 90) {
        progressbar.css('width', (percent+10)+"%");
        fireAjax(globalUris.player.volume.up);
      }
    }
  },
  next: function() {
    fireAjax(globalUris.player.nextSong);
  },
  prev: function() {
    fireAjax(globalUris.player.prevSong);
  },
  playToggle: function() {
    if($(this).hasClass('fi-stop')) {
      //stop playing
      $(this).removeClass('fi-stop');
      $(this).addClass('fi-play');

      fireAjax(globalUris.player.stop);
    } else {
      //resume playing
      $(this).removeClass('fi-play');
      $(this).addClass('fi-stop');

      fireAjax(globalUris.player.play);
    }
  },
  repeatToggle: function() {
    if($(this).attr('active')=== '1') {
      //disable it
      fireAjax({
        method: globalUris.player.repeat.method,
        uri: globalUris.player.repeat.uri.off
      });
    } else {
      //activate it
      fireAjax({
        method: globalUris.player.repeat.method,
        uri: globalUris.player.repeat.uri.on
      });
    }
    changeActiveState(this);
  },
  shuffleToggle: function() {
    if($(this).attr('active')=== '1') {
      //disable it
      fireAjax({
        method: globalUris.player.shuffle.method,
        uri: globalUris.player.shuffle.uri.off
      });
    } else {
      //activate it
      fireAjax({
        method: globalUris.player.shuffle.method,
        uri: globalUris.player.shuffle.uri.on
      });
    }
    changeActiveState(this);
  },
  playSong: function(songIdx) {
    console.log("play idx: "+songIdx);
    $.ajax({
      method: globalUris.player.playSong.method,
      url: globalUris.player.playSong.uri + songIdx,
      success: function(msg) {
        console.log("success");
        console.log(msg);
      },
      error: function(msg) {
        console.log(msg);
      }
    });
  },
  removeSong: function(songIdx) {
    console.log("remove idx:"+songIdx);
  },
  search: function(searchString) {
    console.log("search for "+searchString);
  }
}

function changeActiveState(elem) {
  if($(elem).attr('active') === '1') {
    $(elem).css('color', 'black');
    $(elem).attr('active', '0')
  } else {
    $(elem).css('color', defaultBlue);
    $(elem).attr('active', '1')
  }
}

function fireAjax(uriObj) {
  $.ajax({
    method: uriObj.method,
    url: uriObj.uri,
    success: function(msg) {
      console.log("success");
      console.log(msg);
    },
    error: function(msg) {
      console.log(msg);
    }
  });
}
