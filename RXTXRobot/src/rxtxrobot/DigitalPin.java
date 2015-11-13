package rxtxrobot;

/**
 * This is a wrapper class meant to represent a single digital pin.
 * 
 * The DigitalPin class is a simple wrapper class to store readings from an
 * digital pin. An digital pin on an Arduino is made up of two properties:<br><br>
 * 
 * 1. A pin number (typically from 2 to 13, as 0 and 1 should never be used)<br>
 * 2. A pin value, either 0 or 1.<br><br>
 * 
 * A digital pin will have a value of 0 if the voltage going into the pin is
 * less than 3 volts. It will have a value of 1 if the voltage going into the
 * pin is greater than 3 volts. Typically you do not read digital pins directly.
 * The only real use case in which you do is to wire a bump sensor, and read
 * when the voltage is high (which means it is triggered). Instead, the 
 * {@link rxtxrobot.RXTXRobot RXTXRobot} class has lots of helper methods for
 * moving motors, servos, reading digital sensors, etc.
 * 
 */
public class DigitalPin
{
        private int pinNum;
        private int pinVal;

        /**
         * Initialize DigitalPin with the pin number and the pin value.
         *
         * @param pin Digital pin number
         * @param val Value to this Digital pin
         */
        public DigitalPin(int pin, int val)
        {
                pinNum = pin;
                pinVal = val;
        }

        /**
         * Get the number of the pin,
         *
         * @return The pin's identifying number
         */
        public int getNumber()
        {
                return pinNum;
        }

        /**
         * Get the value of the pin,
         *
         * @return The pin's value. This will be 0 if the input voltage is less
         * than about 3 volts. This will be 1 if the input voltage on this pin
         * is greater than about 3 volts.
         */
        public int getValue()
        {
                return pinVal;
        }

        /**
         * Displays the pin in a readable form
         *
         * @return A string that is a readable representation of the pin object
         */
        @Override
        public String toString()
        {
                return "Pin  #" + getNumber() + ": " + getValue();
        }
}
