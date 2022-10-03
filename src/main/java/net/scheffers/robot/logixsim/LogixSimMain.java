package net.scheffers.robot.logixsim;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class LogixSimMain {
	
	public static List<LogixSimMain> instances = new ArrayList<>();
	
	public static void main(String[] args) {
		new LogixSimMain();
	}
	
	/** The main window. */
	public JFrame frame;
	
	/** The main part of the editor: The simulation. */
	public Simulation simulation;
	
	/** The area containing scale controls. */
	public JPanel scalePanel;
	/** The scale description. */
	public JLabel scaleDesc;
	/** The scale text box. */
	public JTextField scaleField;
	
	/** The side panel containing all tools and components. */
	public JPanel toolsPanel;
	public JButton toolTest1;
	public JButton toolTest2;
	
	public LogixSimMain() {
		LogixSimMain thankYouForMakingThisOverComplicated = this;
		instances.add(this);
		try {
			// Create the frame.
			frame = new JFrame("Logix Simulator");
			frame.setSize(600, 400);
			frame.setLocation(1940, 400);
			frame.setLayout(new BorderLayout());
			
			// Core editor.
			frame.add(simulation = new Simulation(this), BorderLayout.CENTER);
			
			// Scale settings.
			frame.add(scalePanel = new JPanel(new FlowLayout(FlowLayout.LEFT)), BorderLayout.SOUTH);
			scalePanel.add(scaleDesc = new JLabel("Scale:"));
			scalePanel.add(scaleField = new JTextField("100%", 5));
			scaleField.addActionListener(e -> {
				// Find the number.
				String text = scaleField.getText().trim();
				if (text.endsWith("%")) text = text.substring(0, text.length() - 1).trim();
				
				try {
					// Parse it?
					float scale = Float.parseFloat(text);
					
					// Update canvas.
					simulation.scaleGridAroundCursor(scale * 0.1f);
					simulation.repaint();
				} catch (NumberFormatException ignored) {
					// TODO: Color it red?
				}
			});
			
			// Tools panel.
			frame.add(toolsPanel = new JPanel(), BorderLayout.WEST);
			toolsPanel.setLayout(new BoxLayout(toolsPanel, BoxLayout.Y_AXIS));
			toolsPanel.add(toolTest1 = new JButton("Button"));
			toolsPanel.add(toolTest2 = new JButton("OR Gate"));
			toolsPanel.add(toolTest2 = new JButton("Light"));
			toolTest1.addActionListener(e -> {
				simulation.toPlace = new SimComponentButton(simulation, 0, 0);
			});
			
			// Focus on editor.
			frame.requestFocus();
			
			// Add exit handlers.
			frame.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					instances.remove(thankYouForMakingThisOverComplicated);
					if (instances.isEmpty()) System.exit(0);
				}
			});
			
			// Startup complete!
			frame.setVisible(true);
			
		} catch (Throwable t) {
			t.printStackTrace();
			
			if (t instanceof Error) {
				System.exit(1);
			} else {
				instances.remove(this);
				if (instances.isEmpty()) System.exit(0);
			}
		}
	}
	
}
