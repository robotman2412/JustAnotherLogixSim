package net.scheffers.robot.logixsim.fundamental;

import net.scheffers.robot.logixsim.Simulation;

import java.awt.*;
import java.awt.geom.Line2D;

/** The wire class is almost entirely handled by Simulation. */
public class Wire extends SimComponent implements WireAttachable {
	
	/** The net associated with this wire. */
	WireNet net;
	/** The starting point of this wire. */
	public Point start;
	/** The end point of this wire. */
	public Point end;
	
	/** The wire class is almost entirely handled by Simulation. */
	public Wire(Simulation parent) {
		super(parent, 0, 0, 1, 1);
		start		= new Point(0, 0);
		end			= new Point(0, 0);
		net			= new WireNet();
		net.wires.add(this);
	}
	
	/** The wire class is almost entirely handled by Simulation. */
	public Wire(Simulation parent, Point start, Point end) {
		super(parent, 0, 0, 1, 1);
		this.start	= start.getLocation();
		this.end	= end.getLocation();
		net			= new WireNet();
		net.wires.add(this);
		calculateBounds();
	}
	
	/** Draw this wire. */
	@Override
	public void drawInternal(Graphics2D g, boolean asGhost) {
		g.translate(-x * 10, -y * 10);
		if (asGhost) {
			g.setColor(Color.GRAY);
		} else if (selected) {
			g.setColor(Color.CYAN);
		} else {
			g.setColor(net.getColor());
		}
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
		x		= x0;
		y		= y0;
		width	= x1 - x0;
		height	= y1 - y0;
	}
	
	/** A point to point square distance. */
	protected static float sqrDist(float x0, float y0, float x1, float y1) {
		float dx = x1 - x0;
		float dy = y1 - y0;
		return dx * dx + dy * dy;
	}
	
	/** Whether a point is on this wire. */
	@Override
	public boolean contains(float x, float y) {
		float limit    = 0.25f;
		float sqrLimit = limit * limit;
		
		// Initial box check.
		if (x < this.x - limit || x > this.x + height || y < this.y || y > this.y + height) {
			return false;
		}
		
		// Vertical check.
		if (this.width == 0) {
			// Smooth the edges.
			if (y < this.y) return sqrDist(x, y, this.x, this.y) < sqrLimit;
			if (y > this.y + this.height) return sqrDist(x, y, this.x, this.y + this.height) < sqrLimit;
			
			// It is in the middle.
			return Math.abs(x - this.x) < limit;
		}
		
		// Line tangent based distance check.
		float dy	= y - this.y;
		float dx	= x - this.x;
		float rc	= dy / dx;
		float trc	= -1 / rc;
		
		// Calculate intersection.
		float ix    =
	}
	
	/** Connect the wire to another wire. */
	public void connect(Wire other) {
		net.merge(other.net);
	}
	
	/** Connect the wire to a pin. */
	public void connect(Pin pin) {
		if (pin.net != null) {
			net.merge(pin.net);
		} else {
			net.connect(pin);
			pin.net = net;
		}
	}
	
	/**
	 * Called when a wire is attached to this object at the given point.
	 * May modify the point's location.
	 */
	@Override
	public void attachWire(Wire wire, Point at) {
		connect(wire);
	}
	
	/** Called when a WireAttachable is joined with another, which is not necessarily a Wire. */
	@Override
	public void join(WireAttachable other) {
		if (other instanceof Wire) {
			Wire wire = (Wire) other;
			if (net != wire.net) connect(wire);
		} else if (other instanceof Pin) {
			Pin pin = (Pin) other;
			if (net != pin.net) connect(pin);
		}
	}
	
	/** Gets the closest valid attachment point. */
	@Override
	public Point getClosestAttachable(float x, float y) {
		// TODO: Update this to support bisecting the line.
		return start.distanceSq(x, y) < end.distanceSq(x, y) ? start : end;
	}
	
	/** Obtain a copy of this component for placing on the canvas. */
	@Override
	public SimComponent getCopy(Simulation to, int x, int y) {
		return null;
	}
	
}
