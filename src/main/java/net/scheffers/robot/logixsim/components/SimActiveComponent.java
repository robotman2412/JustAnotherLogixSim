package net.scheffers.robot.logixsim.components;

import net.scheffers.robot.logixsim.Simulation;
import net.scheffers.robot.logixsim.fundamental.SimComponent;

/** The fundamentals for active components like logic gates. */
public abstract class SimActiveComponent extends SimComponent {
	
	/** The fundamentals for active components like logic gates. */
	public SimActiveComponent(Simulation parent, int x, int y) {
		super(parent, x, y);
	}
	
	/** The fundamentals for active components like logic gates. */
	public SimActiveComponent(Simulation parent, int x, int y, int width, int height) {
		super(parent, x, y, width, height);
	}
	
	/** Read inputs and calculate new outputs. */
	public abstract void preTick();
	
	/** Sets pins to their next values. */
	public void postTick() {
	
	}
	
}
