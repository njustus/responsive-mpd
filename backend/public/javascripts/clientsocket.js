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
function newSocketHandler() {
  var clientSocket = new WebSocket(globalUris.websocket);

  clientSocket.onopen = function(evt) {
    console.log("connection opened");
  }
  clientSocket.onmessage = function(msg) {
	var data = JSON.parse(msg.data);
	switch(data.action_id) {
    	case 0: //play
    		console.log("resume playing");
    		playerHandler.playToggle($('li#stop-btn'), true, false);
    		break;
		  case 1: //stop
    		console.log("stop playing");
    		playerHandler.playToggle($('li#stop-btn'), false, false);
    		break;
    	case 2: break;
    	case 3: break;
    	case 4: //play(song)
        //set playing line
        var elem = $('span#actual-song-line');
        var newLine = data.artist + " - " + data.title;
        elem.html(newLine);

        //set icon in playlist
        var listItem = $('tr.playlist-item').filter(function() {
          return $(this).attr('idx') == data.position
        });
        playSong(listItem);
        break;
    	case 5: //reload
    		clientSocket.close();
    		location.reload(true);
    		break;
      case 6: //shuffling
        var $btn = $('li#shuffle-btn');
        if( (data.isShuffling && $btn.hasClass('hollow')) ||
            (!data.isShuffling && !$btn.hasClass('hollow'))
          ) {
          /*conditions:
              - button is off & state is on
              - button is on & state is off
          */
          playerHandler.repeatToggle($btn, data.isRepeating, false);
        }
        break;
      case 7: //repeating
        var $btn = $('li#repeat-btn');
        if( (data.isRepeating && $btn.hasClass('hollow')) ||
            (!data.isRepeating && !$btn.hasClass('hollow'))
          ) {
          /*conditions:
              - button is off & state is on
              - button is on & state is off
          */
          playerHandler.repeatToggle($btn, data.isRepeating, false);
        }
        break;
      default:
        console.log("socket got unknown message");
        console.log(data);
        break;
      }
  }

  var handler = {
    send: function(obj) {
      clientSocket.send(JSON.stringify(obj));
    },
  	close: function() {
  		clientSocket.close();
  	}
  };

  window.onbeforeunload = function() {
	 clientSocket.close();
  }

  return handler;
}
