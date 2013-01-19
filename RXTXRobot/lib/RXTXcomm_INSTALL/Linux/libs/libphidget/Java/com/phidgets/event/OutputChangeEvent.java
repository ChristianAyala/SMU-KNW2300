/*
 * Copyright 2006 Phidgets Inc.  All rights reserved.
 */

package com.phidgets.event;

import com.phidgets.Phidget;

/**
 * This class represents the data for a OutputChangeEvent.
 * 
 * @author Phidgets Inc.
 */
public class OutputChangeEvent
{
	Phidget source;
	int index;
	boolean state;

	/**
	 * Class constructor. This is called internally by the phidget library when creating this event.
	 * 
	 * @param source the Phidget object from which this event originated
	 */
	public OutputChangeEvent(Phidget source, int index, boolean state) {
		this.source = source;
		this.index = index;
		this.state = state;
	}

	/**
	 * Returns the source Phidget of this event. This is a reference to the Phidget object from which this
	 * event was called. This object can be cast into a specific type of Phidget object to call specific
	 * device calls on it.
	 * 
	 * @return the event caller
	 */
	public Phidget getSource() {
		return source;
	}

	/**
	 * Returns the index of the output.
	 * 
	 * @return the index of the output
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * Returns the state of the output. True indicated that the output is active, False indicated the default inactive state.
	 * 
	 * @return the state of the output
	 */
	public boolean getState() {
		return state;
	}

	/**
	 * Returns a string containing information about the event.
	 * 
	 * @return an informative event string
	 */
	public String toString() {
		return source.toString() + " output " + index + " changed to "
		  + state;
	}
}
