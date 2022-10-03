package net.scheffers.robot.logixsim;

import net.scheffers.robot.logixsim.components.SimComponent;

import java.awt.*;
import java.awt.event.MouseEvent;

public class SimComponentButton extends SimComponent {
	
	public boolean pressedLeft, pressedRight;
	
	public SimComponentButton(Simulation parent, int x, int y) {
		super(parent, x, y, 2, 2);
	}
	
	@Override
	protected void drawInternal(Graphics2D g, boolean asGhost) {
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, 20, 20);
		
		if (pressedLeft || pressedRight) {
			g.setColor(Color.LIGHT_GRAY);
		} else {
			g.setColor(Color.WHITE);
		}
		g.fillRect(1, 1, 18, 18);
	}
	
	@Override
	public boolean mouseDown(MouseEvent e) {
		pressedLeft  |= e.getButton() == MouseEvent.BUTTON1;
		pressedRight |= e.getButton() == MouseEvent.BUTTON3;
		return true;
	}
	
	@Override
	public void mouseUp(MouseEvent e) {
		pressedLeft  &= e.getButton() != MouseEvent.BUTTON1;
		pressedRight &= e.getButton() != MouseEvent.BUTTON3;
	}
	
	@Override
	public SimComponent getCopy(Simulation to, int x, int y) {
		return new SimComponentButton(to, x, y);
	}
	
}
