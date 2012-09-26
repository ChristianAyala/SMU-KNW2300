package rxtxrobot;

/**
 * @author Chris King
 * @version 2.9
 */
public class AnalogOutputPin
{
    private RXTXRobot parent;
    private int pinNum;
    /**
     * Constant to turn on the pin's voltage
     */
    public static final int ON = 127;
    /**
     * Constant to turn off the pin's voltage
     */
    public static final int OFF = 0;
    
    /**
     * Initialize AnalogOutputPin with the pin number and the pin value.
     * @param p RXTXRobot parent object to this pin
     * @param pin Analog pin number
     */
    public AnalogOutputPin(RXTXRobot p, int pin)
    {
        parent = p;
        pinNum = pin;
    }
    
    /**
     * Set the value of the AnalogOutputPin.
     * 
     * This sets the value of the Analog pin (in the Digital Pin rack).
     * @param value This is the value to set the pin to.  This must be either {@link #ON AnalogOutputPin.ON} or {@link #OFF AnalogOutputPin.OFF}
     * @return 
     */
    public void setValue(int value)
    {
        if (value != AnalogOutputPin.OFF && value != AnalogOutputPin.ON)
        {
            System.err.println("ERROR: You may only set the value of the pin to AnalogOutputPin.OFF or AnalogOutputPin.ON");
            return;
        }
        parent.debug("Setting Analog Pin " + pinNum + " to " + value);
        parent.sendRaw("w a " + pinNum + " " + value);
    }
}
