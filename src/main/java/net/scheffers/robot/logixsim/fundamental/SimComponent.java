package net.scheffers.robot.logixsim.fundamental;

import net.scheffers.robot.logixsim.Simulation;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

public abstract class SimComponent {
	
	/** The simulation that this component is part of. */
	public final Simulation parent;
	
	/** Grid coordinate position. */
	public int x, y;
	/** Grid coordinate dimensions. */
	public int width, height;
	/** Whether this component is visible, determined by SimCanvas. */
	public boolean visible;
	/** Whether component is selected. */
	public boolean selected;
	/** Whether this component has received mousedown and should therefore receive mouseup. */
	public boolean mouseLeftDown, mouseRightDown;
	
	/** The pins on this component. */
	private final List<Pin> pins;
	
	/**
	 * The basic component constructor.
	 * Subclasses may override X and Y with rounding or other transformations.
	 */
	public SimComponent(Simulation parent, int x, int y) {
		this.parent		= parent;
		this.x			= x;
		this.y			= y;
		this.width		= 1;
		this.height		= 1;
		this.visible	= true;
		pins = new ArrayList<>();
	}
	
	/**
	 * The basic component constructor.
	 * Subclasses may override X and Y with rounding or other transformations.
	 */
	public SimComponent(Simulation parent, int x, int y, int width, int height) {
		this.parent		= parent;
		this.x			= x;
		this.y			= y;
		this.width		= width;
		this.height		= height;
		this.visible	= true;
		pins = new ArrayList<>();
	}
	
	/** Draws the component given graphics context. */
	public final void draw(Graphics2D g, boolean asGhost) {
		AffineTransform pre = g.getTransform();
		g.translate(x * 10, y * 10);
		
		g.setColor(Color.BLACK);
		drawInternal(g, asGhost);
		
		if (!(this instanceof Wire)) {
			g.setColor(Color.CYAN);
			if (selected) {
				g.drawRoundRect(0, 0, width * 10, height * 10, 5, 5);
			}
			
			pins.forEach(pin -> pin.draw(g));
		}
		
		g.setTransform(pre);
	}
	
	/** Internal drawing method, origin is the origin of the component and coordinates are grid divided by 10. */
	protected abstract void drawInternal(Graphics2D g, boolean asGhost);
	
	
	/** Obtain a copy of this component for placing on the canvas. */
	public abstract SimComponent getCopy(Simulation to, int x, int y);
	/** Called by Simulation when the component is newly placed. */
	public void onAdded() {}
	/** Called by Simulation just before the component gets removed. */
	public void onRemoved() {}
	
	/**
	 * Called when the mouse is pressed on this component.
	 * Returns whether a selection event should be cancelled.
	 */
	public boolean mouseDown(MouseEvent e) { return false; }
	/** Called when the mouse is released on this component. */
	public void mouseUp(MouseEvent e) {}
	/** Called when the mouse is clicked on this component without moving the mouse too far. */
	public void mouseClick(MouseEvent e) {}
	
	
	/** Adds a pin to this component. */
	protected Pin addPin(int x, int y, Direction direction) {
		Pin pin = new Pin(this, x, y, direction);
		pins.add(pin);
		parent.joinWirePoints(this.x + x, this.y + y);
		return pin;
	}
	
	/** Gets an iterator for the pins list. */
	protected Iterable<Pin> iteratePins() {
		//noinspection FunctionalExpressionCanBeFolded
		return pins::iterator;
	}
	
	/** Removes a pin from the component. */
	protected void removePin(Pin pin) {
		if (pin.net != null) pin.net.remove(pin);
		pins.remove(pin);
	}
	
	
	/** Returns whether the given point is contained in this component's bounding box. */
	public boolean contains(float x, float y) {
		return this.x <= x && this.x + this.width >= x && this.y <= y && this.y + this.height >= y;
	}
	
	/** Finds a pin with given absolute coordinates. */
	public Pin findPin(int x, int y) {
		x -= this.x;
		y -= this.y;
		for (Pin pin : pins) {
			if (pin.x == x && pin.y == y) {
				return pin;
			}
		}
		return null;
	}
	
}
