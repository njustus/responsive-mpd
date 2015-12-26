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
    changeActiveState(this);
  },
  shuffleToggle: function() {
    changeActiveState(this);
  },
  playSong: function(songIdx) {
    console.log("play idx: "+songIdx);
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
