function playSong(elem) {
  $(elem).fadeOut("fast");
  $(elem).fadeIn("fast");
  removePlayIcons();
  addPlayIcon(elem);
}

function removeSong(elem) {
  var trElem = $(elem).parents('tr.playlist-item');
  var idx = $(trElem).attr('idx');
  $(trElem).fadeOut("slow", function() {
      $(trElem).remove();
  });
}
