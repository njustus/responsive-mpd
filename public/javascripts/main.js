var ENTER_KEY = 13;
function addMenuBarHandlers() {
  $('li button#search-btn').on('click', searchPressed);
  $('li input[type=search]').on('keypress', function(e) {
    if(e.which == ENTER_KEY)
      searchPressed();
  });
}
function addPlayerHandlers() {
  var bar = $('div.progress-meter');
  // ==================== playing line
  $('button#volume-minus-btn').on('click', function() {
    playerHandler.volume.down(bar);
  });
  $('button#volume-plus-btn').on('click', function() {
    playerHandler.volume.up(bar);
  });
  $('li#stop-btn').on('click', function() {
	  var flag = $(this).hasClass('fi-play');
	  playerHandler.playToggle(this, flag, true);
  });
  $('li#repeat-btn').on('click', function() {
	  var flag = $(this).hasClass('hollow');
	  playerHandler.repeatToggle(this, flag, true);
  });
  $('li#shuffle-btn').on('click', function() {
	  var flag = $(this).hasClass('hollow');
	  playerHandler.shuffleToggle(this, flag, true);
  });
  $('li#prev-btn').on('click', playerHandler.prev);
  $('li#next-btn').on('click', playerHandler.next);
}
function addPlaylistHandlers() {
  $('tr.playlist-item').on('dblclick touchstart', function() {
    var idx = $(this).attr("idx");
    playSong(this);
    playerHandler.playSong(idx);
  });
  $('td span#remove-song').on('click', function() {
    var trElem = $(this).parents('tr.playlist-item');
    var idx = $(trElem).attr('idx');
    removeSong(this);
    playerHandler.removeSong(idx);
  });
}
function addLibHandlers() {
  if(getBrowserUrl().indexOf("/lib") != -1) {
    $('span#add-to-playlist-btn').on('click', function() {
      var elem = $(this).parents('tr');
      libHandler.addToPlaylist(elem);
    });
  }

  if(getBrowserUrl().indexOf("/db") != -1) {
	  $('a.add-search-result').on('click', function() {
		  var trElem = $(this).parents('tr.search-result');
		  libHandler.addSearchResultToPlaylist(trElem);
	  });
  }
}
function searchPressed() {
  var key = $('li input[type=search]').val();
  libHandler.search(key);
}

//add all $handlers
$(document).ready(function() {
  addMenuBarHandlers();
  addPlaylistHandlers();
  addPlayerHandlers();
  addLibHandlers();
});
