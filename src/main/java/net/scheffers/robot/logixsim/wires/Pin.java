package net.scheffers.robot.logixsim.wires;

import net.scheffers.robot.logixsim.components.SimComponent;

import java.awt.*;
import java.awt.geom.AffineTransform;

/** A pin of a component, input, output or both. */
public class Pin {
	
	/** The direction in which this pin operates. */
	private Direction direction;
	/** Whether to drive high on the wire net. */
	private boolean value;
	/** The wire net that this pin is attached to. */
	WireNet net;
	/** The location relative to a component's origin that this pin is on. */
	public int x, y;
	/** The callback to run when the received value changes. */
	public Runnable onChange;
	
	/** A pin of a component, input, output or both. */
	public Pin(int x, int y, Direction direction) {
		this.x = x;
		this.y = y;
		this.direction = direction;
	}
	
	/** Draw this pin as part of a given component. */
	public void draw(SimComponent component, Graphics2D g) {
		AffineTransform pre = g.getTransform();
		g.translate(x * 10, y * 10);
		
		g.setColor(getColor());
		g.fillOval(-2, -2, 4, 4);
		
		g.setTransform(pre);
	}
	
	/** Update the direction of this pin and tell the net. */
	public void setDirection(Direction direction) {
		Direction old = this.direction;
		this.direction = direction;
		if (!direction.isOutput) value = false;
		if (net != null) {
			net.directionChanged(this, direction);
		}
	}
	
	/** Get the direction of this pin. */
	public Direction getDirection() {
		return direction;
	}
	
	/** Get whether this pin is driving high on the net. */
	public boolean getDrivenValue() {
		if (!direction.isOutput) throw new IllegalStateException("getValue() on an input pin");
		return value;
	}
	
	/** Gets the value on the wire net, also acceptable for output pins. */
	public boolean getNetValue() {
		return net == null ? value : net.getValue();
	}
	
	/** Set the value of this pin. */
	public void setDrivenValue(boolean value) {
		this.value = value;
	}
	
	/** Gets the location of this pin. */
	public Point getPosition() {
		return new Point(x, y);
	}
	
	/** Gets the recommended color for this pin based on value. */
	public Color getColor() {
		if (value || direction.isInput && getNetValue()) {
			return Color.GREEN;
		} else {
			return new Color(0, 64, 0);
		}
	}
	
	/** Determine whether a point is near enough for starting wire dragging. */
	public boolean isNear(SimComponent relativeTo, float x, float y) {
		float max = 0.8f;
		float dx = relativeTo.x + this.x - x;
		float dy = relativeTo.y + this.y - y;
		return (dx * dx + dy * dy) < (max * max);
	}
	
}
