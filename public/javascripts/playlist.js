function playSongPressed() {
  var idx = $(this).attr("idx");
  $(this).fadeOut("fast");
  $(this).fadeIn("fast");
  removePlayIcons();
  addPlayIcon(this);
  playerHandler.playSong(idx);
}

function removeSongPressed() {
  var trElem = $(this).parents('tr.playlist-item');
  var idx = $(trElem).attr('idx');
  $(trElem).fadeOut("slow", function() {
      $(trElem).remove();
  });
  playerHandler.removeSong(idx);
}

/*
var unmarkSong = function(element, oldColor) {
  $(element).attr('marked', '0');
  $(element).css('background-color', oldColor);
  $(element).on('click', markSong);
}
var markSong = function() {
  var me = this;
  var idx = $(this).attr("idx");

  $(this).attr('marked', '1'); //flag to indicate that this line is marked
  var oldColor = $(this).css('background-color');
  $(this).css('background-color', defaultBlue);

  $(this).on('click', function() {
    unmarkSong(me, oldColor);
  });

  console.log("marked idx "+idx);
}*/
