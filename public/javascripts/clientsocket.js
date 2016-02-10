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
