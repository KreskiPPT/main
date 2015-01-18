package models;

import models.GameRoom.GameRoomData;
import play.libs.Json;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JsonMessages {
  public static ObjectNode Join(String user) {
    ObjectNode event = Json.newObject();
    event.put("type", "join");
    event.put("user", user);
    event.put("message", "has entered the room");
    
    return event;
  }
  
  public static ObjectNode Talk(String user, String text) {
		ObjectNode event = Json.newObject();
		event.put("type", "talk");
		event.put("user", user);
		event.put("message", text);
		
		return event;
	}
  
  public static ObjectNode Quit(String user) {
    ObjectNode event = Json.newObject();
    event.put("type", "quit");
    event.put("user", user);
    event.put("message", "has left the room");
    
    return event;
  }
  
  public static ObjectNode Leave(String user) {
    ObjectNode event = Json.newObject();
    event.put("type", "leave");
    event.put("user", user);
    event.put("message", "has left the room");
    
    return event;
  }
  
  public static ObjectNode StartGame() {
    ObjectNode event = Json.newObject();
    event.put("type", "startgame");
    event.put("user", "");
    event.put("message", "");
    
    return event;
  }
  
  public static ObjectNode Configuration(GameRoomData data) {
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

		/*ArrayNode m = event.putArray("rooms");
		for(String u: data) {
			ObjectNode roomsdata = Json.newObject();
			roomsdata.put("roomname", u);
			m.add(roomsdata);
		}*/
		
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
  }
}
