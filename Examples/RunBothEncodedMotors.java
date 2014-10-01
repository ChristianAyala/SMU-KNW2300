// This example shows how to run both DC encoded motors at the same time but different distances.
import rxtxrobot.*;

public class RunBothEncodedMotors
{
	public static void main(String[] args)
	{
		RXTXRobot r = new ArduinoNano(); // Create RXTXRobot object
		r.setPort("COM2"); // Set port to COM2
		r.connect();
		//We don't have to attach anything because these motors are
		//attached by default
		r.runEncodedMotor(RXTXRobot.MOTOR1, 255, 100, RXTXRobot.MOTOR2, 255, 500); // Run both motors forward, one for 100,000 ticks and one for 50,000 ticks.
		r.close();
	}
}
