// This example shows how to get the response from the Ping sensor since it is different than other Analog sensors.  It MUST be connected to digital pin 13.
import rxtxrobot.*;

public class GetPing
{
	final private static PING_PIN = 12;

	public static void main(String[] args)
	{
		RXTXRobot r = new ArduinoUno(); // Create RXTXRobot object
		r.setPort("COM3"); // Set the port to COM3
		r.connect();
		for (int x=0; x < 100; ++x)
		{
			//Read the ping sensor value, which is connected to pin 12
			System.out.println("Response: " + r.getPing(PING_PIN) + " cm");
			r.sleep(300);
		}
		r.close();
	}
}

