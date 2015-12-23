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
    player.volume.down(bar);
  });
  $('span#volume-plus-btn').on('click', function() {
    player.volume.up(bar);
  });
  $('span#stop-btn').on('click', player.playToggle);
  $('span#repeat-btn').on('click', player.repeatToggle);
  $('span#shuffle-btn').on('click', player.shuffleToggle);
}
function addPlaylistHandlers() {
  $('tr.playlist-item').on('dblclick', playSongPressed);
  $('td span#remove-song').on('click', removeSongPressed);
  //$('tr.playlist-item').on('click', markSong);
  //$('td span#save-playlist-btn').on('click')
}

$(document).ready(function() {
  addMenuBarHandlers();
  addPlaylistHandlers();
  addPlayerHandlers();

});

function searchPressed() {
  var key = $('li input[type=search]').val();
  player.search(key);
}
