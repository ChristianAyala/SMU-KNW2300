// This example shows how to move only one DC encoded motor.  The motor will run for the specific number of ticks, which pauses the execution of the program until the motor stops.
import rxtxrobot.*;

public class RunEncodedMotor
{
	public static void main(String[] args)
	{
		RXTXRobot r = new RXTXRobot(); // Create RXTXRobot object
		r.setPort("COM3"); // Set port to COM3
		r.setHasEncodedMotors(true);
		r.connect();
		r.runEncodedMotor(RXTXRobot.MOTOR1, 255, 100000); // Run motor 1 forward (speed of 255) for 100,000 ticks
		// Program stops until the command above is completed
		r.runEncodedMotor(RXTXRobot.MOTOR1, -255, 100000); // Run motor 1 backward (speed of 255) for 100,000 ticks
		r.close();
	}
}
