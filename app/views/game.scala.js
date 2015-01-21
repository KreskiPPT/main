@(username: String)(host: String)

$(function() {
    var WS = window['MozWebSocket'] ? MozWebSocket : WebSocket
    chatSocket = new WS("@routes.Application.gameWS(username).webSocketURL(request)")

	window.onbeforeunload = function() {
      chatSocket.onclose = function () {}; // disable onclose handler first
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

        switch (data.type) {
			case "join":
			  receiveJoin(data);
			break;
			case "talk":
			  receiveTalk(data);
			break;
			case "point":
			  receivePoint(data);
			break;
			case "start":
			  receiveStart(data);
			break;
			case "collision":
			  receiveConfiguration(data);
			break;
			case "configuration":
			  receiveConfiguration(data);
			break;
			case "leave":
			  receiveLeave(data);
			break;
			case "quit":
			  receiveQuit(data);
			break;
		}
    }

    var handleReturnKey = function(e) {
        if(e.charCode == 13 || e.keyCode == 13) {
            e.preventDefault()
            textMessage()
        }
    }

    $("#talk").keypress(handleReturnKey)

	@if(host == username) {
		document.getElementById("start").addEventListener("click", startMessage, false)
	}

	document.getElementById("leave").addEventListener("click", leaveMessage, false)
	document.getElementById("collision").addEventListener("click", collisionMessage, false)
	document.getElementById("point").addEventListener("click", pointMessage, false)

    chatSocket.onmessage = receiveEvent
})
