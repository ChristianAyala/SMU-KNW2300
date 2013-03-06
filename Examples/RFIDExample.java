// This example shows how to use the RFID Sensor to get a scanned tag.  This is a completely separate object from RXTXRobot.
import rxtxrobot.*;

public class RFIDExample
{   
    public static void main(String[] args)
    {    	
        RFIDSensor r = new RFIDSensor(); //Create RFIDSensor object
	r.setPort("COM5"); // Sets the port to COM5
	r.connect();
	while (!r.hasTag())
		r.sleep(300);
	System.out.println("Got tag: " + r.getTag());

	r.close();
    }
}
