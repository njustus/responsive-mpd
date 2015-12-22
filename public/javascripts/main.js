var ENTER_KEY = 13;

$(document).ready(function() {
  var bar = $('div.progress-meter');

  // ==================== menuebar
  $('li button#search-btn').on('click', searchPressed);
  $('li input[type=search]').on('keypress', function(e) {
    if(e.which == ENTER_KEY)
      searchPressed();
  });

  // ==================== playlist
  $('tr.playlist-item').on('dblclick', playSong);
  $('td span#remove-song').on('click', removeSong);
  //$('tr.playlist-item').on('click', markSong);
  //$('td span#save-playlist-btn').on('click')

  // ==================== playing line
  $('span#volume-minus-btn').on('click', function() {
    minusPressed(bar);
  });
  $('span#volume-plus-btn').on('click', function() {
    plusPressed(bar);
  });
  $('span#stop-btn').on('click', playPressed);
  $('span#repeat-btn').on('click', repeatPressed);
  $('span#shuffle-btn').on('click', shufflePressed);
});

var searchPressed = function() {
  var key = $('li input[type=search]').val();
  console.log("search for "+key);
}
