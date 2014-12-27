package collsion;

public class Arc extends Shape {
  public double x,y,r,start_angle,extent;

  public Arc(double x, double y, double r, double start_angle, double extent) {
    this.x = x;
    this.y = y;
    this.r = r;
    this.start_angle = start_angle;
    this.extent = extent;
  }
}
