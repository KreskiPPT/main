@(username: String)

$(function() {
    var WS = window['MozWebSocket'] ? MozWebSocket : WebSocket
    var chatSocket = new WS("@routes.Application.chat(username).webSocketURL(request)")
	
	window.onbeforeunload = function() {
      chatSocket.onclose = function () {}; // disable onclose handler first
      chatSocket.close()
    };
	
	function closeSocket() {
      chatSocket.close()
    };
	
	function sendToSocket(type) {
		chatSocket.send(JSON.stringify(
            {type: type, text: " " }
        ))
	}

    var sendMessage = function() {
        chatSocket.send(JSON.stringify(
            {type: "message", text: $("#talk").val()}
        ))
        $("#talk").val('')
    }

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
        $("p", el).text(data.kind+" "+data.message)
        $(el).addClass(data.kind)
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
	document.getElementById("join").addEventListener("click", closeSocket, false)
	document.getElementById("refresh").addEventListener("click", function(){
                             var type = "refresh";
                             sendToSocket(type);
							}, false)

    chatSocket.onmessage = receiveEvent

})
