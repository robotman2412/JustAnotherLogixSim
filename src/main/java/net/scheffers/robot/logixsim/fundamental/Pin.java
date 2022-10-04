package net.scheffers.robot.logixsim.fundamental;

import java.awt.*;
import java.awt.geom.AffineTransform;

/** A pin of a component, input, output or both. */
public class Pin implements WireAttachable {
	
	/** The direction in which this pin operates. */
	private Direction direction;
	/** Whether to drive high on the wire net. */
	private boolean value;
	/**
	 * The next value to write to this pin.
	 * Used by active components.
	 */
	public boolean nextValue;
	/** The wire net that this pin is attached to. */
	WireNet net;
	/** The location relative to a component's origin that this pin is on. */
	public int x, y;
	/** The callback to run when the received value changes. */
	public Runnable onChange;
	/** The component to which this pin belongs. */
	public final SimComponent parent;
	
	/** A pin of a component, input, output or both. */
	public Pin(SimComponent parent, int x, int y, Direction direction) {
		this.x = x;
		this.y = y;
		this.direction = direction;
		this.parent = parent;
	}
	
	/** Draw this pin. */
	public void draw(Graphics2D g) {
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
		if (net != null) net.calculateValue();
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
	public boolean isNear(float x, float y) {
		float max = 0.8f;
		float dx = parent.x + this.x - x;
		float dy = parent.y + this.y - y;
		return (dx * dx + dy * dy) < (max * max);
	}
	
	/**
	 * Called when a wire is attached to this object at the given point.
	 * May modify the point's location.
	 */
	@Override
	public void attachWire(Wire wire, Point at) {
		at.x = parent.x + x;
		at.y = parent.y + y;
		wire.connect(this);
	}
	
	/** Called when a WireAttachable is joined with another, which is not necessarily a Wire. */
	@Override
	public void join(WireAttachable other) {
		if (other instanceof Wire wire) {
			if (net != wire.net) wire.connect(this);
		} else if (other instanceof Pin pin) {
			if (pin.net != null && pin.net != net) {
				pin.net.connect(this);
				net = pin.net;
			} else if (net != null && pin.net != net) {
				net.connect(pin);
				pin.net = net;
			} else if (net == null) {
				net = new WireNet();
				net.connect(this);
				net.connect(pin);
				pin.net = net;
			}
		}
	}
	
	/** Gets the closest valid attachment point. */
	@Override
	public Point getClosestAttachable(float x, float y) {
		return new Point(this.x + parent.x, this.y + parent.y);
	}
	
}
