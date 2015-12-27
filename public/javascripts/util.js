var defaultBlue = "#2199e8";

function addPlayIcon(elem) {
  console.log(elem);
  var tdElem = $(elem).children(":first")
  tdElem.append("<br/>");
  tdElem.append("<span class=\"fi-play size-60 step\" id=\"actual-song-icon\"></span>");
}

function removePlayIcons() {
  var tdElems = $('tr.playlist-item > td');
  tdElems.find('br').remove();
  tdElems.find('span#actual-song-icon').remove();
}

function getBrowserUrl() {
  return window.location.href;
}
