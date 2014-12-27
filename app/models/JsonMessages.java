package models;

import play.libs.Json;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JsonMessages {
	public static ObjectNode Talk(String kind, String user, String room, String text, String[] members) {
		ObjectNode event = Json.newObject();
		event.put("kind", kind);
		event.put("user", user);
		event.put("message", text);

		ArrayNode m = event.putArray("members");
		for(String u: members) {
			m.add(u);
		}
		
		return event;
	}
	
	public static ObjectNode Refresh(String kind, String user, String room, String text, String[] data) {
		ObjectNode event = Json.newObject();
		event.put("kind", kind);
		event.put("user", user);
		event.put("message", text);

		ArrayNode m = event.putArray("members");
		for(String u: data) {
			ObjectNode roomsdata = Json.newObject();
			roomsdata.put("roomname", u);
			m.add(roomsdata);
		}
		
		return event;
	}
}
