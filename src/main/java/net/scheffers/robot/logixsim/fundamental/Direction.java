package net.scheffers.robot.logixsim.fundamental;

public enum Direction {
	INPUT	(true,	false),
	OUTPUT	(false,	true),
	INOUT	(true,	true);
	
	public boolean isInput, isOutput;
	Direction(boolean isInput, boolean isOutput) {
		this.isInput	= isInput;
		this.isOutput	= isOutput;
	}
}
