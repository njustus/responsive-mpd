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
  $('span#volume-minus-btn').on('click', function() {
    playerHandler.volume.down(bar);
  });
  $('span#volume-plus-btn').on('click', function() {
    playerHandler.volume.up(bar);
  });
  $('span#stop-btn').on('click', playerHandler.playToggle);
  $('span#repeat-btn').on('click', playerHandler.repeatToggle);
  $('span#shuffle-btn').on('click', playerHandler.shuffleToggle);
  $('span#prev-btn').on('click', playerHandler.prev);
  $('span#next-btn').on('click', playerHandler.next);
}
function addPlaylistHandlers() {
  $('tr.playlist-item').on('dblclick', playSongPressed);
  $('td span#remove-song').on('click', removeSongPressed);
  //$('tr.playlist-item').on('click', markSong);
  //$('td span#save-playlist-btn').on('click')
}

function addLibHandlers() {
  if(getBrowserUrl().indexOf("/lib") != -1) {
    $('span#add-to-playlist-btn').on('click', function() {
      var elem = $(this).parents('tr');
      libHandler.addToPlaylist(elem);
    });
  }
}

$(document).ready(function() {
  addMenuBarHandlers();
  addPlaylistHandlers();
  addPlayerHandlers();
  addLibHandlers();

  console.log(globalUris);
});

function searchPressed() {
  var key = $('li input[type=search]').val();
  libHandler.search(key);
}
