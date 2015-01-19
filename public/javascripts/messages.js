var chatSocket = null;

function sendToSocket(json) {
  chatSocket.send(json)
}

function textMessage() {
  var json = JSON.stringify(
    {type: "message", text: $("#talk").val()}
  )
  $("#talk").val('')
  
  sendToSocket(json);
}

function leaveMessage() {
  chatSocket.close()
}

function refreshMessage() {
  var json = JSON.stringify(
    {type: "refresh" }
  )
  
  sendToSocket(json)
}

function joinMessage() {
  chatSocket.close()
}

function configurationMessage() {
  var json = JSON.stringify(
    { type: "configuration", numberOfPlayers: "8", radius: "30" }
  )
  
  sendToSocket(json)
}

function playMessage() {
  chatSocket.close()
}

function startMessage() {
  var json = JSON.stringify(
	{type: "start",
	 players:[
		{username:"aaa", "x":"5", "y":"5"},
		{username:"ppp", "x":"5", "y":"5"},
		{username:"lll", "x":"5", "y":"5"},
		{username:"mmm", "x":"5", "y":"5"}]
	})
  sendToSocket(json)
}

function collisionMessage() {
  var json = JSON.stringify(
    {type: "collision",
	  players:[
		{username:"aaa", "x":"5", "y":"5"},
		{username:"ppp", "x":"5", "y":"5"},
		{username:"lll", "x":"5", "y":"5"},
		{username:"mmm", "x":"5", "y":"5"}]
	})
  
  sendToSocket(json)
}

function pointMessage() {
  var json = JSON.stringify(
    {type: "point", x: "5", y: "5" }
  )
  
  sendToSocket(json)
}
	
	