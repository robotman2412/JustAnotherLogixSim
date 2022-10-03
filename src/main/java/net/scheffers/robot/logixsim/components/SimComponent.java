package net.scheffers.robot.logixsim.components;

import net.scheffers.robot.logixsim.Simulation;
import net.scheffers.robot.logixsim.simulation.SimPin;

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
	public List<SimPin> pins;
	
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
		g.setColor(Color.CYAN);
		if (selected) {
			g.drawRoundRect(0, 0, (int) (width * 10), (int) (height * 10), 5, 5);
		}
		
		g.setTransform(pre);
	}
	
	/** Internal drawing method, origin is the origin of the component and coordinates are grid divided by 10. */
	protected abstract void drawInternal(Graphics2D g, boolean asGhost);
	
	
	/**
	 * Obtain a copy of this component for placing on the canvas.
	 * It is up to this method to determine whether to snap to grid or freely place.
	 */
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
	
	
	/** Returns whether the given point is contained in this component's bounding box. */
	public boolean contains(float x, float y) {
		return this.x <= x && this.x + this.width >= x && this.y <= y && this.y + this.height >= y;
	}
	
}
