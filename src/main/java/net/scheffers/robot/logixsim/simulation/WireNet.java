package net.scheffers.robot.logixsim.simulation;

import java.util.List;

public class WireNet {
	
	/** The current value that is on this wire net. */
	boolean currentValue;
	
	/** The pins with output capability on this network. */
	public List<SimPin> outputs;
	/** The pins with input capability on this network. */
	public List<SimPin> inputs;
	
}
