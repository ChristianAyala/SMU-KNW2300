/* 
 ---- Modification to SimpleMessageSystem to Include Motor Commands ----
 
 Control Arduino board functions with the following messages:
 
 r a -> read analog pins
 r d -> read digital pins
 w d [pin] [value] -> write digital pin
 w a [pin] [value] -> write analog pin
 
 v [num] [position] -> move servo number [num] to position [position] (position is (0,180)
 p [num] [steps] -> move stepper motor [num] in direction [direction] [steps] steps
 d [num] [speed] [t] -> set dc motor number [num] to speed [speed] for time [t], if t=0 then keep on.
 
 The next 3 do the same thing for 2 motors as close to simultaneously as possible:
 V [position1] [position2]-> move servos to position1 and 2 [position] (position is (0,180)
 P [num1] [steps1] [num2] [steps2]-> move 2 stepper motors [num] in direction [direction] [steps] steps
 D [num1] [speed1] [num2] [speed2] [t] -> set dc motor number [num] to speed [speed] for time [t], if t=0 then keep on.
 
 F [num1] [speed1] [num2] [speed2] [num3] [speed3] [num4] [speed4] [t] -> set dc motor number [num] to speed [speed] for time [t], if t=0 then keep on

 
 Base: Thomas Ouellet Fredericks 
 Additions: Alexandre Quessy
 Motor Additions: Marc Christensen
 
 */

// Include de SimpleMessageSystem library
// REMOVE THE FOLLOWING LINE IF USING WIRING
#include <SimpleMessageSystem.h> 

// Needed for Servos
#include <Servo.h>

// Needed for DC motors
#include <AFMotor.h>

//Set up our global motor types
	AF_DCMotor motor0(1);
	AF_DCMotor motor1(2);
AF_DCMotor motor2(3);
AF_DCMotor motor3(4);
Servo myservo0;
Servo myservo1;
// create an instance of the stepper class, 

void setup()
{
  
  // The following command initiates the serial port at 9600 baud. Please note this is VERY SLOW!!!!!! 
  // I suggest you use higher speeds in your own code. You can go up to 115200 with the USB version, that's 12x faster
  Serial.begin(9600); //Baud set at 9600 for compatibility, CHANGE!
  myservo0.attach(9); // attach the sevo on pin 9 to servo object
  myservo1.attach(10); // attach the servo on pin 10 to servo object
	 motor0.setSpeed(0);
	 motor1.setSpeed(0);
	 motor0.run(FORWARD);
	 motor1.run(FORWARD);
//  motor0.setSpeed(0); // initialize motor speeds
//  motor1.setSpeed(0); // initialize motor speeds
  motor2.setSpeed(0); // initialize motor speeds
  motor3.setSpeed(0); // initialize motor speeds
//  motor0.run(FORWARD);
//  motor1.run(FORWARD);
  motor2.run(FORWARD);
  motor3.run(FORWARD);
  //Note the steppers and DC are mutually exclusive, so I need to impliment a setup string to cause one or the other to be called
}

void loop()
{

  if (messageBuild() > 0) { // Checks to see if the message is complete and erases any previous messages
    switch (messageGetChar()) { // Gets the first word as a character
    case 'r': // Read pins (analog or digital)
      readpins(); // Call the readpins function
      break; // Break from the switch
    case 'w': // Write pin
      writepin(); // Call the writepin function
      break; // Break from the switch
    case 'v': // Servo Motor
      moveservo(); // Call the servomove function
      break; // Break from the switch
    case 'p': // Stepper Motor
      movestepper(); // Call the servomove function
      break; // Break from the switch
    case 'd': // DC Motor
      moveDCmotor(); // Call the moveDCmotor function
      break; // Break from the switch
    case 'V': // 2 Servo Motor
      move2servo(); // Call the servo2move function
      break; // Break from the switch
    case 'D': // 2 DC Motor
      move2DCmotor(); // Call the move2DCmotor function
      break; // Break from the switch
    case 'F':
      move4DCmotor();
      break;
    }
  }
}

void moveDCmotor(){ // move DC motor
  int pin; // motor number
  int s; // speed
  int t; //time; 0 -> keep on until speed zero called
  int i; //counter
  int dir; //direction;
    messageSendChar('d');  // Echo what is being read
    pin=messageGetInt(); // Get motor number
    messageSendInt(pin); // Echo what is being read
    s=messageGetInt(); // Get speed
    messageSendInt(s); // Echo what is being read
    t=messageGetInt(); // Get time
    messageSendInt(t); // Echo what is being read
    messageEnd(); // Terminate the message being sent
    dir=FORWARD;
    if (s<0) {s=-s;dir=BACKWARD;}
    	if (pin==0) motor0.run(dir);
	if (pin==1) motor1.run(dir);
    if (pin==2) motor2.run(dir);
    if (pin==3) motor3.run(dir);
    if (t==0) {
	    if (pin==0) { motor0.setSpeed(s);}
	    if (pin==1) { motor1.setSpeed(s);}
      if (pin==2) {motor2.setSpeed(s);}// insert move dc command here //
      if (pin==3) {motor3.setSpeed(s);}// insert move dc command here //
    }
    else 
    {
	    if (pin==0) {motor0.setSpeed(s);}
	    if (pin==1) {motor1.setSpeed(s);}
      if (pin==2) {motor2.setSpeed(s);}// insert move dc command here //
      if (pin==3) {motor3.setSpeed(s);}// insert move dc command here //
      for (i=0;i<t;i++) delay(1);
      	   if (pin==0) motor0.setSpeed(0);
	   if (pin==1) motor1.setSpeed(0);
//      if (pin==0) motor0.setSpeed(0);// insert move dc command here //
//      if (pin==1) motor1.setSpeed(0);// insert move dc command here //
      if (pin==2) motor2.setSpeed(0);// insert move dc command here //
      if (pin==3) motor3.setSpeed(0);// insert move dc command here //
    }
}

void move2DCmotor(){ // move DC motor
  int pin1; // motor number
  int s1; // speed
  int pin2; // motor number
  int s2; // speed
  int t; //time; 0 -> keep on until speed zero called
  int i; //counter
  int dir1; // direction 1
  int dir2; //direction 2
    messageSendChar('D');  // Echo what is being read
    pin1=messageGetInt(); // Get motor number
    messageSendInt(pin1); // Echo what is being read
    s1=messageGetInt(); // Get speed
    messageSendInt(s1); // Echo what is being read
    pin2=messageGetInt(); // Get motor number
    messageSendInt(pin2); // Echo what is being read
    s2=messageGetInt(); // Get speed
    messageSendInt(s2); // Echo what is being readt=messageGetInt(); // Get time
    t=messageGetInt(); // Get time
    messageSendInt(t); // Echo what is being read
    messageEnd(); // Terminate the message being sent
    dir1=FORWARD;
    if (s1<0) {s1=-s1;dir1=BACKWARD;}
    dir2=FORWARD;
    if (s2<0) {s2=-s2;dir2=BACKWARD;}
    	if (pin1==0) motor0.run(dir1);
	if (pin1==1) motor1.run(dir1);
	if (pin2==0) motor0.run(dir2);
	if (pin2==1) motor1.run(dir2);
    if (pin1==2) motor2.run(dir1);
    if (pin1==3) motor3.run(dir1);
    if (pin2==2) motor2.run(dir2);
    if (pin2==3) motor3.run(dir2);
   // messageSendChar('D');messageSendInt(pin1);messageSendInt(dir1);messageSendInt(s1);
   // messageSendInt(pin2);messageSendInt(dir2);messageSendInt(s2);
    //messageSendInt(t);messageEnd();
     if (t==0) {
	     if (pin1==0) motor0.setSpeed(s1);
	     if (pin1==1) motor1.setSpeed(s1);
	     if (pin2==0) motor0.setSpeed(s2);
	     if (pin2==1) motor1.setSpeed(s2);
     // if (pin1==0) motor0.setSpeed(s1);// insert move dc command here //
     // if (pin1==1) motor1.setSpeed(s1);// insert move dc command here //
      if (pin1==2) motor2.setSpeed(s1);// insert move dc command here //
      if (pin1==3) motor3.setSpeed(s1);// insert move dc command here //
     // if (pin2==0) motor0.setSpeed(s2);// insert move dc command here //
     // if (pin2==1) motor1.setSpeed(s2);// insert move dc command here //
      if (pin2==2) motor2.setSpeed(s2);// insert move dc command here //
      if (pin2==3) motor3.setSpeed(s2);// insert move dc command here //
    }
    else 
    {
	    if (pin1==0) motor0.setSpeed(s1);
	    if (pin1==1) motor1.setSpeed(s1);
	    if (pin2==0) motor0.setSpeed(s2);
	    if (pin2==1) motor1.setSpeed(s2);
      // if (pin1==0) motor0.setSpeed(s1);// insert move dc command here //
      // if (pin1==1) motor1.setSpeed(s1);// insert move dc command here //
       if (pin1==2) motor2.setSpeed(s1);// insert move dc command here //
       if (pin1==3) motor3.setSpeed(s1);// insert move dc command here //
      // if (pin2==0) motor0.setSpeed(s2);// insert move dc command here //
      // if (pin2==1) motor1.setSpeed(s2);// insert move dc command here //
       if (pin2==2) motor2.setSpeed(s2);// insert move dc command here //
       if (pin2==3) motor3.setSpeed(s2);// insert move dc command here //
      for (i=0;i<t;i++) delay(1); // wait for time then stop motors
      	if (pin1==0) motor0.setSpeed(0);
	if (pin1==1) motor1.setSpeed(0);
	if (pin2==0) motor0.setSpeed(0);
	if (pin2==1) motor1.setSpeed(0);
      // if (pin1==0) motor0.setSpeed(0);// insert move dc command here //
      // if (pin1==1) motor1.setSpeed(0);// insert move dc command here //
       if (pin1==2) motor2.setSpeed(0);// insert move dc command here //
       if (pin1==3) motor3.setSpeed(0);// insert move dc command here //
      // if (pin2==0) motor0.setSpeed(0);// insert move dc command here //
      // if (pin2==1) motor1.setSpeed(0);// insert move dc command here //
       if (pin2==2) motor2.setSpeed(0);// insert move dc command here //
       if (pin2==3) motor3.setSpeed(0);// insert move dc command here //
    }
}

void move4DCmotor(){ // move DC motor
  int pin1; // motor number
  int s1; // speed
  int pin2; // motor number
  int s2; // speed
  int pin3;
  int s3;
  int pin4;
  int s4;
  int t; //time; 0 -> keep on until speed zero called
  int i; //counter
  int dir1; // direction 1
  int dir2; //direction 2
  int dir3;
  int dir4;
    messageSendChar('F');  // Echo what is being read
    pin1=messageGetInt(); // Get motor number
    messageSendInt(pin1); // Echo what is being read
    s1=messageGetInt(); // Get speed
    messageSendInt(s1); // Echo what is being read
    pin2=messageGetInt(); // Get motor number
    messageSendInt(pin2); // Echo what is being read
    s2=messageGetInt(); // Get speed
    messageSendInt(s2); // Echo what is being readt=messageGetInt(); // Get time
    pin3=messageGetInt();
    messageSendInt(pin3);
    s3=messageGetInt();
    messageSendInt(s3);
    pin4=messageGetInt();
    messageSendInt(pin4);
    s4=messageGetInt();
    messageSendInt(s4);
    t=messageGetInt(); // Get time
    messageSendInt(t); // Echo what is being read
    messageEnd(); // Terminate the message being sent
    dir1=FORWARD;
    if (s1<0) {s1=-s1;dir1=BACKWARD;}
    dir2=FORWARD;
    if (s2<0) {s2=-s2;dir2=BACKWARD;}
    dir3=FORWARD;
    if (s3<0) {s3=-s3;dir3=BACKWARD;}
    dir4=FORWARD;
    if (s4<0) {s4=-s4;dir4=BACKWARD;}
    	if (pin1==0) motor0.run(dir1);
	if (pin1==1) motor1.run(dir1);
	if (pin2==0) motor0.run(dir2);
	if (pin2==1) motor1.run(dir2);
	if (pin3==0) motor0.run(dir3);
	if (pin3==1) motor1.run(dir3);
	if (pin4==0) motor0.run(dir4);
	if (pin4==1) motor1.run(dir4);
    if (pin1==2) motor2.run(dir1);
    if (pin1==3) motor3.run(dir1);
    if (pin2==2) motor2.run(dir2);
    if (pin2==3) motor3.run(dir2);
    if (pin3==2) motor2.run(dir3);
    if (pin3==3) motor3.run(dir3);
    if (pin4==2) motor2.run(dir4);
    if (pin4==3) motor3.run(dir4);
   // messageSendChar('D');messageSendInt(pin1);messageSendInt(dir1);messageSendInt(s1);
   // messageSendInt(pin2);messageSendInt(dir2);messageSendInt(s2);
    //messageSendInt(t);messageEnd();
     if (t==0) {
	     if (pin1==0) motor0.setSpeed(s1);
	     if (pin1==1) motor1.setSpeed(s1);
	     if (pin2==0) motor0.setSpeed(s2);
	     if (pin2==1) motor1.setSpeed(s2);
	     if (pin3==0) motor0.setSpeed(s3);
	     if (pin3==1) motor1.setSpeed(s3);
	     if (pin4==0) motor0.setSpeed(s4);
	     if (pin4==1) motor1.setSpeed(s4);
      if (pin1==2) motor2.setSpeed(s1);// insert move dc command here //
      if (pin1==3) motor3.setSpeed(s1);// insert move dc command here //
     // if (pin2==0) motor0.setSpeed(s2);// insert move dc command here //
     // if (pin2==1) motor1.setSpeed(s2);// insert move dc command here //
      if (pin2==2) motor2.setSpeed(s2);// insert move dc command here //
      if (pin2==3) motor3.setSpeed(s2);// insert move dc command here //
      if (pin3==2) motor2.setSpeed(s3);
      if (pin3==3) motor3.setSpeed(s3);
      if (pin4==2) motor2.setSpeed(s4);
      if (pin4==3) motor3.setSpeed(s4);
    }
    else 
    {
	    if (pin1==0) motor0.setSpeed(s1);
	    if (pin1==1) motor1.setSpeed(s1);
	    if (pin2==0) motor0.setSpeed(s2);
	    if (pin2==1) motor1.setSpeed(s2);
	    if (pin3==0) motor0.setSpeed(s3);
	    if (pin3==1) motor1.setSpeed(s3);
	    if (pin4==0) motor0.setSpeed(s4);
	    if (pin4==1) motor1.setSpeed(s4);
      // if (pin1==0) motor0.setSpeed(s1);// insert move dc command here //
      // if (pin1==1) motor1.setSpeed(s1);// insert move dc command here //
       if (pin1==2) motor2.setSpeed(s1);// insert move dc command here //
       if (pin1==3) motor3.setSpeed(s1);// insert move dc command here //
      // if (pin2==0) motor0.setSpeed(s2);// insert move dc command here //
      // if (pin2==1) motor1.setSpeed(s2);// insert move dc command here //
       if (pin2==2) motor2.setSpeed(s2);// insert move dc command here //
       if (pin2==3) motor3.setSpeed(s2);// insert move dc command here //
       if (pin3==2) motor2.setSpeed(s3);
       if (pin3==3) motor3.setSpeed(s3);
       if (pin4==2) motor2.setSpeed(s4);
       if (pin4==3) motor3.setSpeed(s4);
      for (i=0;i<t;i++) delay(1); // wait for time then stop motors
      	if (pin1==0) motor0.setSpeed(0);
	if (pin1==1) motor1.setSpeed(0);
	if (pin2==0) motor0.setSpeed(0);
	if (pin2==1) motor1.setSpeed(0);
	if (pin3==0) motor0.setSpeed(0);
	if (pin3==1) motor1.setSpeed(0);
	if (pin4==0) motor0.setSpeed(0);
	if (pin4==1) motor1.setSpeed(0);
      // if (pin1==0) motor0.setSpeed(0);// insert move dc command here //
      // if (pin1==1) motor1.setSpeed(0);// insert move dc command here //
       if (pin1==2) motor2.setSpeed(0);// insert move dc command here //
       if (pin1==3) motor3.setSpeed(0);// insert move dc command here //
      // if (pin2==0) motor0.setSpeed(0);// insert move dc command here //
      // if (pin2==1) motor1.setSpeed(0);// insert move dc command here //
       if (pin2==2) motor2.setSpeed(0);// insert move dc command here //
       if (pin2==3) motor3.setSpeed(0);// insert move dc command here //
       if (pin3==2) motor2.setSpeed(0);
       if (pin3==3) motor3.setSpeed(0);
       if (pin4==2) motor2.setSpeed(0);
       if (pin4==3) motor3.setSpeed(0);
    }
}


void movestepper(){ // move stepper motor
    messageSendChar('n');  // Echo what is being read
    messageSendChar('o');
  }

void moveservo(){ // move servo motor
  int pin;
  int pos;
    messageSendChar('v');  // Echo what is being read
    pin=messageGetInt(); // Get servo number
    messageSendInt(pin); // Echo what is being read
    pos=messageGetInt(); // Get servo postiion
    messageSendInt(pos); // Echo what is being read
    messageEnd();
    // insert move servo command here //
    if (pin==0) myservo0.write(pos);
    if (pin==1) myservo1.write(pos);
    //messageEnd(); // Terminate the message being sent

  }


void move2servo(){ // move servo motor
  int pos1;
  int pos2;
    messageSendChar('V');  // Echo what is being read
    pos1=messageGetInt(); // Get servo postiion
    messageSendInt(pos1); // Echo what is being read
    pos2=messageGetInt(); // Get servo postiion
    messageSendInt(pos2); // Echo what is being read
    messageEnd();
    myservo0.write(pos1); // move first servo
    myservo1.write(pos2); // move second servo
   // messageEnd(); // Terminate the message being sent
    
  }

void readpins(){ // Read pins (analog or digital)

  switch (messageGetChar()) { // Gets the next word as a character

    case 'd': // READ digital pins

    messageSendChar('d');  // Echo what is being read
    for (char i=2;i<14;i++) {
      messageSendInt(digitalRead(i)); // Read pins 2 to 13
    }
    messageEnd(); // Terminate the message being sent
    break; // Break from the switch

  case 'a': // READ analog pins

    messageSendChar('a');  // Echo what is being read
    for (char i=0;i<6;i++) {
      messageSendInt(analogRead(i)); // Read pins 0 to 5
    }
    messageEnd(); // Terminate the message being sent

  }

}

void writepin() { // Write pin

  int pin;
  int state;

  switch (messageGetChar()) { // Gets the next word as a character

    case 'a' : // WRITE an analog pin

    pin = messageGetInt(); // Gets the next word as an integer
    state = messageGetInt(); // Gets the next word as an integer
    pinMode(pin, OUTPUT); //Sets the state of the pin to an output
    analogWrite(pin, state); //Sets the PWM of the pin 
    break;  // Break from the switch


    // WRITE a digital pin
  case 'd' : 

    pin = messageGetInt();  // Gets the next word as an integer
    state = messageGetInt();  // Gets the next word as an integer
    pinMode(pin,OUTPUT);  //Sets the state of the pin to an output
    digitalWrite(pin,state);  //Sets the state of the pin HIGH (1) or LOW (0)
  }
}

