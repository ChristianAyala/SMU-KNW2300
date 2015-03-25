/* 
----------------------------------------
|          RXTXRobot Firmware          |
----------------------------------------

 To connect to the Arduino without using an external API, you
 can do one of the following:
 
 -- Go to Tools > Serial Monitor. Change the "No Line Ending"
    option to "Both NL & CR", and make sure to use 9600 baud rate,
    if not already set that way. Then type the commands below.

 -- On Mac: In terminal, use the command:
 
    ls /dev/ | grep usb
    
    to get a list of connected USB devices. Pick the one that looks
    like: "tty.usbmodem####", where #### is some number. Connect to
    it through screen with the following command:
    
    screen /dev/tty.usbmodem####
    
    Then type the commands below, hitting enter for each command.
    To exit, type ctrl-a, ctrl-\, y. (ctrl, not command). This method
    is particularly useful when debugging on a student's laptop.

 
 
 
 Control Arduino board functions with the following messages:
 
 ------------------------------
 Configuring Arduino
 ------------------------------
 a m [num] -> attaches motor number [num] as dictated by the layout below
 a s [num] -> attaches servo number [num] as dictated by the layout below
 
 ------------------------------
 Reading sensor values
 ------------------------------
 n -> Gets full version number
 r a -> read analog pins
 r d -> read digital pins
 r t -> read temperature sensor from pin 4
 q [pin] -> Gets a ping result on pin [pin], in centimeters
 c -> Gets a conductivity reading

 ------------------------------
 Controlling motors and servos
 ------------------------------
 v [num] [position] -> move servo number [num] to position [position] (position is [0,180])
 d [num] [speed] [t] -> set dc motor number [num] at speed [speed] for time [t], if t=0 then keep on.
 e [num] [speed] [ticks] -> run encoded dc motor number [num] at speed [speed] for ticks [ticks]
 
 The next 3 do the same thing for 2 motors as close to simultaneously as possible:
 V [position1] [position2] [position3] -> move servos to position 1, 2, and 3 (position is [0,180])
 D [num1] [speed1] [num2] [speed2] [t] -> set dc motor number [num] at speed [speed] for time [t], if t=0 then keep on.
 E [num1] [speed1] [ticks1] [num2] [speed2] [ticks2] -> run 2 encoded dc motors at same time
 
 p [num] -> get the current encoder tick value for motor number [num]
 z [num] -> zero out the encoder tick value for motor number [num]
 m [t] -> sets the ramp-up time for the motors to be [t] milliseconds (rounds down to nearest hundred)
 
 ------------------------------
 Sensor Layout
 ------------------------------

	Digital Pins:
                0/1 - RX/TX Pins, don't use
		2 -  Motor 1 Encoder (hardcoded)
		3 -  Motor 2 Encoder (hardcoded)
		4 -  Free
		5 -  Encoded DC Motor 1 (hardcoded)
		6 -  Encoded DC Motor 2 (hardcoded)
		7 -  Servo, DC Motor or free
		8 -  Servo, DC Motor or free
		9 -  Servo, DC Motor or free
		10 - Servo, DC Motor or free
		11 - Servo, DC Motor or free
		12 - Conductivity Digital Pin 1, or free
		13 - Conductivity Digital Pin 2, or free

	Analog Pins:
		0 - Free
		1 - Free
		2 - Free
		3 - Free
		4 - Conductivity Analog Pin 1
		5 - Conductivity Analog Pin 2
                6 - Free (Arduino Nano only)
                7 - Free (Arduino Nano only)


 
Authors:
 
 Past Contributors:
   -- Ouellet Fredericks
   -- Alexandre Quessy
   -- Marc Christensen
   -- Chris King
 Current Maintainers
   -- Mark Fontenot
   -- Chris Ayala
   -- Charlie Albright
   -- Austin Wells
   -- Luke Oglesbee
   -- Candie Solis
 */

#define BAUD_RATE 9600

#include <SimpleMessageSystem.h>
#include <Servo.h>
#include <Wire.h>
#include <MPU6050.h>

/*
 * firmware version numbers, split into major, minor subminor.
 * must match version numbers in API.
 * Major - requires api update
 * Minor - suggest api update
 * Subminor - no api update needed 
 */
String versionNumber = "n 4 4 0";

//Used to connect to servos (up to 3 per arduino)
Servo servo0, servo1, servo2;
Servo servos[] = { servo0, servo1, servo2 };
int servos_length = 3;

//Used to connect to motors (up to 4 per arduino)
Servo motor0, motor1, encodedMotor0, encodedMotor1;
Servo dc_motors[] = {encodedMotor0, encodedMotor1, motor0, motor1};
int encoders_length = 2;

//Used to track encoder position, distance traveled, direction, etc.
const long forward = 1;
const long backward = -1;
int halt = 1500;
int motorIncrements = 0;
int incrementDelay = 100;
long encoderPositions[] = {0L, 0L};
long encoderTicks[] = {0L, 0L};
long encoderDirections[] = {forward, forward};

//Used to contain the output string written to the serial port
char output[255];

MPU6050 accelgyro;
int16_t ax, ay, az;
int16_t gx, gy, gz;

               //Pins: 0     1     2     3     4      5     6     7      8      9      10     11     12    13
bool pinsAttached[] = {true, true, true, true, false, true, true, false, false, false, false, false, false, false};

void setup()
{
	Serial.begin(BAUD_RATE);

	dc_motors[0].attach(5);
	dc_motors[1].attach(6);

        attachInterrupt(0, incrementEncoder1, RISING);
        attachInterrupt(1, incrementEncoder2, RISING);
        
        accelgyro.initialize();
        accelgyro.testConnection();
        
        //initializeConductivityInterrupt();        
}

void initializeConductivityInterrupt()
{
          //We're attaching a clock interrupt to toggle pin 12 every 1KHz ish
          //for the conductivity sensor. Check out this site for details:
          //http://www.instructables.com/id/Arduino-Timer-Interrupts/step2/Structuring-Timer-Interrupts/
          
          TCCR2A = 0;            //Use default overflow counter
          TCCR2B = _BV(CS22);    //Set the 64 prescaler (triggers every 1.43 ms., check using a scope)
          TCNT2 = 0;             //Clear the interrupt register
          TIMSK2 |= _BV(OCIE2A); //Activate the compare interrupt

}

//Interrupt function for conductivity sensor
ISR(TIMER2_COMPA_vect)
{
        //Toggle pin 12 (bit 4 in PORTB register)
        PORTB ^= (1 << 4);
}

void loop()
{
	if (messageBuild() > 0)
	{
		switch (messageGetChar())
		{
			case 'n':
				getVersionNumber();
				break;
			case 'r':
				readpin();
				break;
			case 'v':
				moveservo();
				break;
			case 'd':
				moveDCmotor();
				break;
			case 'e':
				moveEncodedDCmotor();
				break;
                        case 'p':
                                getEncoderPosition();
                                break;
                        case 'z':
                                zeroEncoderPosition();
                                break;
                        case 'm':
                                setMotorRampUpTime();
                                break;
			case 'V':
				moveAllServo();
				break;
			case 'D':
				move2DCmotor();
				break;
			case 'E':
				move2EncodedDCmotor();
				break;
                        case 'q':
                                getPing();
            	                break;
                        case 'c':
                                getConductivity();
                                break;
                        case 'a':
                                attach();
                                break;
                        case 'g':
                                getGyro();
                                break;
		}
	}
}

void incrementEncoder1()
{
        ++(encoderTicks[0]);
        encoderPositions[0] += encoderDirections[0];
}

void incrementEncoder2()
{
        ++(encoderTicks[1]);
        encoderPositions[1] += encoderDirections[1];
}

void setMotorRampUpTime()
{
        //The ramp-up time for the motors is the increments * delay between increments.
        //Hence the division by the delay to get the requested time. The delay is read-only for now.
        motorIncrements = messageGetInt();
        sprintf(output, "m %i", motorIncrements);
        Serial.println(output);
        motorIncrements /= incrementDelay;
}

void rampUpMotorSpeed(int pin, int speed)
{
        //the time for motor to reach full speed is divisions * delay (in ms.)
        int increment;
        
        if (motorIncrements == 0)
        {
                dc_motors[pin].write(speed);
                return;
        }
        
        increment = (speed - 1500) / motorIncrements;
        
        for (int i = 0; i < motorIncrements; ++i)
        {
                dc_motors[pin].write(halt + (i * increment));
                delay(incrementDelay);
        }
}

void rampUpMotorSpeed(int pin1, int speed1, int pin2, int speed2)
{
        int increment1, increment2;
        
        if (motorIncrements == 0)
        {
                dc_motors[pin1].write(speed1);
                dc_motors[pin2].write(speed2);
                return;
        }
        
        increment1 = (speed1 - 1500) / motorIncrements;
        increment2 = (speed2 - 1500) / motorIncrements;
        
        for (int i = 0; i < motorIncrements; ++i)
        {
                dc_motors[pin1].write(halt + (i * increment1));
                dc_motors[pin2].write(halt + (i * increment2));
                delay(incrementDelay);
        }
}

void moveDCmotor()
{
	int pin, speed, time;
	pin = messageGetInt();
	speed = messageGetInt();
	time = messageGetInt();
	
        sprintf(output, "d %i %i %i", pin, speed, time);
        Serial.println(output);

        speed = constrain(speed + 1500, 1000, 2000);

	if (pin < 0)
		return;
        if (pin < encoders_length)
                encoderDirections[pin] = (speed > halt) ? forward : backward;
        
        rampUpMotorSpeed(pin, speed);      
                
	if (time > 0)
	{
                time -= (motorIncrements * incrementDelay);
                if (time > 0)
		        delay(time);
                dc_motors[pin].write(halt);
	}
        
}

void moveEncodedDCmotor()
{
	int pin, speed, tickInput;
	
	pin = messageGetInt();
	speed = messageGetInt();
	tickInput = messageGetInt();
	        
        long ticks = (long)tickInput;
        speed = constrain(speed + 1500, 1000, 2000);
        
	if (pin < 0)
		return;
        
        encoderTicks[pin] = 0L;
        encoderDirections[pin] = (speed > halt) ? forward : backward;
        
        rampUpMotorSpeed(pin, speed);
        
        while (encoderTicks[pin] < ticks)
        {
                delayMicroseconds(2);
        }
        
        dc_motors[pin].write(halt);
        encoderTicks[pin] = 0L;
        
        sprintf(output, "e %i %i %i", pin, speed-1500, tickInput);
        Serial.println(output);
}

void move2DCmotor()
{
	int pin1, speed1, pin2, speed2, time;
	pin1 = messageGetInt();
	speed1 = messageGetInt();
	pin2 = messageGetInt();
	speed2 = messageGetInt();	
	time = messageGetInt();
        
        sprintf(output, "D %i %i %i %i %i", pin1, speed1, pin2, speed2, time);
        Serial.println(output);
	
        speed1 = constrain(speed1 + 1500, 1000, 2000);
        speed2 = constrain(speed2 + 1500, 1000, 2000);
        
	if (pin1 < 0 || pin2 < 0)
		return;

        if (pin1 < encoders_length)
                encoderDirections[pin1] = (speed1 > halt) ? forward : backward;
        if (pin2 < encoders_length)
                encoderDirections[pin2] = (speed2 > halt) ? forward : backward;
                
        rampUpMotorSpeed(pin1, speed1, pin2, speed2);
        
	if (time > 0)
	{
                time -= (motorIncrements * incrementDelay);
                if (time > 0)
                        delay(time);
		dc_motors[pin1].write(halt);
		dc_motors[pin2].write(halt);
	}
}

void move2EncodedDCmotor()
{
	int pin1, speed1, tickInput1, pin2, speed2, tickInput2;
	pin1 = messageGetInt();
	speed1 = messageGetInt();
	tickInput1 = messageGetInt();
	pin2 = messageGetInt();
	speed2 = messageGetInt();
	tickInput2 = messageGetInt();

        long ticks1 = (long)tickInput1;
        long ticks2 = (long)tickInput2;
        
        speed1 = constrain(speed1 + 1500, 1000, 2000);
        speed2 = constrain(speed2 + 1500, 1000, 2000);
        
	if (pin1 < 0 || pin2 < 0)
		return;

        encoderTicks[pin1] = 0L;
        encoderTicks[pin2] = 0L;
        encoderDirections[pin1] = (speed1 > halt) ? forward : backward;
        encoderDirections[pin2] = (speed2 > halt) ? forward : backward;
        
        rampUpMotorSpeed(pin1, speed1, pin2, speed2);
                
        while (encoderTicks[pin1] < ticks1 || encoderTicks[pin2] < ticks2)
        {
                delayMicroseconds(2);
                if (encoderTicks[pin1] > ticks1)
                        dc_motors[pin1].write(halt);
                if (encoderTicks[pin2] > ticks2)
                        dc_motors[pin2].write(halt);
        }
        
        encoderTicks[pin1] = encoderTicks[pin2] = 0L;
	dc_motors[pin1].write(halt);
	dc_motors[pin2].write(halt);

        sprintf(output, "E %i %i %i %i %i %i", pin1, speed1-1500, tickInput1, pin2, speed2-1500, tickInput2);
        Serial.println(output);
}

void getEncoderPosition()
{
        int pin, position;
        pin = messageGetInt();
        position = int(encoderPositions[pin]);
        
        sprintf(output, "p %i %i", pin, position);
        Serial.println(output);
}

void zeroEncoderPosition()
{
        int pin;
        pin = messageGetInt();
        
        sprintf(output, "z %i", pin);
        Serial.println(output);
        encoderPositions[pin] = 0L;
}

void moveservo()
{
	int pin, position;
	pin = messageGetInt();
	position = messageGetInt();

        sprintf(output, "v %i %i", pin, position);
        Serial.println(output);
        
	if (pin < 0 || pin >= servos_length)
		return;
	servos[pin].write(position);
}

void moveAllServo()
{
	int position1, position2, position3;
	position1 = messageGetInt();
	position2 = messageGetInt();
        position3 = messageGetInt();

        sprintf(output, "V %i %i %i", position1, position2, position3);
        Serial.println(output);

	servos[0].write(position1);
	servos[1].write(position2);
        servos[2].write(position3);
}


void getVersionNumber()
{
        Serial.println(versionNumber);
}

void readpin()
{
        String outputString;
	switch (messageGetChar())
	{
 	 	case 'd':
                        outputString = "d";
                        for (char i=0; i < 14; ++i)
                        {
                                if (pinsAttached[i] == false)
                                {
                                        pinMode(i, INPUT);
                                        outputString += " ";
                                        outputString += digitalRead(i);
                                }
                        }
                        
			break;
		case 'a':
                        outputString = "a";
			for (char i=0;i < 8; ++i)
                        {
                                outputString += " ";
                                outputString += analogRead(i);
                        }
			break;
	}
        Serial.println(outputString);
}

//Check out this site for implementation details:
//http://arduino.cc/en/Tutorial/Ping
void getPing()
{
        long duration;
        int cm;
        int pin = messageGetInt();
        pinMode(pin, OUTPUT);
	digitalWrite(pin, LOW);
	delayMicroseconds(2);
	digitalWrite(pin, HIGH);
	delayMicroseconds(5);
	digitalWrite(pin, LOW);
	pinMode(pin, INPUT);
	duration = pulseIn(pin, HIGH);
	cm = duration / 29 / 2;

        sprintf(output, "q %i %i", pin, cm);
        Serial.println(output);
}

void attach()
{
        int num, pin;
        char component;
        
        component = messageGetChar();
        num = messageGetInt();
        pin = messageGetInt();
        
        //Only do this if the pin hasn't already been attached
        if (pinsAttached[pin] == false) {
                switch(component)
                {
                        //Attaching a motor
                        case 'm':
                                dc_motors[num].attach(pin);
                                break;
                        //Attaching a servo
                        case 's':
                                servos[num].attach(pin);
                                break;
                }
                pinsAttached[pin] = true;
        }
        
        sprintf(output, "a %c %i %i", component, num, pin);
        Serial.println(output);
}

void getConductivity()
{
        const int dPin1 = 12, dPin2 = 13;
        const int aPin1 = 4,  aPin2 = 5;
        const unsigned long seconds = 3;
        int reading1, reading2, result;
        
        //One period of the wave is 10ms. So we want to repeat it numMilliseconds/10ms times
        unsigned long loopCount = (seconds)*100ul;
        
        //In case something is already attached to one of the digital pins
        if (pinsAttached[dPin1] || pinsAttached[dPin2]) {
                Serial.println("c 0");
                return;
        }
        
        pinMode(dPin1, OUTPUT);
        pinMode(dPin2, OUTPUT);
        digitalWrite(dPin1, HIGH);
        digitalWrite(dPin2, HIGH);
        
        //We make an alternating-phase square wave out of digital pins 12/13
        //For this to work, we needed simultaneous digital pin writes. Refer to
        //http://www.arduino.cc/en/Reference/PortManipulation
        for (unsigned long i = 0; i < loopCount; ++i) 
        {
                //The AND turns off pin 13, OR turns on pin 12
                PORTB = B00010000 | (PORTB & B11011111);
                delay(5);
                
                //AND turns off pin 12, OR turns on pin 13
                PORTB = B00100000 | (PORTB & B11101111);
                delay(5);
        }  
      
        reading1 = analogRead(aPin1);
        reading2 = analogRead(aPin2);
        digitalWrite(dPin1, LOW);
        digitalWrite(dPin2, LOW);
        
        result = abs(reading1 - reading2);
        sprintf(output, "c %i", result);
        Serial.println(output);
}

void getGyro() {
    accelgyro.getMotion6(&ax, &ay, &az, &gx, &gy, &gz);
    sprintf(output, "g %i %i %i", ax, ay, az);
    Serial.println(output);
}
