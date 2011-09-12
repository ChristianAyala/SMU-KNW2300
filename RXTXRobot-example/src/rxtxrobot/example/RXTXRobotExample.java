/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rxtxrobot.example;

import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import rxtxrobot.RXTXRobot;

public class RXTXRobotExample {

    public static void main (String [] args)
    {
        RXTXRobot r = new RXTXRobot("/dev/ttyUSB0",true);
        /*Scanner s = new Scanner(System.in);
        String t = "";
        do
        {
            System.out.print("Input: ");
            t = s.nextLine();
            if (!t.equals("exit"))
            {
                r.sendRaw(t);
                System.out.println("Response -->"+r.getLastResponse());
            }
        }
        while(!t.equals("exit"));*/
        System.out.println("Moving forward...");
        r.runMotor(RXTXRobot.MOTOR1,2000,RXTXRobot.MOTOR2,2000,1000);
        r.sleep(3000);
        System.out.append("Moving backwards....");
        r.runMotor(RXTXRobot.MOTOR1,-2000,RXTXRobot.MOTOR2,-2000,1000);
        r.sleep(2000);
        System.out.println("Closing...");
        r.close();
        System.out.println("Done");
    }
}