// This example shows how to get the Analog pin sensor data from the Arduino.  It shows the value of every Analog pin once after connecting to the Arduino.
import rxtxrobot.*;

public class GetSensorData
{   
    public static void main(String[] args)
    {    
	    // All sensor data will be read from the analog pins
		
	    RXTXRobot r = new RXTXRobot(); //Create RXTXRobot object

		r.setPort("COM5"); // Sets the port to COM5
		
		r.connect();

		r.refreshAnalogPins(); // Cache the Analog pin information

		for (int x=0; x < RXTXRobot.NUM_ANALOG_PINS; ++x)
		{
			AnalogPin temp = r.getAnalogPin(x);
			System.out.println("Sensor " + x + " has value: " + temp.getValue());
		}
		r.close();
    }
}
