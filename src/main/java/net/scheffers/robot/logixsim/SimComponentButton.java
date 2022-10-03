package net.scheffers.robot.logixsim;

import net.scheffers.robot.logixsim.components.SimComponent;
import net.scheffers.robot.logixsim.wires.Direction;
import net.scheffers.robot.logixsim.wires.Pin;

import java.awt.*;
import java.awt.event.MouseEvent;

public class SimComponentButton extends SimComponent {
	
	public boolean pressedLeft, pressedRight;
	public Pin outputPin;
	
	public SimComponentButton(Simulation parent, int x, int y) {
		super(parent, x, y, 2, 2);
	}
	
	@Override
	protected void drawInternal(Graphics2D g, boolean asGhost) {
		g.setColor(Color.BLACK);
		
		if (pressedLeft || pressedRight) {
			g.setColor(Color.LIGHT_GRAY);
		} else {
			g.setColor(Color.WHITE);
		}
		g.fillRect(0, 0, 20, 20);
		
		g.setColor(Color.BLACK);
		g.drawLine(0,  0,  20, 0);
		g.drawLine(20, 0,  20, 20);
		g.drawLine(20, 20, 0,  20);
		g.drawLine(0,  20, 0,  0);
	}
	
	@Override
	public boolean mouseDown(MouseEvent e) {
		pressedLeft  |= e.getButton() == MouseEvent.BUTTON1;
		pressedRight |= e.getButton() == MouseEvent.BUTTON3;
		outputPin.setDrivenValue(pressedLeft || pressedRight);
		return true;
	}
	
	@Override
	public void mouseUp(MouseEvent e) {
		pressedLeft  &= e.getButton() != MouseEvent.BUTTON1;
		pressedRight &= e.getButton() != MouseEvent.BUTTON3;
		outputPin.setDrivenValue(pressedLeft || pressedRight);
	}
	
	@Override
	public void onAdded() {
		outputPin = addPin(2, 1, Direction.OUTPUT);
	}
	
	@Override
	public SimComponent getCopy(Simulation to, int x, int y) {
		return new SimComponentButton(to, x, y);
	}
	
}
