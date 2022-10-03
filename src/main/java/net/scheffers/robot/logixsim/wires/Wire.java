package net.scheffers.robot.logixsim.wires;

import java.awt.*;

/** The wire class is almost entirely handled by Simulation. */
public class Wire {
	
	/** The net associated with this wire. */
	WireNet net;
	/** The starting point of this wire. */
	public Point start;
	/** The end point of this wire. */
	public Point end;
	/** The bounding box of this wire. */
	public Rectangle boundingBox;
	
	/** The wire class is almost entirely handled by Simulation. */
	public Wire() {
		start		= new Point(0, 0);
		end			= new Point(0, 0);
		net			= new WireNet();
		boundingBox	= new Rectangle();
	}
	
	/** Draw this wire. */
	public void draw(Graphics2D g) {
		g.setColor(net.getColor());
		g.drawLine(start.x * 10, start.y * 10, end.x * 10, end.y * 10);
	}
	
	/** Whether a point is on this wire. */
	public boolean contains(Point pos) {
		return contains(pos.x, pos.y);
	}
	
	/** Recalculates the bounding box of this wire. */
	public void calculateBounds() {
		// Sort X.
		int x0, x1;
		if (start.x < end.x) {
			x0 = start.x;
			x1 = end.x;
		} else {
			x0 = end.x;
			x1 = start.x;
		}
		
		// Sort Y.
		int y0, y1;
		if (start.y < end.y) {
			y0 = start.y;
			y1 = end.y;
		} else {
			y0 = end.y;
			y1 = start.y;
		}
		
		// Construct rectangle.
		boundingBox.x		= x0;
		boundingBox.y		= y0;
		boundingBox.width	= x1 - x0;
		boundingBox.height	= y1 - y0;
	}
	
	/** Whether a point is on this wire. */
	public boolean contains(int x, int y) {
		// TODO: Include margin?
		
		// Step 1: Bounding box check.
		if (!boundingBox.contains(x, y)) return false;
		
		// Step 2: Vertical check.
		
		// Step 3: Interpolated check.
		return false;
	}
	
	/** Connect the wire to another wire. */
	public void connect(Wire other) {
		net.merge(other.net);
	}
	
	/** Connect the wire to a pin. */
	public void connect(Pin pin) {
		net.connect(pin);
	}
	
}
