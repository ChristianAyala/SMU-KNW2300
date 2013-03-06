package rxtxrobot;

/**
 * @author Chris King
 * @version 3.1.1
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
