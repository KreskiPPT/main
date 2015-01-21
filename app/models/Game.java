package models;

import play.Logger;
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
					case "point":  
					  defaultRoom.tell(new Messages.Point(username, event.get("x").asDouble(), event.get("y").asDouble(), event.get("turn").asText()), null);
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
	public static Map<String, String> players = new HashMap<String, String>();
	public static Map<String, String> host = new HashMap<String, String>();
	public static Map<String, GameRoomData> game_list = new HashMap<String, GameRoomData>();
	public static Map<String, Collision> collision = new HashMap<String, Collision>();

	public void onReceive(Object message) throws Exception {

		if(message instanceof Messages.Join) {

			// Received a Join message
			Messages.Join join = (Messages.Join)message;

			// Check if this username is free.
			if(members.containsKey(join.username)) {
				getSender().tell("This username is already used", getSelf());
			} else {
				members.put(join.username, join.channel);
				String room = players.get(join.username);
				
				collision.get(players.get(join.username)).addPlayer(join.username);
				notifyAll(JsonMessages.Join(join.username), players.get(join.username));
				notifyAll(JsonMessages.Configuration(game_list.get(room)), room);
				getSender().tell("OK", getSelf());
			}

		} else if(message instanceof Messages.Start) {
			
			// Received a Start message
			Messages.Start start = (Messages.Start)message;

			collision.get(players.get(start.username)).init(start.players);
			
			notifyAll(JsonMessages.Start(start.players), players.get(start.username));

		} else if(message instanceof Messages.Point) {

			// Received a Point message
			Messages.Point point = (Messages.Point)message;

			/*collision.get(players.get(point.username)).addPoint(new Point(point.x,point.y), point.username);
			if(collision.get(players.get(point.username)).ifCollision(point.username))
				notifyAll(JsonMessages.Collision(point.username), players.get(point.username));
			else*/
			Logger.debug(String.format("%s (%s, %s) %s", point.username, point.x, point.y, point.turn));
				notifyAll(JsonMessages.Point(point.username, point.x, point.y, point.turn), players.get(point.username));

		} else if(message instanceof Messages.Collision) {

			// Received a Collision message
			Messages.Collision coll = (Messages.Collision)message;
			//if(collision.get(players.get(coll.username)).checkCollision(coll.username, coll.players))
				notifyAll(JsonMessages.Collision(coll.username), players.get(coll.username));
			
		} else if(message instanceof Messages.Leave)  {

			// Received a Leave message
			Messages.Leave leave = (Messages.Leave)message;

			System.out.println("LeaveGameRoom");

			String roomhost = host.get(leave.roomname);

			if(roomhost != null && roomhost.equals(leave.username)) {
				host.remove(leave.roomname);
				game_list.remove(leave.roomname);
				notifyAll(JsonMessages.Leave(leave.username), leave.roomname);
			}

		} else if(message instanceof Messages.Talk)  {

			// Received a Talk message
			Messages.Talk talk = (Messages.Talk)message;

			notifyAll(JsonMessages.Talk(talk.username, talk.text), players.get(talk.username));

		} else if(message instanceof Messages.Quit)  {

			// Received a Quit message
			Messages.Quit quit = (Messages.Quit)message;

			System.out.println("Quit");

			members.remove(quit.username);
			String room = players.get(quit.username);
			players.remove(quit.username);

			notifyAll(JsonMessages.Quit(quit.username), room);

		} else {
			unhandled(message);
		}

	}

	// Send a Json event to all members
	public void notifyAll(ObjectNode event, String room) {


		for(Map.Entry<String, String> entry: players.entrySet()) {

			if(entry.getValue().equals(room)) {

				ArrayNode m = event.putArray("members");
				for(String u: members.keySet()) {
					m.add(u);
				}
				
				if(members.get(entry.getKey()) != null)
				  members.get(entry.getKey()).write(event);
			}
		}
	}
}