/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rxtxrobot;

/**
 *
 * @author Chris
 */
public class TesterClass {
    public static RXTXRobot robot;

    public static void main(String[] args) {
	System.out.println("Hello world");
        robot= new RXTXRobot(); // Create RXTXRobot object
        robot.setPort("/dev/ttyUSB0"); // Set the port to COM3
        robot.setVerbose(true); // Turn on debugging messages
        robot.connect();
        robot.setHasEncodedMotors(true);

        //testServos();
        //testRunMotors();
        testRunEncodedMotors();

        robot.close();
    }

    public static void delay(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void testServos() {
        robot.moveServo(RXTXRobot.SERVO1, 0);
        delay(2000);
        robot.moveServo(RXTXRobot.SERVO1, 90);
        delay(2000);
        robot.moveServo(RXTXRobot.SERVO1, 180);
        delay(2000);
        robot.moveServo(RXTXRobot.SERVO1, 90);
        delay(2000);

        robot.moveServo(RXTXRobot.SERVO2, 0);
        delay(2000);
        robot.moveServo(RXTXRobot.SERVO2, 90);
        delay(2000);
        robot.moveServo(RXTXRobot.SERVO2, 180);
        delay(2000);
        robot.moveServo(RXTXRobot.SERVO2, 90);
        delay(2000);
    }

    public static void testRunMotors() {
        robot.runMotor(RXTXRobot.MOTOR1, 255, 2000);
    }
    
    public static void testRunEncodedMotors() {
        robot.runEncodedMotor(RXTXRobot.MOTOR1, 500, 300);
        robot.runEncodedMotor(RXTXRobot.MOTOR1, -500, 300);
        //delay(1000);
        robot.runEncodedMotor(RXTXRobot.MOTOR1, 500, 300, RXTXRobot.MOTOR2, 500, 300);
        robot.runEncodedMotor(RXTXRobot.MOTOR1, -500, 300, RXTXRobot.MOTOR2, -500, 300);
        //delay(2000);
        robot.runEncodedMotor(RXTXRobot.MOTOR1, -500, 3000, RXTXRobot.MOTOR2, 500, 3000);
        robot.runEncodedMotor(RXTXRobot.MOTOR1, 500, 300, RXTXRobot.MOTOR2, -500, 300);
        
    }
}
