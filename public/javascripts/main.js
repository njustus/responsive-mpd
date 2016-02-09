var ENTER_KEY = 13;
var socket = newSocketHandler();

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
  $('span#volume-minus-btn').on('click', function() {
    playerHandler.volume.down(bar);
  });
  $('span#volume-plus-btn').on('click', function() {
    playerHandler.volume.up(bar);
  });
  $('span#stop-btn').on('click', function() {
	  var flag = $(this).hasClass('fi-play');
	  playerHandler.playToggle(this, flag, true);
  });
  $('span#repeat-btn').on('click', function() {
	  var flag = !($(this).attr('active')=== '1');
	  playerHandler.repeatToggle(this, flag, true);
  });
  $('span#shuffle-btn').on('click', function() {
	  var flag = !($(this).attr('active')=== '1');
	  playerHandler.shuffleToggle(this, flag, true);
  });
  $('span#prev-btn').on('click', playerHandler.prev);
  $('span#next-btn').on('click', playerHandler.next);
}
function addPlaylistHandlers() {
  $('tr.playlist-item').on('dblclick', function() {
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
