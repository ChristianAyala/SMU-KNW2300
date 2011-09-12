/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rxtxrobot.example;

import java.util.Scanner;
import rxtxrobot.RXTXRobot;

public class RXTXRobotExample {

    public static void main (String [] args)
    {
        RXTXRobot r = new RXTXRobot("/dev/ttyUSB0",true);
        Scanner s = new Scanner(System.in);
        String t = "";
        do
        {
            System.out.print("Input: ");
            t = s.nextLine();
            if (!t.equals("exit"))
            {
                r.sendRaw(t);
            }
        }
        while(!t.equals("exit"));
        System.out.println("Closing...");
        r.close();
        System.out.println("Done");
    }
}