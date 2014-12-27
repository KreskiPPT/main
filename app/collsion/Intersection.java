package collsion;

public class Intersection {

	private static boolean inAngle(double cx, double cy, double radius, double x, double y, double arcangle, double extent) {
		double dx = x - cx;
		double dy = y - cy;

		double anglesin = Math.toDegrees(Math.asin(dy/radius));
		double anglecos = Math.toDegrees(Math.acos(dx/radius));
		double angle = anglecos;

		if(anglesin<0)
			angle+=180;

		double exangle = arcangle + extent;
		double endangle = 0;
		if(exangle > 360)
			endangle = exangle - 360;
		else if(exangle < 0)
			endangle = exangle + 360;

		if(angle > arcangle && exangle > angle) {
			return true;
		}
		else if(angle < arcangle && exangle < angle) {
			return true;
		}
		else if(endangle != 0 && angle < arcangle && endangle > angle) {
			return true;
		}
		else if(endangle != 0 && angle > arcangle && endangle < angle) {
			return true;
		}

		return false;
	}

	public static boolean arc_line_intersection(double cx, double cy, double r, double arcangle, double extent, 
			double x1, double y1, double x2, double y2) {

		if(x1 > x2) {
			double tmp = x1;
			x1 = x2;
			x2 = tmp;

			tmp = y1;
			y1 = y2;
			y2 = tmp;
		}

		x1 -= cx; x2 -= cx;
		y1 -= cy; y2 -= cy;

		double dx = x2 - x1;
		double dy = y2 - y1;
		double dr = Math.sqrt(Math.pow(dx,2) + Math.pow(dy,2));
		double D = x1*y2 - x2*y1;

		double delta = Math.pow(r,2)*Math.pow(dr,2) - Math.pow(D,2);

		if(y1 > y2) {
			double tmp = y1;
			y1 = y2;
			y2 = tmp;
		}

		if(delta >= 0) {
			delta = Math.sqrt(delta);
			double x = (D*dy + sgn(dy) * dx * delta) / Math.pow(dr,2);

			if(x > x1 && x < x2) {
				double y = (-D*dx + Math.abs(dy) * delta) / Math.pow(dr,2);

				if(y > y1 && y < y2) {
					if(inAngle(cx, cy, r, x+cx, y+cy, arcangle, extent))
						return true;
				}
			}

			x = (D*dy - sgn(dy) * dx * delta) / Math.pow(dr,2);

			if(x > x1 && x < x2) {
				double y = (-D*dx - Math.abs(dy) * delta) / Math.pow(dr,2); 

				if(y > y1 && y < y2) {

					if(inAngle(cx, cy, r, x+cx, y+cy, arcangle, extent))
						return true;
				}


			}
		}

		return false;
	}

	public static boolean arc_line_intersection(Arc circle, Line line) {
		return arc_line_intersection(circle.x, circle.y, circle.r, circle.start_angle, circle.extent, line.x1, line.y1, line.x2, line.y2);
	}

	public static boolean arc_arc_intersection(double x1, double y1, double r1, double angle1, double extent1,
			double x2, double y2, double r2, double angle2, double extent2) {

		double d = Math.hypot(x1-x2, y1-y2);

		double dx = Math.abs(x1 - x2);
		double dy = Math.abs(y1 - y2);

		if(d > r1 + r2)
			return false;
		if(d < Math.abs(r1-r2))
			return false;
		if(d == 0)
			return true;

		double a = (Math.pow(r1,2) - Math.pow(r2, 2) + Math.pow(d, 2)) / (2*d);
		double h = Math.sqrt(Math.pow(r1,2)-Math.pow(a, 2));

		double x3 = x1 + a*dx/d;
		double y3 = y1 + a*dy/d;

		double offsets_x = h*dy/d;
		double offsets_y = -h*dx/d;

		double x4 = x3 + offsets_x;
		double y4 = y3 + offsets_y;

		if(inAngle(x1, y1, r1, x4, y4, angle1, extent1) 
				&& inAngle(x2, y2, r2, x4, y4, angle2, extent2))
			return true;

		x4 = x3 - offsets_x;
		y4 = y3 - offsets_y;

		if(inAngle(x1, y1, r1, x4, y4, angle1, extent1) 
				&& inAngle(x2, y2, r2, x4, y4, angle2, extent2))
			return true;

		return false;
	}

	public static boolean arc_arc_intersection(Arc arc, Arc arc2) {
		return arc_arc_intersection(arc.x, arc.y, arc.r, arc.start_angle, arc.extent,
				arc2.x, arc2.y, arc2.r, arc2.start_angle, arc2.extent);
	}

	private static double sgn(double value) {
		if(value < 0)
			return -1;
		else
			return 1;
	}

	static public Arc find_arc(Point point, Line line, double R) {
		return find_arc(point.x, point.y, line.x1, line.y1, line.x2, line.y2, R);
	}

	static  public Arc find_arc(double x0, double y0, double x1, double y1, double x2, double y2, double R) {
		double xs = (x1+x2)/2;
		double ys = (y1+y2)/2;

		double xv = xs - x1;
		double yv = ys - y1;

		double a = (1 + Math.pow(xv, 2)/Math.pow(yv, 2));
		double c = Math.pow(xv, 2) + Math.pow(yv, 2) - Math.pow(R,2);

		double delta = - 4*a*c;
		double sqrtdelta = Math.sqrt(delta);

		double xv2 = sqrtdelta / (2*a);
		double yv2 = (-xv*xv2)/yv;

		double xc, yc;

		xc = x1 + xv + xv2;
		yc = y1 + yv + yv2;

		double vector_x1_xc = xc - x1;
		double vector_y1_yc = yc - y1;

		double vector_x0_x1 = x1 - x0;
		double vector_y0_y1 = y1 - y0;

		if( !(Math.abs(vector_x0_x1*vector_x1_xc + vector_y0_y1*vector_y1_yc) < 0.0001) ) {
			xv2 = -xv2;
			yv2 = -yv2;

			xc = x1 + xv + xv2;
			yc = y1 + yv + yv2;
		}

		double vxc_x1 = x1 - xc;
		double vxc_x2 = x2 - xc;
		double additional_start_angle = 0;
		double additional_end_angle = 0;

		if(y1 - yc < -0.0001) {
			additional_start_angle = 180;
		}
		if(y2 - yc < -0.0001) {
			additional_end_angle = 180;
		}

		boolean counterclock;

		vector_x1_xc = xc - x1;
		vector_y1_yc = yc - y1;

		if(vector_x0_x1 < 0) {
			if(vector_y0_y1 < 0) {
				if(vector_x1_xc > 0) {
					counterclock = true;
				}
				else {
					counterclock = false;
				}
			}
			else {
				if(vector_y1_yc < 0) {
					counterclock = true;
				}
				else {
					counterclock = false;
				}
			}
		}
		else {
			if(vector_y0_y1 < 0) {
				if(vector_x1_xc > 0) {
					counterclock = true;
				}
				else {
					counterclock = false;
				}
			}
			else {
				if(vector_x1_xc < 0) {
					counterclock = true;
				}
				else {
					counterclock = false;
				}
			}
		}

		double start_angle = additional_start_angle + Math.toDegrees(Math.acos(vxc_x1/R));
		double end_angle = additional_end_angle + Math.toDegrees(Math.acos(vxc_x2/R));

		double extent = end_angle-start_angle;

		if(counterclock) {
			if(extent < 0)
				extent = 360 + extent;
		}
		else {
			if(extent > 0)
				extent = extent - 360;
		}        

		return new Arc(xc,yc,R,start_angle,extent);
	}
}