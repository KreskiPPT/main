package models;

import java.util.ArrayList;
import java.util.Map;

import models.GameRoom.GameRoomData;
import play.libs.Json;
import collsion.Point;

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
  
  public static ObjectNode Init() {
    ObjectNode event = Json.newObject();
    event.put("type", "init");
    event.put("user", "");
    event.put("message", "");
    
    return event;
  }
  
  public static ObjectNode Start(Map<String, Point> players) {
    ObjectNode event = Json.newObject();
    event.put("type", "start");
    event.put("user", "");
    event.put("message", "");
    
    event.put("size", players.size());
    
    ArrayNode m = event.putArray("position");
    for(Map.Entry<String, Point> entry: players.entrySet()) {
      ObjectNode roomsdata = Json.newObject();
      roomsdata.put("player", entry.getKey());
      roomsdata.put("x", entry.getValue().x);
      roomsdata.put("y", entry.getValue().y);
      m.add(roomsdata);
    }
    
    return event;
  }
  
  public static ObjectNode Configuration(GameRoomData data) {
    ObjectNode event = Json.newObject();
    event.put("type", "configuration");
    event.put("user", "");
    event.put("message", data.room_name+" "+data.numberOfPlayers+" "+data.maxNumberOfPlayers);
    
    event.put("roomname", data.room_name);
    event.put("numberOfPlayers", data.numberOfPlayers);
    event.put("maxNumberOfPlayers", data.maxNumberOfPlayers);
    event.put("radius", data.radius);
    event.put("hostname", data.host_name);
    
    return event;
  }
	
	public static ObjectNode Refresh(String user, String messages, ArrayList<GameRoomData> data) {
		ObjectNode event = Json.newObject();
		event.put("type", "refresh");
		event.put("user", user);
		event.put("message", messages);

		ArrayNode m = event.putArray("rooms");
		for(int i=0; i < data.size(); i++) {
			ObjectNode roomsdata = Json.newObject();
			roomsdata.put("roomname", data.get(i).room_name);
			roomsdata.put("numberOfPlayers", data.get(i).numberOfPlayers);
			roomsdata.put("maxNumberOfPlayers", data.get(i).maxNumberOfPlayers);
			roomsdata.put("radius", data.get(i).radius);
			roomsdata.put("hostname", data.get(i).host_name);
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
	
	public static ObjectNode Point(String username, double x, double y, String turn) {
    ObjectNode event = Json.newObject();
    event.put("type", "point");
    event.put("user", username);
    event.put("message", x+" "+y+" "+turn);
    
    event.put("x", x);
    event.put("y", y);
    event.put("turn", turn);
    
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
