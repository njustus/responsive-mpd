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
  playToggle: function(element, flag, ajaxCallFlag) {
    if($(element).hasClass('fi-stop') && flag === false) {
      //stop playing
      $(element).removeClass('fi-stop');
      $(element).addClass('fi-play');
      if(ajaxCallFlag) {
    	  fireAjax(globalUris.player.stop);
      }
    } else if($(element).hasClass('fi-play') && flag === true){
      //resume playing
      $(element).removeClass('fi-play');
      $(element).addClass('fi-stop');
      if(ajaxCallFlag) {
    	  fireAjax(globalUris.player.play);
      }
    }
  },
  repeatToggle: function(element, flag, ajaxCallFlag) {
	  if(!flag) {
    	//disable it
    	changeActiveState(element);
    	if(ajaxCallFlag) {
			fireAjax({
		        method: globalUris.player.repeat.method,
		        uri: globalUris.player.repeat.uri.off
		    	});
    	}
    } else if(flag){
    	//activate it
    	changeActiveState(element);
    	if(ajaxCallFlag) {
	      fireAjax({
	          method: globalUris.player.repeat.method,
	          uri: globalUris.player.repeat.uri.on
	        });
    	}
    }
  },
  shuffleToggle: function(element, flag, ajaxCallFlag) {
    if(!flag) {
    	//disable it
    	changeActiveState(element);
      if(ajaxCallFlag) {
    	fireAjax({
	        method: globalUris.player.shuffle.method,
	        uri: globalUris.player.shuffle.uri.off
    	});
      }
    } else if(flag){
      //activate it
      changeActiveState(element);
    	if(ajaxCallFlag) {
	      fireAjax({
	        method: globalUris.player.shuffle.method,
	        uri: globalUris.player.shuffle.uri.on
	      });
    	}
    }
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
    fireAjax({
      method: globalUris.playlist.removeSong.method,
      uri: globalUris.playlist.removeSong.uri + songIdx,
    });
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
