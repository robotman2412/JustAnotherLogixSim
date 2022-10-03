package net.scheffers.robot.logixsim.simulation;

import net.scheffers.robot.logixsim.components.SimComponent;

import java.awt.*;
import java.awt.geom.AffineTransform;

/** A pin of a component, input, output or both. */
public class SimPin {
	
	/** The wire net that this pin is attached to. */
	public WireNet net;
	/** The location relative to a component's origin that this pin is on. */
	public int x, y;
	
	/** A pin of a component, input, output or both. */
	public SimPin() {}
	
	/** A pin of a component, input, output or both. */
	public SimPin(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	/** Draw this pin as part of a given component. */
	public void draw(SimComponent component, Graphics2D g) {
		AffineTransform pre = g.getTransform();
		g.translate(x * 10, y * 10);
		
		g.setTransform(pre);
	}
	
}
