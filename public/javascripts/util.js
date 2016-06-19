/**
 * Copyright (C) 2015, 2016 Nicola Justus <nicola.justus@mni.thm.de>
 * 
 * This file is part of Responsive mpd.
 * 
 * Responsive mpd is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Responsive mpd is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Responsive mpd.  If not, see <http://www.gnu.org/licenses/>.
 */
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

function containsOnlyWhitespace(str) {
  let regex = /\s/g;
  return (str.replace(regex, "").length <= 0) ? true : false;
}
