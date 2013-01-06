import rxtxrobot.*;

public class RunBothMotors
{
	public static void main(String[] args)
	{
		RXTXRobot r = new RXTXRobot(); // Create RXTXRobot object
		r.setPort("COM2"); // Set port to COM2
		r.runMotor(RXTXRobot.MOTOR1, 125, RXTXRobot.MOTOR2, 125, 0); // Run both motors forward indefinitely
		r.sleep(5000); // Pause execution for 5 seconds, but the motors keep running.
		r.runMotor(RXTXRobot.MOTOR1,0,RXTXRobot.MOTOR2,0,0); // Stop both motors
		r.close();
	}
}
