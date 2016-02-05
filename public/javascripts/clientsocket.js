function newSocketHandler() {
  clientSocket = new WebSocket("ws://" + globalUris.websocket);

  clientSocket.onopen = function(evt) {
    console.log("connection opened");
  }
  clientSocket.onmessage = function(msg) {
	  var data = JSON.parse(msg.data);
    switch(data.action_id) {
    	case 0: case 1: //play,stop
    		console.log("play toggle");
    		playerHandler.playToggle(true);
    		break;
    	case 2: break;
    	case 3: break;
    	case 4: //play(song)
        //set playing line
        var elem = $('li.actual-song-text > span#actual-song-line');
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
