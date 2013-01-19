/*
 * Copyright 2006 Phidgets Inc.  All rights reserved.
 */

package com.phidgets.event;

import com.phidgets.Phidget;
import com.phidgets.IRCode;

/**
 * This class represents the data for a CodeEvent.
 * 
 * @author Phidgets Inc.
 */
public class CodeEvent
{
	Phidget source;
	IRCode code;
	boolean repeat;

	/**
	 * Class constructor. This is called internally by the phidget library when creating this event.
	 * 
	 * @param source the Phidget object from which this event originated
	 * @param code the IR code
	 * @param repeat whether the code is a repeat
	 */
	public CodeEvent(Phidget source, IRCode code, boolean repeat)
	{
		this.source = source;
		this.code = code;
		this.repeat = repeat;
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
	 * Returns the code.
	 * 
	 * @return the code
	 */
	public IRCode getCode() {
		return code;
	}

	/**
	 * Returns the repeat identifier.
	 * 
	 * @return whether this is a repeat
	 */
	public boolean getRepeat() {
		return repeat;
	}

	/**
	 * Returns a string containing information about the event.
	 * 
	 * @return an informative event string
	 */
	public String toString() {
		return source.toString() + " Code: "
		  + code.toString();
	}
}
