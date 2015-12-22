var minusPressed = function(progressbar) {
  var percent = Number(progressbar.css('width').replace("px", ""));
  if(percent >= 10)
    progressbar.css('width', (percent-10)+"%");
}
var plusPressed = function(progressbar) {
  var percent = Number(progressbar.css('width').replace("px", ""));
  if(percent <= 90)
    progressbar.css('width', (percent+10)+"%");
}
var playPressed = function() {
  if($(this).hasClass('fi-stop')) {
    //stop playing
    $(this).removeClass('fi-stop');
    $(this).addClass('fi-play');
  } else {
    //resume playing
    $(this).removeClass('fi-play');
    $(this).addClass('fi-stop');
  }
}
var repeatPressed = function() {
  changeActiveState(this);
}

var shufflePressed = function() {
  changeActiveState(this);
}

var changeActiveState = function(elem) {
  if($(elem).attr('active') === '1') {
    $(elem).css('color', 'black');
    $(elem).attr('active', '0')
  } else {
    $(elem).css('color', defaultBlue);
    $(elem).attr('active', '1')
  }
}
