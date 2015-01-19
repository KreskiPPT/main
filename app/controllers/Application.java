package controllers;

import static akka.pattern.Patterns.ask;

import java.util.HashMap;
import java.util.Map;

import play.api.mvc.Session;
import play.mvc.*;

import com.fasterxml.jackson.databind.JsonNode; 

import views.html.*;
import models.*;

public class Application extends Controller {

	static Map<String, String> roomhost = new HashMap<String, String>();
	
	/**
	 * Display the home page.
	 */
	public static Result index() {
		String username = session("username");

		if(username == null)
			return ok(index.render());
		else
			return redirect(routes.Application.lobby());
	}

	public static Result loginSubmit() {
		final Map<String, String[]> values = request().body().asFormUrlEncoded();

		String username = values.get("username")[0];

		if(username == null || username.trim().equals("")) {
			flash("error", "Please choose a valid username.");
			return redirect(routes.Application.index());
		}
		else if(Lobby.isNameUsed(username)) {
			flash("error", "This username is already used.");
			return redirect(routes.Application.index());
		}

		session("username", username);
		session("state", "lobby");

		return redirect(routes.Application.lobby());
	}

	public static Result disconnect() {
		String username = session("username");
		String roomname = session("roomname");
		String state = session("state");
		
		Lobby.disconnect(session("username"));
		if(state.equals("gameRoom")) {
			GameRoom.defaultRoom.tell(new Messages.Leave(username, roomname), null);
		}
		else if(state.equals("game")) {
			Game.defaultRoom.tell(new Messages.Leave(username, roomname), null);
		}
		
		session().clear();

		return redirect(routes.Application.index());
	}

	/**
	 * Display the chat room.
	 */
	public static Result lobby() {
		String username = session("username");

		return ok(lobby.render(username));
	}

	public static Result reLobby() {
		String username = session("username");
		String roomname = session("roomname");
		String state = session("state");
		
		if(state.equals("gameRoom")) {
			GameRoom.defaultRoom.tell(new Messages.Leave(username, roomname), null);
		}
		else if(state.equals("game")) {
			Game.defaultRoom.tell(new Messages.Leave(username, roomname), null);
		}
		
		if(roomhost.get(roomname) != null)
		  if(roomhost.get(roomname).equals(username))
		    roomhost.remove(roomname);

		return redirect(routes.Application.lobby());
	}

	public static Result lobbyJs(String username) {
		return ok(views.js.lobby.render(username));
	}

	public static Result gameRoom() {
		final Map<String, String[]> values = request().body().asFormUrlEncoded();
		String game = values.get("game")[0];
		String host = "";

		String username = session("username");
		//ChatRoom.defaultRoom.tell(new Messages.DisconnectWS(username), null);
		if(game.equals("Create_Game")) {
			GameRoom.game_list.put("test room", new GameRoom.GameRoomData("test room",username));
			GameRoom.defaultRoom.tell(new Messages.CreateGame(username, "test room"), null);
			session("roomname", "test room");
			host = username;
			roomhost.put("test room", host);
		}
		else {
			if(!GameRoom.game_list.containsKey("test room")) {
				flash("error", "Room doesn't exist.");
				return redirect(routes.Application.lobby());
			}
			else if(GameRoom.game_list.get("test room").isFull()) {
				flash("error", "Room is full.");
				return redirect(routes.Application.lobby());	
			}
			else {
				GameRoom.defaultRoom.tell(new Messages.JoinGameRoom(username, "test room"), null);
				session("roomname", "test room");
			}
		}

		session("state", "gameRoom");
		
		return ok(gameRoom.render(username, "test room", host));
	}

	public static Result gameRoomJs(String username, String host) {
		return ok(views.js.gameRoom.render(username, host));
	}

	public static Result game() {
		String username = session("username");
		String roomname = session("roomname");
		if(username ==  roomhost.get(roomname))
		  GameRoom.defaultRoom.tell(new Messages.StartGame(username, roomname), null);

		session("state", "game");
		return ok(game.render(username, roomhost.get(roomname)));
	}

	public static Result gameJs(String username, String host) {
		return ok(views.js.game.render(username, host));
	}

	/**
	 * Handle the chat websocket.
	 */
	public static WebSocket<JsonNode> chat(final String username) {
		return new WebSocket<JsonNode>() {

			// Called when the Websocket Handshake is done.
			public void onReady(WebSocket.In<JsonNode> in, WebSocket.Out<JsonNode> out){

				// Join the chat room.
				try { 
					Lobby.join(username, in, out);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		};
	}

	/**
	 * Handle the chat websocket.
	 */
	public static WebSocket<JsonNode> gameRoomWS(final String username) {
		return new WebSocket<JsonNode>() {

			// Called when the Websocket Handshake is done.
			public void onReady(WebSocket.In<JsonNode> in, WebSocket.Out<JsonNode> out){

				// Join the chat room.
				try { 
					GameRoom.join(username, in, out);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		};
	}

	/**
	 * Handle the chat websocket.
	 */
	public static WebSocket<JsonNode> gameWS(final String username) {
		return new WebSocket<JsonNode>() {

			// Called when the Websocket Handshake is done.
			public void onReady(WebSocket.In<JsonNode> in, WebSocket.Out<JsonNode> out){

				// Join the chat room.
				try { 
					Game.join(username, in, out);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		};
	}

}
