var defaultBlue = "#2199e8";

function fireAjax(uriObj) {
  $.ajax({
    method: uriObj.method,
    url: uriObj.uri,
    success: function(msg) {
      console.log("success");
      console.log(msg);
    },
    error: function(msg) {
      console.log(msg);
    }
  });
}

function addPlayIcon(elem) {
  var tdElem = $(elem).children(":first")
  tdElem.append("<br/>");
  tdElem.append("<span class=\"fi-play size-60 step\" id=\"actual-song-icon\"></span>");
}

function removePlayIcons() {
  var tdElems = $('tr.playlist-item > td');
  tdElems.find('br').remove();
  tdElems.find('span#actual-song-icon').remove();
}

function animateActive(element) {
	$(element).fadeOut('fast');
	$(element).fadeIn('fast');
}

function getBrowserUrl() {
  return window.location.href;
}

function toUriComponent(str) {
  return encodeURIComponent(str);
}
