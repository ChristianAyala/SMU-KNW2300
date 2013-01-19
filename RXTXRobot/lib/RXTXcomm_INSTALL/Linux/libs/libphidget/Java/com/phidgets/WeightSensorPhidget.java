
/*
 * Copyright 2006 Phidgets Inc.  All rights reserved.
 */

package com.phidgets;
import java.util.Iterator;
import java.util.LinkedList;
import com.phidgets.event.*;
/**
 * This class represents a Phidget Weight Sensor. All methods
 * to read weight data from the weight sensor are implemented in this class.
 * <p>
 * The Phidget Weight Sensor is simply an electronic scale with a USB interface.
 * It provides one weight value, in kg.
 *
 * @author Phidget Inc.
 */
public final class WeightSensorPhidget extends Phidget
{
	public WeightSensorPhidget () throws PhidgetException
	{
		super (create ());
	}
	private static native long create () throws PhidgetException;
	/**
	 * Returns the sensed weight in kg. This weight is in kg (kilograms), but can
	 * easily be converted into other units. The accuracy, sesitivity and range depends on the scale and version.
	 *
	 * @return The sensed weight
	 * @throws PhidgetException If this Phidget is not opened and attached. 
	 * See {@link com.phidgets.Phidget#open(int) open} for information on determining if a device is attached.
	 */
	public native double getWeight () throws PhidgetException;
	/**
	 * Sets the weight change trigger. This is the ammount by which the sensed weight must
	 * change between WeightChangeEvents. By default this is set to 5.
	 *
	 * @param newVal The new trigger value
	 * @throws PhidgetException If this Phidget is not opened and attached. 
	 * See {@link com.phidgets.Phidget#open(int) open} for information on determining if a device is attached.
	 */
	public native void setWeightChangeTrigger (double newVal) throws PhidgetException;
	/**
	 * Returns the weight change trigger. This is the ammount by which the sensed weight must
	 * change between WeightChangeEvents. By default this is set to 5.
	 *
	 * @return The trigger value
	 * @throws PhidgetException If this Phidget is not opened and attached. 
	 * See {@link com.phidgets.Phidget#open(int) open} for information on determining if a device is attached.
	 */
	public native double getWeightChangeTrigger () throws PhidgetException;

	private final void enableDeviceSpecificEvents (boolean b)
	{
		enableWeightChangeEvents (b && weightChangeListeners.size () > 0);
	}
	/**
	 * Adds a weight change listener. The weight change handler is a method that will be called when the weight 
	 * has changed by at least the {@link #getWeightChangeTrigger() Trigger} that has been set.
	 * <p>
	 * There is no limit on the number of weight change handlers that can be registered for a particular Phidget.
	 * 
	 * @param l An implemetation of the {@link com.phidgets.event.WeightChangeListener WeightChangeListener} interface
	 */
	public final void addWeightChangeListener (WeightChangeListener l)
	{
		synchronized (weightChangeListeners)
		{
			weightChangeListeners.add (l);
			enableWeightChangeEvents (true);
	}} private LinkedList weightChangeListeners = new LinkedList ();
	private long nativeWeightChangeHandler = 0;
	public final void removeWeightChangeListener (WeightChangeListener l)
	{
		synchronized (weightChangeListeners)
		{
			weightChangeListeners.remove (l);
			enableWeightChangeEvents (weightChangeListeners.size () > 0);
	}} private void fireWeightChange (WeightChangeEvent e)
	{
		synchronized (weightChangeListeners)
		{
			for (Iterator it = weightChangeListeners.iterator (); it.hasNext ();)
				((WeightChangeListener) it.next ()).weightChanged (e);
		}
	}
	private native void enableWeightChangeEvents (boolean b);
}
