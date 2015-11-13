package rxtxrobot;

/**
 * This is a wrapper class meant to represent a single analog pin.
 * 
 * The AnalogPin class is a simple wrapper class to store readings from an
 * analog pin. An analog pin on an Arduino is made up of two properties:<br><br>
 * 
 * 1. A pin number (from 0 to 5 on an Arduino Uno, or 0 to 7 on an Arduino Nano)<br>
 * 2. A pin value.<br><br>
 * 
 * An analog pin has <strong>10 bits of precision</strong>, meaning it can have
 * values in a range from 0 to 1023, inclusive. You typically do not create these
 * explicitly. Instead, refer to the 
 * {@link rxtxrobot.RXTXRobot#getAnalogPin(int) getAnalogPin} method in the
 * RXTXRobot class for a code sample on how to get AnalogPin objects the right way.
 * 
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
         * Get the number of the pin.
         *
         * @return The pin's identifying number
         */
        public int getNumber()
        {
                return pinNum;
        }

        /**
         * Get the value of the pin.
         * 
         * The value of these analog pins will be a number between 0 and 1023. 
         * This maps linearly to a range of 0 to 5V. Refer to the
         * {@link rxtxrobot.RXTXRobot#getAnalogPin(int) getAnalogPin} method
         * for a code sample on how to convert from ADC code to voltage.
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
