function newSocketHandler() {
  clientSocket = new WebSocket("ws://" + globalUris.websocket);

  clientSocket.onopen = function(evt) {
    console.log("connection opened");
  }
  clientSocket.onmessage = function(msg) {
    console.log("Got msg: ");
    console.log(msg);
    if(typeof(msg) === 'object') {
      switch(msg.action-id) {
        case 0: case 1:
          playerHandler.playToggle(true)
          break
      }
    }
  }

  var handler = {
    send: function(obj) {
      clientSocket.send(JSON.stringify(obj));
    }
  };
  return handler;
}
