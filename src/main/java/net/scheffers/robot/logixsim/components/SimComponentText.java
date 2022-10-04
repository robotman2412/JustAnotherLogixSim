package net.scheffers.robot.logixsim.components;

import net.scheffers.robot.logixsim.Simulation;
import net.scheffers.robot.logixsim.fundamental.SimComponent;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;

public class SimComponentText extends SimComponent {
	
	protected String text;
	
	public SimComponentText(Simulation parent, int x, int y, String text) {
		super(parent, x, y, 2, 2);
		Rectangle2D dims = Simulation.defaultFont.getStringBounds(text, new FontRenderContext(null, true, false));
		width  = (int) Math.ceil(dims.getWidth() / 10 + 0.2);
		this.text = text;
	}
	
	@Override
	protected void drawInternal(Graphics2D g, boolean asGhost) {
		if (asGhost) g.setColor(new Color(0, 0, 0, 127));
		else g.setColor(Color.BLACK);
		g.drawString(text, 2, 15);
	}
	
	@Override
	public SimComponent getCopy(Simulation to, int x, int y) {
		return new SimComponentText(to, x, y, text);
	}
	
}
