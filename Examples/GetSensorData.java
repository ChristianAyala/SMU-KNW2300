import rxtxrobot.*;

public class GetSensorData
{   
    public static void main(String[] args)
    {    
        // All sensor data will be read from the analog pins
	
        RXTXRobot r = new RXTXRobot("COM5"); //Create object on COM5
	
	r.refreshAnalogPins();

	for (int x=0; x < RXTXRobot.NUM_ANALOG_PINS; ++x)
	{
		AnalogPin temp = r.getAnalogPin(x);
		System.out.println("Sensor " + x + " has value: " + temp.getValue());
	}
	r.close();
    }
}
