public class RunBothMotors
{
	public static void main(String[] args)
	{
		RXTXRobot r = new RXTXRobot("COM2"); // Create object on COM2
		r.runMotor(RXTXRobot.MOTOR1, 2000, RXTXRobot.MOTOR2, 2000, 0); // Run both motors forward indefinitely
		r.sleep(5000); // Pause execution for 5 seconds, but the motor keeps running.
		r.runMotor(RXTXRobot.MOTOR1,0,RXTXRobot.MOTOR2,0,0); // Stop both motors
		r.close();
	}
}
