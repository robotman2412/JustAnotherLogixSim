package net.scheffers.robot.logixsim.fundamental;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

class WireNet {
	
	/** The current value that is on this wire net. */
	private boolean value;
	
	/** The pins with output capability on this network. */
	public List<Pin> outputs;
	/** The pins with input capability on this network. */
	public List<Pin> inputs;
	/** All wire members of this net. */
	public List<Wire> wires;
	
	public WireNet() {
		outputs = new ArrayList<>();
		inputs = new ArrayList<>();
		wires = new ArrayList<>();
	}
	
	/** Connects this wire net to a new pin. */
	public void connect(Pin pin) {
		outputs.remove(pin);
		inputs.remove(pin);
		if (pin.getDirection().isOutput) outputs.add(pin);
		if (pin.getDirection().isInput) inputs.add(pin);
	}
	
	/** Merges this wire net with another. */
	public void merge(WireNet other) {
		other.outputs.forEach(pin -> pin.net = this);
		other.inputs.forEach(pin -> pin.net = this);
		other.wires.forEach(wire -> wire.net = this);
		outputs.addAll(other.outputs);
		inputs.addAll(other.inputs);
		wires.addAll(other.wires);
		value |= other.value;
	}
	
	/** Called to notify that a pin changed direction modes. */
	public void directionChanged(Pin pin, Direction oldDirection) {
		outputs.remove(pin);
		inputs.remove(pin);
		if (pin.getDirection().isOutput) outputs.add(pin);
		if (pin.getDirection().isInput) inputs.add(pin);
		
		if (oldDirection.isOutput) calculateValue();
	}
	
	/** Recalculates the value on this net. */
	boolean calculateValue() {
		boolean oldValue = value;
		value = false;
		
		for (Pin output : outputs) {
			if (output.getDrivenValue()) {
				value = true;
				break;
			}
		}
		
		if (oldValue != value) {
			inputs.forEach(pin -> {
				if (pin.onChange != null) pin.onChange.run();
			});
		}
		
		return value;
	}
	
	/** Get the value on this net without reevaluating it. */
	boolean getValue() {
		return value;
	}
	
	/** Gets the recommended color for this net based on value. */
	public Color getColor() {
		if (outputs.size() == 0) {
			return Color.DARK_GRAY;
		} else if (value) {
			return Color.GREEN;
		} else {
			return new Color(0, 64, 0);
		}
	}
	
	/** Removes a pin from this net. */
	public void remove(Pin pin) {
	
	}
	
}
