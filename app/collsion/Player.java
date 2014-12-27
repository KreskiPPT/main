package collsion;

import java.util.ArrayList;

public class Player {
  public ArrayList<Rectangle> boxes;
  public ArrayList<Point> points;
  public ArrayList<Arc> arc;
  private double radius;

  Player(double radius) {
    points = new ArrayList<Point>();
    boxes = new ArrayList<Rectangle>();
    arc = new ArrayList<Arc>();
    this.radius = radius;
  }

  void addPoint(Point point) {
    this.points.add(point);

    if(points.size() > 1) {
      
      Point point2 = points.get(points.size()-2);
            
      if(points.size() % 2 == 1) {
        Point point0 = points.get(points.size()-3);
        Arc arctmp = Intersection.find_arc(point0, new Line(point2.x,point2.y,point.x,point.y), radius);
        arc.add(arctmp);
        boxArc(arctmp);
      }
      else {
    	boxes.add(new Rectangle(point2,point));  
      }
    }
  }
  
  public void boxArc(Arc arc) {
	  double x1 = arc.x - arc.r;
	  double y1 = arc.y - arc.r;
	  double x2 = arc.x + arc.r;
	  double y2 = arc.y + arc.r;
	  
	  boxes.add(new Rectangle(new Point(x1,x2), new Point(x2,y2)));  
  }

  public Rectangle getLastBox() {
    return boxes.get(boxes.size()-1);
  }
  
  public int getNumberOfPoint() {
    return points.size();
  }
  
  public Point getPoint(int index) {
    return points.get(index);
  }
  
  public Arc getArc(int index) {
    return arc.get(index);
  }
  
  public int getNumberOfArc() {
    return arc.size();
  }
  
  public Point getLastPoint() {
    return points.get(points.size()-1);
  }
  
  public Point getSecondToLastPoint() {
    return points.get(points.size()-2);
  }

  public ArrayList<Rectangle> getBoxes() {
    return boxes;
  }

  public ArrayList<Point> getPoints() {
    return points;
  } 
}
