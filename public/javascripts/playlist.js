function playSong(elem) {
	animateActive(elem);
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
