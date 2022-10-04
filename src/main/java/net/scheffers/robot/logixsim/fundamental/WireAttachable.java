package net.scheffers.robot.logixsim.fundamental;

import java.awt.*;

public interface WireAttachable {
	
	/**
	 * Called when a wire is attached to this object at the given point.
	 * May modify the point's location.
	 */
	void attachWire(Wire wire, Point at);
	
	/** Called when a WireAttachable is joined with another, which is not necessarily a Wire. */
	void join(WireAttachable other);
	
	/** Gets the closest valid attachment point. */
	Point getClosestAttachable(float x, float y);
	
}
