package models;

import play.mvc.*;
import play.libs.*;
import play.libs.F.*;
import scala.concurrent.Await;
import scala.concurrent.duration.Duration;
import akka.actor.*;
import static akka.pattern.Patterns.ask;
import collsion.Collision;
import collsion.Point;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;































import java.util.*;

import models.GameRoom.GameRoomData;
import static java.util.concurrent.TimeUnit.*;

/**
 * A chat room is an Actor.
 */
public class Game extends UntypedActor {

	// Default room.
	public static ActorRef defaultRoom = Akka.system().actorOf(Props.create(Game.class));

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
					case "point":  defaultRoom.tell(new Messages.Point(username, event.get("x").asDouble(), event.get("y").asDouble()), null);
					break;
					case "collision":
						
						Map<String, Point> players = new HashMap<String, Point>();
						JsonNode arrayJson = event.get("players");
						if (arrayJson.isArray()) {
						    for (final JsonNode node : arrayJson) {
						    	String user = node.get("username").asText();
								double x =  node.get("x").asDouble();
								double y =  node.get("y").asDouble();
						    	
								players.put(user, new Point(x,y));
						    }
						}
						
						defaultRoom.tell(new Messages.Collision(username, players), null);
						
						break;
					case "start":
						
						Map<String, Point> splayers = new HashMap<String, Point>();
						JsonNode sarrayJson = event.get("players");
						if (sarrayJson.isArray()) {
						    for (final JsonNode node : sarrayJson) {
						    	String user = node.get("username").asText();
								double x =  node.get("x").asDouble();
								double y =  node.get("y").asDouble();
						    	
								splayers.put(user, new Point(x,y));
						    }
						}
						
						defaultRoom.tell(new Messages.Start(username, splayers), null);
						
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

	// Members of this room.
	Map<String, WebSocket.Out<JsonNode>> members = new HashMap<String, WebSocket.Out<JsonNode>>();
	Map<String, String> players = new HashMap<String, String>();
	Map<String, String> host = new HashMap<String, String>();
	public static Map<String, GameRoomData> game_list = new HashMap<String, GameRoomData>();
	public static Map<String, Collision> collision = new HashMap<String, Collision>();

	public void onReceive(Object message) throws Exception {

		if(message instanceof Messages.CreateGame) {

			// Received a CreateGame message
			Messages.CreateGame create = (Messages.CreateGame)message;

			players.put(create.username, create.room_name);
			host.put(create.room_name, create.username);

		} else if(message instanceof Messages.JoinGame) {

			// Received a JoinGame message
			Messages.JoinGame join = (Messages.JoinGame)message;

			players.put(join.username, join.room_name);

		} else if(message instanceof Messages.Join) {

			// Received a Join message
			Messages.Join join = (Messages.Join)message;

			// Check if this username is free.
			if(members.containsKey(join.username)) {
				getSender().tell("This username is already used", getSelf());
			} else {
				members.put(join.username, join.channel);
				collision.get(players.get(join.username)).addPlayer(join.username);
				notifyAll("join", join.username, players.get(join.username), "has entered the room");
				getSender().tell("OK", getSelf());
			}

		} else if(message instanceof Messages.Start) {
			
			// Received a Start message
			Messages.Start start = (Messages.Start)message;

			collision.get(players.get(start.username)).init(start.players);
			
			notifyAll("start", start.username, players.get(start.username), "game start");

		} else if(message instanceof Messages.Point) {

			// Received a Point message
			Messages.Point point = (Messages.Point)message;

			collision.get(players.get(point.username)).addPoint(new Point(point.x,point.y), point.username);
			if(collision.get(players.get(point.username)).ifCollision(point.username))
				notifyAll("collision", point.username, players.get(point.username), point.username+" Kolizja");
			else
				notifyAll("point", point.username, players.get(point.username), point.x+" "+point.y);

		} else if(message instanceof Messages.Collision) {

			// Received a Collision message
			Messages.Collision coll = (Messages.Collision)message;
			if(collision.get(players.get(coll.username)).checkCollision(coll.username, coll.players))
				notifyAll("collision", coll.username, players.get(coll.username), coll.username);
			
		} else if(message instanceof Messages.Leave)  {

			// Received a Leave message
			Messages.Leave leave = (Messages.Leave)message;

			System.out.println("LeaveGameRoom");

			String roomhost = host.get(leave.roomname);

			if(roomhost != null && roomhost.equals(leave.username)) {
				host.remove(leave.roomname);
				game_list.remove(leave.roomname);
				notifyAll("leave", leave.username, leave.roomname, "has left the room");
			}

		} else if(message instanceof Messages.Talk)  {

			// Received a Talk message
			Messages.Talk talk = (Messages.Talk)message;

			notifyAll("talk", talk.username, players.get(talk.username), talk.text);

		} else if(message instanceof Messages.Quit)  {

			// Received a Quit message
			Messages.Quit quit = (Messages.Quit)message;

			System.out.println("Quit");

			members.remove(quit.username);
			String room = players.get(quit.username);
			players.remove(quit.username);

			notifyAll("quit", quit.username, room, "has left the room");

		} else {
			unhandled(message);
		}

	}

	// Send a Json event to all members
	public void notifyAll(String kind, String user, String room, String text) {


		for(Map.Entry<String, String> entry: players.entrySet()) {

			if(entry.getValue().equals(room)) {
				ObjectNode event = Json.newObject();
				event.put("type", kind);
				event.put("user", user);
				event.put("message", text);

				ArrayNode m = event.putArray("members");
				for(String u: members.keySet()) {
					m.add(u);
				}

				members.get(entry.getKey()).write(event);
			}
		}
	}
}