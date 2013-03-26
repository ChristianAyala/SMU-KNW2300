// This example shows how to run both DC encoded motors at the same time but different distances.
import rxtxrobot.*;

public class RunBothEncodedMotors
{
	public static void main(String[] args)
	{
		RXTXRobot r = new RXTXRobot(); // Create RXTXRobot object
		r.setPort("COM2"); // Set port to COM2
		r.setHasEncodedMotors(true);
		r.connect();
		r.runEncodedMotor(RXTXRobot.MOTOR1, 255, 100000, RXTXRobot.MOTOR2, 255, 50000); // Run both motors forward, one for 100,000 ticks and one for 50,000 ticks.
		r.close();
	}
}
