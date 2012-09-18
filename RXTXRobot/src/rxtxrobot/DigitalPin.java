package rxtxrobot;

/**
 * @author Chris King
 * @version 2.8
 */
public class DigitalPin
{
    private RXTXRobot parent;
    private int pinNum;
    private int pinVal;
    /**
     * Initialize DigitalPin with the pin number and the pin value.
     * @param p RXTXRobot parent object to this pin
     * @param pin Digital pin number
     * @param val Value to this Digital pin
     */
    public DigitalPin(RXTXRobot p, int pin, int val)
    {
        parent = p;
        pinNum = pin;
        pinVal = val;
    }
    /**
     * Get the value of the pin,
     * @return The pin's value
     */
    public int getValue()
    {
        return pinVal;
    }
    /**
     * Set the value of the pin.
     * 
     * This actually sends the command to the robot to set the value of the pin.
     * @param x The value to set the pin to.
     */
    public void setValue(int x)
    {
        parent.setDigitalPin(pinNum, x);
    }
}
