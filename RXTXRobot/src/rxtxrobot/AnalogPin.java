package rxtxrobot;

/**
 * @author Chris King
 * @version 3.1.2
 */
public class AnalogPin
{
        private int pinNum;
        private int pinVal;

        /**
         * Initialize AnalogPin with the pin number and the pin value.
         *
         * @param pin Analog pin number
         * @param val Value to this analog pin
         */
        public AnalogPin(int pin, int val)
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
         * @return The pin's value
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
