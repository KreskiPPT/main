package models;

import play.mvc.*;
import play.libs.*;
import play.libs.F.*;
import scala.concurrent.Await;
import scala.concurrent.duration.Duration;
import akka.actor.*;
import static akka.pattern.Patterns.ask;
import collsion.Collision;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.util.*;

import static java.util.concurrent.TimeUnit.*;

/**
 * A chat room is an Actor.
 */
public class GameRoom extends UntypedActor {

	// Default room.
	public static ActorRef defaultRoom = Akka.system().actorOf(Props.create(GameRoom.class));

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
					case "configuration":  defaultRoom.tell(new Messages.GameConfiguration(username, event.get("numberOfPlayers").asInt(), event.get("numberOfPlayers").asDouble()), null);
					break;
					  case "play":  defaultRoom.tell(new Messages.StartGame(username, null), null);
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

	public void onReceive(Object message) throws Exception {

		if(message instanceof Messages.CreateGame) {

			// Received a CreateGame message
			Messages.CreateGame create = (Messages.CreateGame)message;

			players.put(create.username, create.room_name);
			host.put(create.room_name, create.username);

		} else if(message instanceof Messages.JoinGameRoom) {

			// Received a JoinGameRoom message
			Messages.JoinGameRoom join = (Messages.JoinGameRoom)message;

			players.put(join.username, join.room_name);

		} else if(message instanceof Messages.Join) {

			// Received a Join message
			Messages.Join join = (Messages.Join)message;

			// Check if this username is free.
			if(members.containsKey(join.username)) {
				getSender().tell("This username is already used", getSelf());
			} else {
				members.put(join.username, join.channel);
				String room = players.get(join.username);
				int numberOfPlayers = ++game_list.get(room).numberOfPlayers;
				
				notifyAll(JsonMessages.Join(join.username), room);
				notifyAll(JsonMessages.Configuration(game_list.get(room)), room);
				getSender().tell("OK", getSelf());
			}

		} else if(message instanceof Messages.GameConfiguration) {

			// Received a GameConfiguration message
			Messages.GameConfiguration conf = (Messages.GameConfiguration)message;
			
			String room = players.get(conf.username);
			String roomhost = host.get(room);

			if(roomhost != null && roomhost.equals(conf.username)) {
				game_list.get(room).maxNumberOfPlayers=conf.maxNumberOfPlayers;
				game_list.get(room).radius=conf.radius;
				notifyAll(JsonMessages.Configuration(game_list.get(room)), room);
			}

		} else if(message instanceof Messages.StartGame)  {

			// Received a StartGame message
			Messages.StartGame start = (Messages.StartGame)message;

			System.out.println("StartGame ");

			String roomname = players.get(start.username);
			String roomhost = host.get(roomname);

			if(roomhost != null && roomhost.equals(start.username)) {				
				
				Game.game_list.put(roomname, game_list.get(roomname));
				Game.collision.put(roomname, new Collision(game_list.get(roomname).radius));
				//Game.defaultRoom.tell(new Messages.CreateGame(start.username, start.roomname), null);
				
				for(Map.Entry<String, String> entry: players.entrySet()) {
				  Game.players.put(entry.getKey(), entry.getValue());
				}
				Game.host.put(start.username, roomname);
				
				host.remove(roomname);
				game_list.remove(roomname);
				System.out.println("Usuwanie "+game_list.size());
				notifyAll(JsonMessages.StartGame(), roomname);
			}
			else {
				Game.defaultRoom.tell(new Messages.JoinGame(start.username, roomname), null);
			}

		} else if(message instanceof Messages.InitializedGame)  {

      // Received a Talk message
      Messages.InitializedGame init = (Messages.InitializedGame)message;

      sendJSON(init.username, JsonMessages.Init());

    } else if(message instanceof Messages.Leave)  {

			// Received a Talk message
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
			
			if(game_list.get(room) != null)
			  game_list.get(room).numberOfPlayers--;

			notifyAll(JsonMessages.Quit(quit.username), room);

		} else if(message instanceof Messages.Unknown)  {

			// Received a Unknown message
			Messages.Unknown unknown = (Messages.Unknown)message;
			
			sendJSON(unknown.username, JsonMessages.Unknown("unknown type: "+unknown.unknownType));

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

				members.get(entry.getKey()).write(event);
			}
		}
	}
	
	public static class GameRoomData {
		final String room_name;
		final String host_name;
		int numberOfPlayers;
		int maxNumberOfPlayers;
		double radius;
		
		public boolean isFull() {
		  return numberOfPlayers == maxNumberOfPlayers;
		}

		public GameRoomData(String room_name, String host_name) {
			this.host_name = host_name;
			this.room_name = room_name;
			numberOfPlayers = 0;
			maxNumberOfPlayers = 1;
			radius = 30;
		}
	}
	
	// Send a Json event to given member
	public void sendJSON(String username, ObjectNode event) {

		ArrayNode m = event.putArray("members");
		for(String u: members.keySet()) {
			m.add(u);
		}


		members.get(username).write(event);        
	}
}