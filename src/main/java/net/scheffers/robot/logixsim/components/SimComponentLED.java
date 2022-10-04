package net.scheffers.robot.logixsim.components;

import net.scheffers.robot.logixsim.Simulation;
import net.scheffers.robot.logixsim.fundamental.Direction;
import net.scheffers.robot.logixsim.fundamental.Pin;
import net.scheffers.robot.logixsim.fundamental.SimComponent;

import java.awt.*;

public class SimComponentLED extends SimComponent {
	
	/** The pin that this LED reads. */
	public Pin inputPin;
	
	public SimComponentLED(Simulation parent, int x, int y) {
		super(parent, x, y, 2, 2);
	}
	
	@Override
	protected void drawInternal(Graphics2D g, boolean asGhost) {
		if (!asGhost && inputPin != null) {
			g.setColor(inputPin.getColor());
			g.fillOval(0, 0, 20, 20);
			g.setColor(Color.BLACK);
		} else {
			g.setColor(Color.GRAY);
		}
		g.drawOval(0, 0, 20, 20);
	}
	
	@Override
	public void onAdded() {
		inputPin = addPin(0, 1, Direction.INPUT);
		inputPin.onChange = () -> parent.dirty = true;
	}
	
	@Override
	public SimComponent getCopy(Simulation to, int x, int y) {
		return new SimComponentLED(to, x, y);
	}
	
}
