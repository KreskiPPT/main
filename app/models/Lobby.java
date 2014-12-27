package models;

import play.mvc.*;
import play.libs.*;
import play.libs.F.*;
import scala.concurrent.Await;
import scala.concurrent.duration.Duration;
import akka.actor.*;
import static akka.pattern.Patterns.ask;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;





import java.util.*;

import models.GameRoom.GameRoomData;
import static java.util.concurrent.TimeUnit.*;

/**
 * A chat room is an Actor.
 */
public class Lobby extends UntypedActor {

	// Default room.
	public static ActorRef defaultRoom = Akka.system().actorOf(Props.create(Lobby.class));

	/**
	 * Join the default room.
	 */
	public static void join(final String username, WebSocket.In<JsonNode> in, WebSocket.Out<JsonNode> out) throws Exception{

		// Send the Join message to the room
		String result = (String)Await.result(ask(defaultRoom,new Messages.Join(username, out), 1000), Duration.create(1, SECONDS));

		if("OK".equals(result)) {

			// For each event received on the socket,
			in.onMessage(new Callback<JsonNode>() {
				public void invoke(JsonNode event) {

					switch (event.get("type").asText()) {
					case "message":  defaultRoom.tell(new Messages.Talk(username, event.get("text").asText()), null);
						break;
					case "refresh":  defaultRoom.tell(new Messages.Refresh(username), null);
					break;
					default:
						defaultRoom.tell(new Messages.Unknown(username, event.get("type").asText()), null);
						break;
					}
				} 
			});

			// When the socket is closed.
			in.onClose(new Callback0() {
				public void invoke() {

					// Send a Quit message to the room.
					defaultRoom.tell(new Messages.Quit(username), null);

				}
			});

		} else {

			// Cannot connect, create a Json error.
			ObjectNode error = Json.newObject();
			error.put("error", result);

			// Send the error to the socket.
			out.write(error);

		}

	}
	
	public static void disconnect(String username) {
		singin.remove(username);
	}
	
	public static boolean isNameUsed(String username) {
		return singin.contains(username);
	}

	// Members of this room.
	Map<String, WebSocket.Out<JsonNode>> members = new HashMap<String, WebSocket.Out<JsonNode>>();
	static List<String> singin = new ArrayList<String>();

	public void onReceive(Object message) throws Exception {

		if(message instanceof Messages.Join) {

			// Received a Join message
			Messages.Join join = (Messages.Join)message;

			if(singin.contains(join.username)) {
				members.put(join.username, join.channel);
				notifyAll("join", join.username, "has entered the room");
				getSender().tell("OK", getSelf());
			} else {
				members.put(join.username, join.channel);
				singin.add(join.username);
				notifyAll("join", join.username, "has entered the room");
				getSender().tell("OK", getSelf());
			}

		} else if(message instanceof Messages.Refresh) {

			// Received a Refresh message
			Messages.Refresh refresh = (Messages.Refresh)message;
			
			int size = 0;
			String games = "";
			for(Map.Entry<String, GameRoomData> entry: GameRoom.game_list.entrySet()) {
				if(!entry.getValue().isFull()) {
				  games+=entry.getKey()+";";
				  size++;
				}
			}
			games = size+";"+games;
			
			sendJSON(refresh.username, "refresh", refresh.username, games);

		} else if(message instanceof Messages.Talk)  {

			// Received a Talk message
			Messages.Talk talk = (Messages.Talk)message;

			notifyAll("talk", talk.username, talk.text);

		} else if(message instanceof Messages.Quit)  {

			// Received a Quit message
			Messages.Quit quit = (Messages.Quit)message;

			members.remove(quit.username);

			notifyAll("quit", quit.username, "has left the room");

		} else if(message instanceof Messages.Unknown)  {

			// Received a Unknown message
			Messages.Unknown unknown = (Messages.Unknown)message;
			
			sendJSON(unknown.username, "unknown", unknown.username, "unknown type: "+unknown.unknownType);

		} else {
			unhandled(message);
		}

	}

	// Send a Json event to all members
	public void notifyAll(String kind, String user, String text) {
		for(WebSocket.Out<JsonNode> channel: members.values()) {

			ObjectNode event = Json.newObject();
			event.put("type", kind);
			event.put("user", user);
			event.put("message", text);

			ArrayNode m = event.putArray("members");
			for(String u: members.keySet()) {
				m.add(u);
			}
			channel.write(event);
		}
	}

	// Send a Json to given member
	public void sendJSON(String username, String kind, String user, String text) {

		ObjectNode event = Json.newObject();
		event.put("type", kind);
		event.put("user", user);
		event.put("message", text);

		ArrayNode m = event.putArray("members");
		for(String u: members.keySet()) {
			m.add(u);
		}


		members.get(username).write(event);        
	}    
}
