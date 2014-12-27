@(username: String)(host: String)

$(function() {
    var WS = window['MozWebSocket'] ? MozWebSocket : WebSocket
    var chatSocket = new WS("@routes.Application.gameWS(username).webSocketURL(request)")
	
	window.onbeforeunload = function() {
      chatSocket.onclose = function () {}; // disable onclose handler first
      chatSocket.close()
    };

    var sendMessage = function() {
        chatSocket.send(JSON.stringify(
            {type: "message", text: $("#talk").val()}
        ))
        $("#talk").val('')
    }
	
	function sendToSocket(json) {
		chatSocket.send(json)
	}
	
	function closeSocket() {
      chatSocket.close()
    };

    var receiveEvent = function(event) {
        var data = JSON.parse(event.data)

        // Handle errors
        if(data.error) {
            chatSocket.close()
            $("#onError span").text(data.error)
            $("#onError").show()
            return
        } else {
            $("#onChat").show()
        }

        // Create the message element
        var el = $('<div class="message"><span></span><p></p></div>')
        $("span", el).text(data.user)
        $("p", el).text(data.type+" "+data.message)
        $(el).addClass(data.type)
        if(data.user == '@username') $(el).addClass('me')
        $('#messages').append(el)

        // Update the members list
        $("#members").html('')
        $(data.members).each(function() {
            var li = document.createElement('li');
            li.textContent = this;
            $("#members").append(li);
        })
    }

    var handleReturnKey = function(e) {
        if(e.charCode == 13 || e.keyCode == 13) {
            e.preventDefault()
            sendMessage()
        }
    }

    $("#talk").keypress(handleReturnKey)
	
	@if(host == username) {
		document.getElementById("start").addEventListener("click", function(){
                              var json = JSON.stringify(
							{type: "start",
							players:[
    {username:"aaa", "x":"5", "y":"5"},
    {username:"ppp", "x":"5", "y":"5"},
    {username:"lll", "x":"5", "y":"5"},
	{username:"mmm", "x":"5", "y":"5"}
]							})
                             sendToSocket(json)
							}, false)
	}
	
	document.getElementById("leave").addEventListener("click", closeSocket, false)
	document.getElementById("collision").addEventListener("click", function(){
                             var json = JSON.stringify(
							{type: "collision",
							players:[
    {username:"aaa", "x":"5", "y":"5"},
    {username:"ppp", "x":"5", "y":"5"},
    {username:"lll", "x":"5", "y":"5"},
	{username:"mmm", "x":"5", "y":"5"}
]							})
                             sendToSocket(json)
							}, false)
							
	document.getElementById("point").addEventListener("click", function(){
                             var json = JSON.stringify(
            {type: "point", x: "5", y: "5" }
        )
                             sendToSocket(json);
							}, false)

    chatSocket.onmessage = receiveEvent

})
