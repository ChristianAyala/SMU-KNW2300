package rxtxrobot;

/**
 * This class is used to control a robot running on the Arduino Nano Board.
 * 
 * Details for the Arduino Nano board can be found at 
 * <a href="http://arduino.cc/en/Main/arduinoBoardNano">this Arduino page</a>.
 */
public class ArduinoNano extends RXTXRobot
{
    @Override
    protected int[] getInitialFreeDigitalPins()
    {
        //These are the digital pins that are initially free
        return new int[] {4, 7, 8, 9, 10, 11, 12, 13};
        
    }

    @Override
    protected int[] getInitialFreeAnalogPins()
    {
        //Arduino Pro Mini has 8 free analog pins, make them all available
        return new int[] {0, 1, 2, 3, 4, 5, 6, 7};
    }
}
