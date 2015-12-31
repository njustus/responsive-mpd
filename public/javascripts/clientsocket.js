function newSocketHandler() {
  clientSocket = new WebSocket("ws://" + globalUris.websocket);

  clientSocket.onopen = function(evt) {
    clientSocket.send("first msg");
  }
  clientSocket.onmessage = function(msg) {
    console.log("Got msg: "+msg):
  }

  var handler = {
    send: function(obj) {
      clientSocket.send(JSON.stringify(obj));
    }
  };
  return handler;
}
