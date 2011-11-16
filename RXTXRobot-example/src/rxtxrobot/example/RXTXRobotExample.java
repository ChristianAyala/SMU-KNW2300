package rxtxrobot.example;

import rxtxrobot.*;

public class RXTXRobotExample {

    public static void main (String [] args) throws InterruptedException
    {
        RXTXRobot r = new RXTXRobot("/dev/ttyUSB0","",true);
        if (!r.isConnected())
        {
            System.out.println("Error connecting");
            System.exit(0);
        }
        //for (int x=0;x<10;++x)
          //  r.readFromLabView();
        r.moveServo(RXTXRobot.SERVO1,20);
        r.runMotor(RXTXRobot.MOTOR1,200,1000);
        //r.runMotor(RXTXRobot.MOTOR1,500,1000);
        r.close();
        /*for (int x=0;x<5;++x)
        {
            System.out.println("Trial 1");
            Coord c = r.readFromLabView();
            if (c != null)
                System.out.println("Recieved a coordinate of: " + c.getX() + ", " + c.getY() + ", " + c.getZ());
            else
                System.out.println("Didn't receive a coordinate object");
            r.sleep(500);
        }
        r.close();
        /*
        Scanner s = new Scanner(System.in);
        String t = "";
        /*do
        {
            System.out.print("Input: ");
            t = s.nextLine();
            if (!t.equals("exit"))
            {
                r.sendRaw(t);
                System.out.println("Response -->"+r.getLastResponse());
            }
        }
        while(!t.equals("exit"));
        r.close();*//*
        System.out.println("Moving forward...");
        //r.runMotor(RXTXRobot.MOTOR1,500,RXTXRobot.MOTOR2,500,1000);
        //r.sleep(2000);
        //System.out.append("Moving backwards....");
        //r.runMotor(RXTXRobot.MOTOR1,-500,RXTXRobot.MOTOR2,-500,1000);
        //r.sleep(2000);
        //System.out.println("Closing...");
        r.moveServo(RXTXRobot.SERVO1,50);
        r.sleep(500);
        r.moveServo(RXTXRobot.SERVO2,50);
        r.sleep(500);
        r.moveBothServos(90,90);
        r.sleep(500);
        r.close();
        System.out.println("Done");*
        System.out.println("Connected...");
        Thread.sleep(1000);
        System.out.println("Reading digital pins: ");
        String[] a = r.getDigitalPins().trim().split("\\s+");
        System.out.print("Size: "+a.length+"; ");
        for (int x=0;x<a.length;++x)
            System.out.print(a[x]+",");
        System.out.println();
        int temp = 3;
        for (int x=0;x<RXTXRobot.NUM_DIGITAL_PINS;++x)
        {
            if ((temp = r.getDigitalPin(x))==-1)
            {
                System.out.println("FINISHED");
                break;
            }
            System.out.println("Digital Pin #"+x+" was "+temp);
        }
        System.out.println("\nAnalog Pins:");
        a = r.getAnalogPins().trim().split("\\s+");
         System.out.print("Size: "+a.length+"; ");
        for (int x=0;x<a.length;++x)
            System.out.print(a[x]+",");
        System.out.println();
        temp = -1;
        for (int x=0;x<RXTXRobot.NUM_ANALOG_PINS;++x)
        {
            if ((temp = r.getAnalogPin(x))==-1)
            {
                System.out.println("FINISHED");
                break;
            }
            System.out.println("Analog Pin #"+x+" was "+temp);
        }
        System.out.println("\nClosing...");*
        System.out.println("Starting analog pin reading...");
        for (int x=0;x<100;++x)
        {
            System.out.println("Pin 0: "+r.getAnalogPin(0)+"     ");
            r.sleep(200);
            System.out.print("\r");
        }
        System.out.println("Stopping...");*
        int one,two;
        for (int x=0;x<10000;++x)
        {
            one = r.getAnalogPin(0);
            //two = r.getAnalogPin(1);
            System.out.println("\nPin 0: " + one);
            r.sleep(100);
        }
        r.close();*/
        //r.sleep(1000);
        //r.sleep(2000);
        //r.close();*/
    }
}