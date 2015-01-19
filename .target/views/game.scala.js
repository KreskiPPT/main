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
