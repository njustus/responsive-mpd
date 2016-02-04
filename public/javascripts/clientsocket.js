function newSocketHandler() {
  clientSocket = new WebSocket("ws://" + globalUris.websocket);

  clientSocket.onopen = function(evt) {
    console.log("connection opened");
  }
  clientSocket.onmessage = function(msg) {
    console.log(msg.data);
	var data = JSON.parse(msg.data);
	console.log(data);
      switch(data.action_id) {
      	case 0: case 1:
      		console.log("id 0,1: play toggle");
      		playerHandler.playToggle(true);
      		break;
      	case 2: break;
      	case 3: break;
      	case 4: break;
      	case 5:
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
