// This example shows how to get the response from the Ping sensor since it is different than other Analog sensors.  It MUST be connected to digital pin 13.
import rxtxrobot.*;

public class GetPing
{
	public static void main(String[] args)
	{
		RXTXRobot r = new RXTXRobot(); // Create RXTXRobot object
		r.setPort("COM3"); // Set the port to COM3
		r.connect();
		for (int x=0; x < 100; ++x)
		{
			System.out.println("Response: " + r.getPing() + " cm");
			r.sleep(300);
		}
		r.close();
	}
}

