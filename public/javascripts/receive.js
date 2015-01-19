function updateMembers(data) {
  // Update the members list
  $("#members").html('')
  $(data.members).each(function() {
	var li = document.createElement('li');
	li.textContent = this;
    $("#members").append(li);
  })
}

function receiveTalk(data) {
  /*data.type
  data.user
  data.message*/
  
  var el = $('<div class="message"><span></span><p></p></div>')
  $("span", el).text(data.user)
  $("p", el).text(data.type+" "+data.message)
  $(el).addClass(data.type)
  /*if(data.user == '@username') $(el).addClass('me')
    $('#messages').append(el)*/
  $(el).addClass('me')
  $('#messages').append(el)
}

function receiveJoin(data) {
  /*data.type
  data.user
  data.message*/
  
  receiveTalk(data)
  updateMembers(data)
}

function receiveQuit(data) {
  /*data.type
  data.user
  data.message*/
  
  receiveTalk(data)
  updateMembers(data)
}

function receiveLeave(data) {
  /*data.type
  data.user
  data.message*/
  
  receiveTalk(data)
  updateMembers(data)  
}

function receiveStartGame(data) {
  /*data.type
  data.user
  data.message*/
  
  receiveTalk(data)
}

function receiveConfiguration(data) {
  /*data.type
  data.user
  data.message
  data.roomname
  data.numberOfPlayers
  data.maxNumberOfPlayers
  data.radius
  data.hostname"*/
  
  var roomname = data.roomname
  var numberOfPlayers = data.numberOfPlayers
  var maxNumberOfPlayers = data.maxNumberOfPlayers
  var radius = data.radius
  var hostname = data.hostname
  
  receiveTalk(data)
}

function receiveRefresh(data) {
  /*data.type
  data.user
  data.message
  data.rooms[] {
    ["roomname"
    "numberOfPlayers"
	"maxNumberOfPlayers"
	"radius"
	"hostname"]
  }*/
  
  receiveTalk(data)
  
  $(data.rooms).each(function() {
	var li = $('<div class="message"><span></span><p></p></div>')
	$("span", li).text(data.user)
	$("p", li).text("   Room name: "+this.roomname+" "+this.numberOfPlayers+"/"+this.maxNumberOfPlayers)
	$(li).addClass(data.type)
	$(li).addClass('me')
	$('#messages').append(li)
  })
}

function receiveStart(data) {
  /*data.type
  data.user
  data.message*/
  
  var startPositions = new Array(data.size)
  var i=0
  
  $(data.position).each(function() {
	startPositions[i] = [this.player, this.x, this.y]
	i++
  })
  
  receiveTalk(data)
}

function receiveCollision(data) {
  /*data.type
  data.user
  data.message*/
  
  receiveTalk(data)
}

function receivePoint(data) {
  /*data.type
  data.user
  data.message
  data.x
  data.y
  data.turn*/
  
  var x = data.x
  var y = data.y
  var turn = data.turn
  
  receiveTalk(data)
}

function receiveUnknown(data) {
  /*data.type
  data.user
  data.message*/
  
  receiveTalk(data)
}
  
/*  public static ObjectNode Configuration(GameRoomData data) {
    ObjectNode event = Json.newObject();
    event.put("type", "configuration");
    event.put("user", "");
    event.put("message", data.numberOfPlayers+" "+data.maxNumberOfPlayers);
    
    return event;
  }
	
	public static ObjectNode Refresh(String user, String data) {
		ObjectNode event = Json.newObject();
		event.put("type", "refresh");
		event.put("user", user);
		event.put("message", data);

		ArrayNode m = event.putArray("rooms");
		for(String u: data) {
			ObjectNode roomsdata = Json.newObject();
			roomsdata.put("roomname", u);
			m.add(roomsdata);
		}
		
		return event;
	}
	
	public static ObjectNode Collision(String username) {
    ObjectNode event = Json.newObject();
    event.put("type", "collision");
    event.put("user", username);
    event.put("message", "Collision");
    
    return event;
  }
	
	public static ObjectNode Point(String username, double x, double y) {
    ObjectNode event = Json.newObject();
    event.put("type", "point");
    event.put("user", username);
    event.put("message", x+" "+y);
    
    return event;
  }
	
	public static ObjectNode Unknown(String text) {
    ObjectNode event = Json.newObject();
    event.put("type", "unknown");
    event.put("user", "");
    event.put("message", text);
    
    return event;
  }*/