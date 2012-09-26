import rxtxrobot.*;

public class GetSensorData
{   
    public static void main(String[] args)
    {    
        // All sensor data will be read from the analog pins
	
        RXTXRobot r = new RXTXRobot("COM5"); //Create object on COM5
	
	r.refreshAnalogInputPins();

	for (int x=0; x < RXTXRobot.NUM_ANALOG_PINS; ++x)
	{
		AnalogInputPin temp = r.getAnalogInputPin(x);
		System.out.println("Sensor " + x + " has value: " + temp.getValue());
	}

	// Write to an Analog Pin (turn ON), there is also an OFF constant
	AnalogOutputPin p = r.getAnalogOutputPin(3);
	p.setValue(AnalogOutputPin.ON);
	
	// Read a digital pin
	r.refreshDigitalPins();
	DigitalPin d = r.getDigitalPin(2);
	System.out.println("Digital pin 2 has value: " + d.getValue());

	r.close();
    }
}
