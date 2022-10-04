package net.scheffers.robot.logixsim;

import net.scheffers.robot.logixsim.fundamental.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class Simulation extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener, KeyListener {
	
	public static Font	defaultFont = new Font("Arial", Font.PLAIN, 2).deriveFont(15f);
	
	/** The current scroll position of the grid. */
	public float	scrollX, scrollY;
	/** The raw mouse position. */
	public int		rawCursorX, rawCursorY;
	/** The on grid mouse position. */
	public float	cursorX, cursorY;
	/** The on grid mouse position at time of mouse button down. */
	public float	cursorStartX, cursorStartY;
	
	/** Pixel spacing of the grid. */
	public float	gridScale = 10;
	/** Minimum allowable grid scale. */
	public float	minScale  = 1;
	/** Maximum allowable grid scale. */
	public float	maxScale  = 100;
	
	/** Whether the mouse is currently panning the grid position. */
	public boolean	isPanning;
	/** Whether the mouse is currently hovering over this canvas. */
	public boolean	isHovered;
	/** Whether a click will be registered when the mouse is released. */
	public boolean	isClickValid;
	/** Whether a selection will be started when the mouse is moved. */
	public boolean	isSelectValid;
	
	/** Current biggest on-screen part of the grid. */
	protected Rectangle2D	gridVisiblePart;
	/** Whether the canvas needs a redrawing. */
	public boolean dirty;
	
	/** All components on the canvas. */
	protected final List<SimComponent>	components;
	
	/** The component to place at the cursor, if any. */
	public SimComponent		toPlace;
	/** The component to drag around, if any. */
	public SimComponent		toDrag;
	/** The wire to drag around, if any. */
	public Wire				wireToDrag;
	/** Whether to drag wire start instead of end. */
	public boolean			dragWireStart;
	/** The connectable point that the cursor is near, if any. */
	public WireAttachable wirable;
	
	/** The parent window for this simulation. */
	public final LogixSimMain parent;
	
	public Simulation(LogixSimMain parent) {
		this.parent = parent;
		
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
		addKeyListener(this);
		
		setDoubleBuffered(true);
		setFocusable(true);
		
		components = new ArrayList<>();
	}
	
	// region rendering
	@Override
	public void paint(Graphics g0) {
		// Boring init stuff.
		if (!(g0 instanceof Graphics2D)) {
			throw new IllegalArgumentException("Only Graphics2D is supported!");
		}
		Graphics2D g = (Graphics2D) g0;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.clearRect(0, 0, g.getClipBounds().width, g.getClipBounds().height);
		
		// Calculate on-screen portion of grid.
		gridVisiblePart = new Rectangle2D.Float(
				scrollX,
				scrollY,
				g.getClipBounds().width  / gridScale,
				g.getClipBounds().height / gridScale
		);
		
		// Draw grid.
		g.setColor(new Color(0, 0, 0, 32));
		drawGrid(g);
		
		// Drawing setup.
		setBackground(Color.WHITE);
		g.setColor(Color.BLACK);
		g.setFont(defaultFont);
		AffineTransform pre = g.getTransform();
		g.scale(gridScale * 0.1, gridScale * 0.1);
		g.translate(-scrollX * 10, -scrollY * 10);
		
		// Main drawing part.
		components.forEach(component -> {
			if (component.visible) component.draw(g, false);
		});
		
		
		// Placing components.
		if (toPlace != null && isHovered) {
			toPlace.x = (int) Math.floor(cursorX);
			toPlace.y = (int) Math.floor(cursorY);
			toPlace.draw(g, true);
		}
		
		// Wire attaching.
		if (wirable != null) {
			Point attachPoint = wirable.getClosestAttachable(cursorX, cursorY);
			g.setColor(Color.BLUE);
			g.drawOval(attachPoint.x * 10 - 5, attachPoint.y * 10 - 5, 10, 10);
		}
		
		// Selection box.
		if (isSelectValid) {
			drawSelection(g);
		}
		
		// Cleanup.
		g.setTransform(pre);
		
		dirty = false;
	}
	
	/** Reevaluates for all components whether they are visible. */
	public void reevaluateAllVisibility() {
		components.forEach(component -> component.visible = isOnScreen(component));
	}
	
	/** Checks whether the bounding box of a component is on screen. */
	public boolean isOnScreen(SimComponent component) {
		return gridVisiblePart.intersects(component.x, component.y, component.width, component.height);
	}
	
	/** Draws the grid lines. */
	public void drawGrid(Graphics2D g) {
		int multiplier;
		
		if (gridScale > 7.5) multiplier = 1;
		else if (gridScale > 5) multiplier = 2;
		else if (gridScale > 2.5) multiplier = 4;
		else multiplier = 10;
		
		for (int y = (int) Math.ceil(gridVisiblePart.getY()); y < gridVisiblePart.getMaxY(); y++) {
			if (y % multiplier == 0)
				g.drawRect(0, (int) ((y - scrollY) * gridScale), g.getClipBounds().width, 0);
		}
		for (int x = (int) Math.ceil(gridVisiblePart.getX()); x < gridVisiblePart.getMaxX(); x++) {
			if (x % multiplier == 0)
				g.drawRect((int) ((x - scrollX) * gridScale), 0, 0, g.getClipBounds().height);
		}
	}
	
	/** Draws the selection box. */
	public void drawSelection(Graphics2D g) {
		// Sort coords because Graphics is very incredibly stupid.
		int x0, x1;
		if (cursorStartX < cursorX) {
			x0 = (int) (cursorStartX * 10); x1 = (int) (cursorX * 10);
		} else {
			x1 = (int) (cursorStartX * 10); x0 = (int) (cursorX * 10);
		}
		
		int y0, y1;
		if (cursorStartY < cursorY) {
			y0 = (int) (cursorStartY * 10); y1 = (int) (cursorY * 10);
		} else {
			y1 = (int) (cursorStartY * 10); y0 = (int) (cursorY * 10);
		}
		
		g.setColor(new Color(0, 127, 255, 64));
		g.fillRect(x0, y0, x1 - x0, y1 - y0);
		g.setColor(new Color(0, 127, 255));
		g.drawRect(x0, y0, x1 - x0, y1 - y0);
	}
	// endregion rendering
	
	// region viewport manipulation
	/** Calculates the grid position of the mouse cursor. */
	public void recalculateCursor() {
		cursorX = rawCursorX / gridScale + scrollX;
		cursorY = rawCursorY / gridScale + scrollY;
	}
	
	/** Pans the grid instead of recalculating the mouse position on it. */
	public void panGrid() {
		float nextX = rawCursorX / gridScale + scrollX;
		float nextY = rawCursorY / gridScale + scrollY;
		scrollX -= nextX - cursorX;
		scrollY -= nextY - cursorY;
		reevaluateAllVisibility();
	}
	
	/** Scales the grid around the mouse cursor. */
	public void scaleGridAroundCursor(float newScale) {
		// Update grid scale.
		if (newScale < minScale) newScale = minScale;
		if (newScale > maxScale) newScale = maxScale;
		gridScale = newScale;
		
		// Realign to mouse cursor.
		float nextX = rawCursorX / gridScale + scrollX;
		float nextY = rawCursorY / gridScale + scrollY;
		scrollX -= nextX - cursorX;
		scrollY -= nextY - cursorY;
		
		// Check which components are visible.
		reevaluateAllVisibility();
		
		// Update the scale textbox.
		parent.scaleField.setText((int) (gridScale * 10) + "%");
	}
	//endregion viewport manipulation
	
	// region editing
	/** Places an initialised component. */
	public void addComponent(SimComponent component) {
		components.add(component);
		component.visible = isOnScreen(component);
		component.onAdded();
		repaint();
	}
	
	/** Handle left click, assuming the cursor didn't move. */
	public void onClick(MouseEvent e) {
		if (toPlace != null) {
			// Place the ghost component.
			addComponent(toPlace.getCopy(this, (int) Math.floor(cursorX), (int) Math.floor(cursorY)));
			
		} else {
			// Modify selection.
			components.forEach(component -> {
				// Only evaluate when it is on screen.
				boolean next = component.visible && component.contains(cursorX, cursorY);
				if (e.isShiftDown()) next ^= component.selected;
				if (next != component.selected) dirty = true;
				if (next) {
					component.mouseClick(e);
				}
				component.selected = next;
			});
			if (dirty) repaint();
		}
	}

	/** Finds the closest object to which a wire could be attached. */
	public WireAttachable findClosestWirePoint() {
		// Round cursor position.
		int x = Math.round(cursorX);
		int y = Math.round(cursorY);
		
		for (SimComponent component : components) {
			if (!component.visible) continue;
			if (component instanceof Wire wire) {
				// Check wires.
				if (wire.contains(x, y)) return wire;
				
			} else if (component.contains(x, y)) {
				// Check components.
				Pin pin = component.findPin(x, y);
				if (pin != null) return pin;
			}
		}
		
		return null;
	}
	
	/** Joins all wire points at the cursor position. */
	public void joinWirePoints(int x, int y) {
		WireAttachable point = null;
		
		for (SimComponent component : components) {
			if (!component.visible) continue;
			if (component instanceof Wire wire) {
				// Check wires.
				if (wire.contains(x, y)) {
					if (point != null) point.join(wire);
					point = wire;
				}
				
			} else if (component.contains(x, y)) {
				// Check components.
				Pin pin = component.findPin(x, y);
				if (pin != null) {
					if (point != null) point.join(pin);
					point = pin;
				}
			}
		}
	}
	// endregion editing
	
	// region events
	@Override
	public void mousePressed(MouseEvent e) {
		isHovered = true;
		rawCursorX = e.getX();
		rawCursorY = e.getY();
		if (e.getButton() == MouseEvent.BUTTON2) {
			recalculateCursor();
			isPanning = true;
		}
		if (!isPanning) {
			// Prepare for potential selection or click event.
			recalculateCursor();
			cursorStartX	= cursorX;
			cursorStartY	= cursorY;
			isClickValid	= true;
			isSelectValid	= true;
			
			// Check components for mousedown events.
			components.forEach(component -> {
				if (component.visible && component.contains(cursorX, cursorY)) {
					component.mouseLeftDown  = e.getButton() == MouseEvent.BUTTON1;
					component.mouseRightDown = e.getButton() == MouseEvent.BUTTON3;
					boolean consumed = component.mouseDown(e);
					isSelectValid &= !consumed;
				}
			});
		}
		requestFocus();
		repaint();
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		isHovered = true;
		rawCursorX = e.getX();
		rawCursorY = e.getY();
		if (!isPanning) {
			recalculateCursor();
			
			if (wireToDrag != null) {
				// Stop, drag, the wire.
				joinWirePoints(Math.round(cursorX), Math.round(cursorY));
				wireToDrag = null;
				
				// Evaluate draggable for CONTINUATION.
				wirable = findClosestWirePoint();
			}
			
			// Send mouseup events to all affected components.
			components.forEach(component -> {
				if (component.mouseLeftDown && e.getButton() == MouseEvent.BUTTON1) {
					component.mouseLeftDown = false;
					component.mouseUp(e);
				}
				if (component.mouseRightDown && e.getButton() == MouseEvent.BUTTON3) {
					component.mouseRightDown = false;
					component.mouseUp(e);
				}
			});
			
			// Handle click events.
			if (isClickValid) {
				onClick(e);
			}
		}
		if (e.getButton() == MouseEvent.BUTTON2) {
			isPanning = false;
		}
		
		isSelectValid = false;
		repaint();
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		isHovered = true;
		rawCursorX = e.getX();
		rawCursorY = e.getY();
		
		if (isPanning) panGrid();
		else recalculateCursor();
		
		if (wireToDrag != null) {
			// Move around wire.
			Point toDrag = dragWireStart ? wireToDrag.start : wireToDrag.end;
			toDrag.x = Math.round(cursorX);
			toDrag.y = Math.round(cursorY);
			wireToDrag.calculateBounds();
			isSelectValid = false;
		}
		
		if (Math.round(cursorStartX) != Math.round(cursorX) || Math.round(cursorStartY) != Math.round(cursorY)) {
			isClickValid = false;
			
			if (wirable != null) {
				// Start wiring.
				wireToDrag = new Wire(this,
						wirable.getClosestAttachable(cursorX, cursorY),
						new Point(Math.round(cursorX), Math.round(cursorY))
				);
				wirable.attachWire(wireToDrag, wireToDrag.start);
				components.add(wireToDrag);
				dragWireStart = false;
				wirable = null;
			}
		}
		
		repaint();
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		Point prevPoint = wirable != null ? wirable.getClosestAttachable(cursorX, cursorY) : null;
		WireAttachable prev = wirable;
		
		isHovered = true;
		rawCursorX = e.getX();
		rawCursorY = e.getY();
		recalculateCursor();
		
		if (wireToDrag != null) {
			// Move around wire.
			Point toDrag = dragWireStart ? wireToDrag.start : wireToDrag.end;
			toDrag.x = Math.round(cursorX);
			toDrag.y = Math.round(cursorY);
			wireToDrag.calculateBounds();
			repaint();
			
		} else if (toPlace == null) {
			// Check for wire drag stuff.
			wirable = findClosestWirePoint();
			
			// Check for redraw.
			Point point = wirable != null ? wirable.getClosestAttachable(cursorX, cursorY) : null;
			if (wirable != prev || prevPoint != point) repaint();
			
		} else {
			// There is stuff to place, so repaint.
			repaint();
		}
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		isHovered = true;
		if (e.getPreciseWheelRotation() > 0) {
			scaleGridAroundCursor(gridScale / (float) Math.pow(1.1f, e.getPreciseWheelRotation()));
			repaint();
		} else if (e.getPreciseWheelRotation() < 0) {
			scaleGridAroundCursor(gridScale * (float) Math.pow(1.1f, -e.getPreciseWheelRotation()));
			repaint();
		}
	}
	
	@Override
	public void mouseEntered(MouseEvent e) {
		isHovered = true;
		repaint();
	}
	
	@Override
	public void mouseExited(MouseEvent e) {
		isHovered = false;
		repaint();
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			if (toPlace != null) {
				toPlace = null;
				repaint();
			}
		} else if (e.getKeyCode() == KeyEvent.VK_DELETE) {
			AtomicBoolean dirty = new AtomicBoolean(toPlace != null);
			toPlace = null;
			components.forEach(component -> {
				if (component.selected) {
					component.onRemoved();
					dirty.set(true);
				}
			});
			components.removeIf(component -> component.selected);
			if (dirty.get()) repaint();
		}
	}
	// endregion events
	
	// region ignored events
	@Override
	public void mouseClicked(MouseEvent e) {}
	@Override
	public void keyTyped(KeyEvent e) {}
	@Override
	public void keyReleased(KeyEvent e) {}
	// endregion ignored events
	
}
