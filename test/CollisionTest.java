import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.*;

import collsion.*;

public class CollisionTest {

	Collision collision;
	ArrayList<Point> points;

	@Before
	public void Init() {
		points = new ArrayList<Point>();
		collision = new Collision(30);
	}

	@Test
	public void circle_line_intersection_test() {
		assertFalse(Intersection.arc_line_intersection(new Arc(0, 0, 5, 0, 360), 
				new Line(10, 10, 50, 50)));
		assertTrue(Intersection.arc_line_intersection(new Arc(0, 0, 20, 0, 360), 
				new Line(0, 0, -50, 50)));
		assertTrue(Intersection.arc_line_intersection(new Arc(0, 0, 20, 120, -350), 
				new Line(0, 0, -50, 50)));
		assertTrue(Intersection.arc_line_intersection(new Arc(0, 0, 20, 120, 20), 
				new Line(0, 0, -50, 50)));
		assertTrue(Intersection.arc_line_intersection(new Arc(0, 0, 20, 150, 350), 
				new Line(0, 0, -50, 50)));
		assertTrue(Intersection.arc_line_intersection(new Arc(0, 0, 20, 140, -20), 
				new Line(0, 0, -50, 50)));
		assertFalse(Intersection.arc_line_intersection(new Arc(0, 0, 20, 120, -340), 
				new Line(0, 0, -50, 50)));
		assertFalse(Intersection.arc_line_intersection(new Arc(0, 0, 20, 120, 10), 
				new Line(0, 0, -50, 50)));
		assertFalse(Intersection.arc_line_intersection(new Arc(0, 0, 20, 150, 340), 
				new Line(0, 0, -50, 50)));
		assertFalse(Intersection.arc_line_intersection(new Arc(0, 0, 20, 150, -10), 
				new Line(0, 0, -50, 50)));
	}

	@Test
	public void circle_circle_intersection_test() {
		assertTrue(Intersection.arc_arc_intersection(new Arc(0, 0, 10, 0, 360), 
				new Arc(15, 0, 10, 0, 360)));

		assertTrue(Intersection.arc_arc_intersection(new Arc(50, 50, 10, 0, 360), 
				new Arc(60, 50, 10, 0, 360)));

		assertFalse(Intersection.arc_arc_intersection(new Arc(0, 0, 10, 0, 10), 
				new Arc(15, 0, 10, 0, 10)));

		assertFalse(Intersection.arc_arc_intersection(new Arc(50, 50, 10, 0, 10), 
				new Arc(60, 50, 10, 0, 10)));
	}

	@Test
	public void find_range_test() {
		Arc arc;
		double epsilon = 0.0001;
		double epsilon_extent = 1;

		arc = Intersection.find_arc(new Point(0,-10), new Line(0,0,10,10),10);
		assertEquals(10, arc.x, epsilon);
		assertEquals(0, arc.y, epsilon);
		assertEquals(180, arc.start_angle, epsilon);
		assertEquals(-90, arc.extent, epsilon);

		arc = Intersection.find_arc(new Point(0,-10), new Line(0, 0, 3, 7.1414284285428499979993998113673), 10);
		assertEquals(10, arc.x, epsilon);
		assertEquals(0, arc.y, epsilon);
		assertEquals(180, arc.start_angle, epsilon);
		assertEquals(-45, arc.extent, epsilon_extent);

		arc = Intersection.find_arc(new Point(20, 10), new Line(10,10,3,7.1414284285428499979993998113673), 10);

		assertEquals(10, arc.x, epsilon);
		assertEquals(0, arc.y, epsilon);
		assertEquals(90, arc.start_angle, epsilon);
		assertEquals(45, arc.extent, epsilon_extent);

		arc = Intersection.find_arc(new Point(-10, 0), new Line(0,0,10,-10), 10);
		assertEquals(0, arc.x, epsilon);
		assertEquals(-10, arc.y, epsilon);
		assertEquals(90, arc.start_angle, epsilon);
		assertEquals(-90, arc.extent, epsilon_extent);

		arc = Intersection.find_arc(new Point(-10, 0), new Line(0,0,0,-20), 10);
		assertEquals(0, arc.x, epsilon);
		assertEquals(-10, arc.y, epsilon);
		assertEquals(90, arc.start_angle, epsilon);
		assertEquals(-180, arc.extent, epsilon_extent);

		arc = Intersection.find_arc(new Point(50, 50), new Line(50, 60, 60, 70), 10);
		assertEquals(60, arc.x, epsilon);
		assertEquals(60, arc.y, epsilon);
		assertEquals(180, arc.start_angle, epsilon);
		assertEquals(-90, arc.extent, epsilon_extent);

		arc = Intersection.find_arc(new Point(20, -10), new Line(10, -10, 10, 10), 10);
		assertEquals(10, arc.x, epsilon);
		assertEquals(0, arc.y, epsilon);
		assertEquals(270, arc.start_angle, epsilon);
		assertEquals(-180, arc.extent, epsilon_extent);

		//arc = Intersection.find_arc(new Point(20, -10), new Line(10, -10, 10, -30), 10);

		arc = Intersection.find_arc(new Point(0,-10), new Line(0, 0, -10, 10), 10);
		assertEquals(-10, arc.x, epsilon);
		assertEquals(0, arc.y, epsilon);
		assertEquals(0, arc.start_angle, epsilon);
		assertEquals(90, arc.extent, epsilon_extent);
	}

	@After
	public void Clean() {
		points = null;
		collision = null;
	}
}
