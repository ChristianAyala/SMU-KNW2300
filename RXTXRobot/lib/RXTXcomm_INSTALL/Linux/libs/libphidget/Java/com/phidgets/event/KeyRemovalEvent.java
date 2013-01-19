/*
 * Copyright 2006 Dictionarys Inc.  All rights reserved.
 */

package com.phidgets.event;

import com.phidgets.Dictionary;

/**
 * This class represents the data for a KeyRemovalEvent.
 * 
 * @author Dictionarys Inc.
 */
public class KeyRemovalEvent
{
	Dictionary source;
	String value;
	String key;

	/**
	 * Class constructor. This is called internally by the Dictionary library when creating this event.
	 * 
	 * @param source the Dictionary object from which this event originated
	 */
	public KeyRemovalEvent(Dictionary source, String key, String value)
	{
		this.source = source;
		this.value = value;
		this.key = key;
	}

	/**
	 * Returns the source Dictionary of this event. This is a reference to the Dictionary object from which this
	 * event was called. This object can be cast into a specific type of Dictionary object to call specific
	 * device calls on it.
	 * 
	 * @return the event caller
	 */
	public Dictionary getSource() {
		return source;
	}

	public String getKey()
	{
		return key;
	}

	public String getValue() {
		return value;
	}

	/**
	 * Returns a string containing information about the event.
	 * 
	 * @return an informative event string
	 */
	public String toString() {
		return source.toString() + " Key removed: "
		  + key + ":" + value;
	}
}
