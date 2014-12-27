package collsion;

import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Collision {

  public ArrayList<String> players_id;
  public Map<String, Player> players;
  private double radius;

  public void addPlayer(String player) {
    players.put(player, new Player(radius));
    players_id.add(player);
  }

  public void addPoint(Point point, String player_id) {
    Player tmpplayer = players.get(player_id);
    if(tmpplayer != null)
      tmpplayer.addPoint(point);
  }

  public Collision(double radius) {
    players = new HashMap<String, Player>();
    players_id = new ArrayList<String>();
    this.radius = radius;
  }

  public Map<String, Player> getPlayer() {
    return players;
  }
  
  public boolean checkCollision(String player_id, Map<String, collsion.Point> players) {
	  for(Map.Entry<String, Point> entry: players.entrySet()) {
		  addPoint(entry.getValue(), entry.getKey());
	  }
	  
	  boolean collision = ifCollision(player_id);
	  
	  for(Map.Entry<String, Point> entry: players.entrySet()) {
		  addPoint(entry.getValue(), entry.getKey());
	  }
	  
	  return collision;
  }
  
  public void init(Map<String, collsion.Point> players) {
	  for(Map.Entry<String, Point> entry: players.entrySet()) {
		  addPoint(entry.getValue(), entry.getKey());
	  }
  }

  public boolean ifCollision(String player_id) {

    Player player = players.get(player_id);
    Rectangle box = player.getLastBox();

    Point sp = player.getSecondToLastPoint();
    Point ep = player.getLastPoint();

    boolean isline = (player.getNumberOfPoint()%2 == 0);

    for(int i=0; i < players_id.size(); i++) {
      player = players.get(players_id.get(i));
      ArrayList<Rectangle> boxes = player.getBoxes();

      int box_to_check = boxes.size();
      if(players_id.get(i).equals(player_id))
        box_to_check--;

      for(int j=0; j < box_to_check; j++) {

        if(box.x2 > boxes.get(j).x1 && 
            box.x1 < boxes.get(j).x2 && 
            box.y2 > boxes.get(j).y1 && 
            box.y1 < boxes.get(j).y2) {

          Point check_sp = player.getPoint(j);
          Point check_ep = player.getPoint(j+1);
          boolean isline2 = (j%2 == 0);

          if(isline && isline2) {
            if(Line2D.linesIntersect(check_sp.x, check_sp.y, 
                check_ep.x, check_ep.y,
                sp.x, sp.y, ep.x, ep.y)) {

              return true;
            }
          }
          else if(isline || isline2) {
            if(isline) {
              
              if(Intersection.arc_line_intersection(player.getArc(j/2), 
                  new Line(sp.x, sp.y, ep.x, ep.y)))
                return true;
            }
            else {
              Arc arc = player.getArc( player.getNumberOfArc()-1 );
              
              if(Intersection.arc_line_intersection(player.getArc( player.getNumberOfArc()-1 ), 
                  new Line(check_sp.x, check_sp.y, check_ep.x, check_ep.y)))
                return true;
            }
          }
          else {
            
            if(Intersection.arc_arc_intersection(player.getArc( player.getNumberOfArc()-1 ), player.getArc(j/2)))
              return true;
          }
        }
      }
    }

    return false;
  }
}
