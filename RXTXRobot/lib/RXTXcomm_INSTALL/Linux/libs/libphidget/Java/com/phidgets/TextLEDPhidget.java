
/*
 * Copyright 2006 Phidgets Inc.  All rights reserved.
 */

package com.phidgets;
import java.util.Iterator;
import java.util.LinkedList;
import com.phidgets.event.*;
/**
 * This class represents a Phidget Text LED. All methods
 * to control the Text LED are implemented in this class.
 * <p>
 * The Text LED is a Phidget that displays text and numerals on LED
 * numeric display in rows. The number of rows and size of each row depends on
 * your configuration.
 * 
 * @author Phidgets Inc.
 */
public final class TextLEDPhidget extends Phidget
{
	public TextLEDPhidget () throws PhidgetException
	{
		super (create ());
	}
	private static native long create () throws PhidgetException;
	/**
	 * Returns the number of rows. This returns the maximum number of rows supported by the device, not neccessarily
	 * the number of rows actually available with your coniguration.
	 * @return rows
	 * @throws PhidgetException If this Phidget is not opened and attached. 
	 * See {@link com.phidgets.Phidget#open(int) open} for information on determining if a device is attached.
	 */
	public native int getRowCount () throws PhidgetException;
	/**
	 * Returns the number of columns (Characters per row). This returns the maximum number of columns supported by the device, not neccessarily
	 * the number of columns actually available with your coniguration.
	 * @return columns
	 * @throws PhidgetException If this Phidget is not opened and attached. 
	 * See {@link com.phidgets.Phidget#open(int) open} for information on determining if a device is attached.
	 */
	public native int getColumnCount () throws PhidgetException;
	/**
	 * Returns the bringhtness. This is the brightneww of all rows. The Default brightness is 100.
	 * @return brightness
	 * @throws PhidgetException If this Phidget is not opened and attached. 
	 * See {@link com.phidgets.Phidget#open(int) open} for information on determining if a device is attached.
	 */
	public native int getBrightness () throws PhidgetException;
	/**
	 * Sets the brightness of all rows. The valid range is 0-100.
	 * @param brightness brightness
	 * @throws PhidgetException If this Phidget is not opened and attached, or the brightness value is out of range. 
	 * See {@link com.phidgets.Phidget#open(int) open} for information on determining if a device is attached.
	 */
	public native void setBrightness (int brightness) throws PhidgetException;
	/**
	 * Sets the display string of a certain row. If the string is longer then the row, it will be truncated.
	 * @param index Row
	 * @param text String
	 * @throws PhidgetException If this Phidget is not opened and attached, or if the row is out of range. 
	 * See {@link com.phidgets.Phidget#open(int) open} for information on determining if a device is attached.
	 */
	public native void setDisplayString (int index, String text) throws PhidgetException;
	private final void enableDeviceSpecificEvents (boolean b)
	{
	}
}
