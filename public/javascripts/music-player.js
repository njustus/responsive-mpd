var player = {
  volume: {
    down: function(progressbar) {
        var percent = Number(progressbar.css('width').replace("px", ""));
        if(percent >= 10)
          progressbar.css('width', (percent-10)+"%");
      },
    up: function(progressbar) {
      var percent = Number(progressbar.css('width').replace("px", ""));
      if(percent <= 90)
        progressbar.css('width', (percent+10)+"%");
    }
  },
  playToggle: function() {
    if($(this).hasClass('fi-stop')) {
      //stop playing
      $(this).removeClass('fi-stop');
      $(this).addClass('fi-play');
    } else {
      //resume playing
      $(this).removeClass('fi-play');
      $(this).addClass('fi-stop');
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
