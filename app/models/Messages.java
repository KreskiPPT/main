package models;

import java.util.HashMap;
import java.util.Map;

import play.mvc.WebSocket;

import com.fasterxml.jackson.databind.JsonNode;

public class Messages {
	// -- Messages

	public static class Join {

		final String username;
		final WebSocket.Out<JsonNode> channel;

		public Join(String username, WebSocket.Out<JsonNode> channel) {
			this.username = username;
			this.channel = channel;
		}

	}

	public static class IsDisconnected {    
	}

	public static class StartGame {

		final String username;
		final String roomname;

		public StartGame(String username, String roomname) {
			this.username = username;
			this.roomname = roomname;
		}

	}

	public static class Leave {

		final String username;
		final String roomname;

		public Leave(String username, String roomname) {
			this.username = username;
			this.roomname = roomname;
		}

	}

	public static class CreateGame {

		final String username;
		final String room_name;

		public CreateGame(String username, String room_name) {
			this.username = username;
			this.room_name = room_name;
		}

	}

	public static class JoinGameRoom {

		final String username;
		final String room_name;

		public JoinGameRoom(String username, String room_name) {
			this.username = username;
			this.room_name = room_name;
		}

	}

	public static class JoinGame {

		final String username;
		final String room_name;

		public JoinGame(String username, String room_name) {
			this.username = username;
			this.room_name = room_name;
		}

	}

	public static class Refresh {

		final String username;

		public Refresh(String username) {
			this.username = username;;
		}

	}

	public static class GameConfiguration {

		final String username;
		final int maxNumberOfPlayers;
		final double radius;

		public GameConfiguration(String username, int numberOfPlayers, double radius) {
			this.username = username;
			this.maxNumberOfPlayers = numberOfPlayers;
			this.radius = radius;
		}

	}

	public static class Talk {

		final String username;
		final String text;

		public Talk(String username, String text) {
			this.username = username;
			this.text = text;
		}

	}

	public static class Quit {

		final String username;

		public Quit(String username) {
			this.username = username;
		}

	}

	public static class Unknown {

		final String username;
		final String unknownType;

		public Unknown(String username, String unknownType) {
			this.username = username;
			this.unknownType = unknownType;
		}

	}


	//Game Messages

	public static class Point {

		final String username;
		final double x;
		final double y;

		public Point(String username, double x, double y) {
			this.username = username;
			this.x = x;
			this.y = y;
		}

	}

	public static class Collision {

		final String username;
		final Map<String, collsion.Point> players; 

		public Collision(String username, Map<String, collsion.Point> players) {
			this.username = username;
			this.players = players;
		}

	}

	public static class Start {

		final String username;
		final Map<String, collsion.Point> players; 

		public Start(String username, Map<String, collsion.Point> players) {
			this.username = username;
			this.players = players;
		}

	}

}
