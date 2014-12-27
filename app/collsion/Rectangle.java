package collsion;

public class Rectangle extends Shape {
  public double x1,y1,x2,y2;

  public Rectangle(double x1, double y1, double x2, double y2) {
    double tmp;

    if(x1 > x2) {
      tmp = x1;
      x1 = x2;
      x2 = tmp;
    }
    if(y1 > y2) {
      tmp = y1;
      y1 = y2;
      y2 = tmp;
    }

    this.x1 = x1;
    this.y1 = y1;
    this.x2 = x2;
    this.y2 = y2;
  }

  public Rectangle(Point point1, Point point2) {
    double tmp;
    
    Point newPoint1 = new Point(point1.x,point1.y);
    Point newPoint2 = new Point(point2.x,point2.y);

    if(newPoint1.x > newPoint2.x) {
      tmp = newPoint1.x;
      newPoint1.x = newPoint2.x;
      newPoint2.x = tmp;
    }
    if(newPoint1.y > newPoint2.y) {
      tmp = newPoint1.y;
      newPoint1.y = newPoint2.y;
      newPoint2.y = tmp;
    }
    
    this.x1 = newPoint1.x;
    this.y1 = newPoint1.y;
    this.x2 = newPoint2.x;
    this.y2 = newPoint2.y;
  }
}
