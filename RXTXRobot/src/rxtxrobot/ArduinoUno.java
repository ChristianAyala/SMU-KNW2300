package rxtxrobot;

/**
 * This class is used to control a robot running on the Arduino Uno board.
 * 
 * This is a subclass of {@link rxtxrobot.RXTXRobot RXTXRobot}. This class
 * has no exposed functionality of its own; instead refer to the
 * {@link rxtxrobot.RXTXRobot RXTXRobot} class for an exhaustive list of
 * available methods.<br><br>
 * 
 * In particular, refer to the constructor 
 * {@link rxtxrobot.RXTXRobot#RXTXRobot() RXTXRobot()} for a code sample on how
 * to use this class.
 * 
 * Details for the Arduino Uno board can be found at 
 * <a href="http://arduino.cc/en/Main/arduinoBoardUno">this Arduino page</a>.
 */
public class ArduinoUno extends RXTXRobot
{
    
    @Override
    protected int[] getInitialFreeDigitalPins()
    {
        //These are the digital pins that are initially free
        return new int[] {4, 5, 6, 7, 8, 9, 10, 11, 12, 13};
        
    }

    @Override
    protected int[] getInitialFreeAnalogPins()
    {
        //Arduino Uno has 6 free analog pins, make them all available
        return new int[] {0, 1, 2, 3, 4, 5};
    }
}
