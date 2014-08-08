package rxtxrobot;

/**
 * This class is used to control a robot running on the Arduino Uno board.
 * 
 * Details for the Arduino Uno board can be found at 
 * http://arduino.cc/en/Main/arduinoBoardUno.
 */
public class ArduinoUno extends RXTXRobot
{
    
    @Override
    protected int[] getInitialFreeDigitalPins()
    {
        //These are the digital pins that are initially free
        return new int[] {4, 7, 8, 9, 10, 11, 13};
        
    }

    @Override
    protected int[] getInitialFreeAnalogPins()
    {
        //Arduino Uno has 6 free analog pins, make them all available
        return new int[] {0, 1, 2, 3, 4, 5};
    }
}
