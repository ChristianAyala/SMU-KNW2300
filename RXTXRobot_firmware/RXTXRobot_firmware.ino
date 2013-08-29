/* 
----------------------------------------
|          RXTXRobot Firmware          |
----------------------------------------


 Control Arduino board functions with the following messages:
 
 r a -> read analog pins
 r d -> read digital pins
 r t -> read temperature sensor from pin 2
 q -> Gets a ping result on pin 13
 c -> Get compass reading from SDA on pin 4, SCL on pin 5
 

 v [num] [position] -> move servo number [num] to position [position] (position is (0,180)
 d [num] [speed] [t] -> set dc motor number [num] to speed [speed] for time [t], if t=0 then keep on.
 e [num] [speed] [ticks] -> run encoded dc motor number [num] to speed [speed] for ticks [ticks]
 
 The next 3 do the same thing for 2 motors as close to simultaneously as possible:
 V [position1] [position2]-> move servos to position1 and 2 [position] (position is (0,180)
 D [num1] [speed1] [num2] [speed2] [t] -> set dc motor number [num] to speed [speed] for time [t], if t=0 then keep on.
 E [num1] [speed1] [ticks1] [num2] [speed2] [ticks2] -> run 2 encoded dc motors at same time



Sensor layout:
	Digital Pins:
		1 - Unused
		2 - Temperature Sensor
		3 - DC Motor
		4 - DC Motor
		5 - Encoded DC Motor 1
		6 - Encoded DC Motor 2
		7 - Unused
		8 - Unused
		9 - Servo
		10 - Servo
		11 - Unused
		12 - Unused
		13 - Ping Sensor

	Analog Pins:
		0 - Encoded DC Motor 1
		1 - Encoded DC Motor 2
		2 - Unused
		3 - Unused
		4 - Compass
		5 - Compass


 
Authors:
 Base: Thomas Ouellet Fredericks 
 Additions: Alexandre Quessy
 Motor Additions: Marc Christensen
 Temperature addition: Chris King
 Cleanup and complete rewrite: Chris King
 
ATTENTION:
NEW CHANGES FOR VERSION 4:
  + added speed control for DC motors
    + speed range is now -500 to 500
    + safeguards are in place if speed inputted is over 500 or under -500
  * fixed encoding loops for DC motors to count low-to-high tick readings
  
  
  
 */

#define BAUD_RATE 9600


#include <SimpleMessageSystem.h>
#include <Servo.h>
#include <AFMotor.h>
#include <OneWire.h>
#include <Wire.h>

int HMC6352Address = 0x42;
int slaveAddress;

Servo servo0;
Servo servo1;

Servo servos[] = { servo0, servo1 };
int servos_length = 2;

Servo motor0;
Servo motor1;
Servo encodedMotor0;
Servo encodedMotor1;
Servo dc_motors[] = {motor0, motor1, encodedMotor0, encodedMotor1};
int dc_motors_length = 2;
int halt = 1500;

OneWire temp0(2);


void setup()
{
	Serial.begin(BAUD_RATE);
	servo0.attach(9);
	servo1.attach(10);

	motor0.attach(3);
	motor1.attach(4);
	encodedMotor0.attach(5);
	encodedMotor1.attach(6);

        slaveAddress = HMC6352Address >> 1;
        Wire.begin();
}

void loop()
{
	if (messageBuild() > 0)
	{
		switch (messageGetChar())
		{
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
			case 'V':
				move2servo();
				break;
			case 'D':
				move2DCmotor();
				break;
			case 'E':
				move2EncodedDCmotor();
				break;
            case 'q':
                ping();
            	break;
            case 'c':
                compass();
                break;
		}
	}
}


void moveDCmotor()
{
	int pin, speed, time;
	messageSendChar('d');
	pin = messageGetInt();
	messageSendInt(pin);
	speed = messageGetInt();
	messageSendInt(speed);
	time = messageGetInt();
	messageSendInt(time);
	messageEnd();
        
        speed += 1500;

	if (pin < 0)
		return;
        if(speed > 2000)
                dc_motors[pin].write(2000);
        else if(speed < 1000)
                dc_motors[pin].write(1000);
        else
                dc_motors[pin].write(speed);
                
	if (time != 0)
	{
		delay(time);
		dc_motors[pin].write(halt);
	}
}

void moveEncodedDCmotor()
{
	int pin, speed, ticks;
	messageSendChar('e');
	pin = messageGetInt();
	messageSendInt(pin);
	speed = messageGetInt();
	messageSendInt(speed);
	ticks = messageGetInt();
	messageSendInt(ticks);
	messageEnd();

        speed += 1500;
        
	if (pin < 0)
		return;
        if(speed > 2000)
                dc_motors[pin].write(2000);
        else if(speed < 1000)
                dc_motors[pin].write(1000);
        else
                dc_motors[pin].write(speed);
        
        int newTick;
        int oldTick = analogRead(pin - 2);
	while (ticks > 0) //new encoding loop, counts low-to-high tick iterations
	{
                newTick = analogRead(pin - 2);
                if(oldTick < 512 && newTick > 512)
                        ticks--;
                oldTick = newTick;
	}
	dc_motors[pin].write(halt);
}

void move2DCmotor()
{
	int pin1, speed1, pin2, speed2, time;
	messageSendChar('D');
	pin1 = messageGetInt();
	messageSendInt(pin1);
	speed1 = messageGetInt();
	messageSendInt(speed1);
	pin2 = messageGetInt();
	messageSendInt(pin2);
	speed2 = messageGetInt();
	messageSendInt(speed2);
	time = messageGetInt();
	messageSendInt(time);
	messageEnd();
	
        speed1 += 1500;
        speed2 += 1500;
        
	if (pin1 < 0 || pin2 < 0)
		return;

        if(speed1 > 2000)
                dc_motors[pin1].write(2000);
        else if(speed1 < 1000)
                dc_motors[pin1].write(1000);
        else
	        dc_motors[pin1].write(speed1);

        if(speed2 > 2000)
                dc_motors[pin2].write(2000);
        else if(speed2 < 1000)
                dc_motors[pin2].write(1000);
        else
                dc_motors[pin2].write(speed2);
                
	if (time != 0)
	{
		delay(time);
		dc_motors[pin1].write(halt);
		dc_motors[pin2].write(halt);
	}
}

void move2EncodedDCmotor()
{
	int pin1, speed1, ticks1, pin2, speed2, ticks2;
	messageSendChar('E');
	pin1 = messageGetInt();
	messageSendInt(pin1);
	speed1 = messageGetInt();
	messageSendInt(speed1);
	ticks1 = messageGetInt();
	messageSendInt(ticks1);
	pin2 = messageGetInt();
	messageSendInt(pin2);
	speed2 = messageGetInt();
	messageSendInt(speed2);
	ticks2 = messageGetInt();
	messageSendInt(ticks2);
	messageEnd();

        speed1 += 1500;
        speed2 += 1500;
	if (pin1 < 0 || pin2 < 0)
		return;

        if(speed1 > 2000)
                dc_motors[pin1].write(2000);
        else if(speed1 < 1000)
                dc_motors[pin1].write(1000);
        else
	        dc_motors[pin1].write(speed1);

        if(speed2 > 2000)
                dc_motors[pin2].write(2000);
        else if(speed2 > 1000)
                dc_motors[pin2].write(1000);
        else
                dc_motors[pin2].write(speed2);
                
        int newTick1, newTick2;
        int oldTick1 = analogRead(pin1 - 2);
        int oldTick2 = analogRead(pin2 - 2);
                
	while (ticks1 > 0 || ticks2 > 0) //another new encoding loop
	{
		newTick1 = analogRead(pin1 - 2);
                newTick2 = analogRead(pin2 - 2);
                
                if(oldTick1 < 512 && newTick1 > 512)
                        ticks1--;
                if(oldTick2 < 512 && newTick2 > 512)
                        ticks2--;
                if(ticks1 <= 0)
                        dc_motors[pin1].write(halt);
                if(ticks2 <= 0)
                        dc_motors[pin2].write(halt);
                oldTick1 = newTick1;
                oldTick2 = newTick2;
	}
	dc_motors[pin1].write(halt);
	dc_motors[pin2].write(halt);
}

void moveservo()
{
	int pin, position;
	messageSendChar('v');
	pin = messageGetInt();
	messageSendInt(pin);
	position = messageGetInt();
	messageSendInt(position);
	messageEnd();
	if (pin < 0 || pin >= servos_length)
		return;
	servos[pin].write(position);
}

void move2servo()
{
	int position1, position2;
	messageSendChar('V');
	position1 = messageGetInt();
	messageSendInt(position1);
	position2 = messageGetInt();
	messageSendInt(position2);
	messageEnd();
	servos[0].write(position1);
	servos[1].write(position2);
}

void readpin()
{
	int maximum_allowed_temperature = 50;
	int temperature_reading = maximum_allowed_temperature+1;
	int retries = 10;
	switch (messageGetChar())
	{
		case 'd':
			messageSendChar('d');
			pinMode(1, INPUT);
			pinMode(7, INPUT);
			pinMode(8, INPUT);
			pinMode(11, INPUT);
			pinMode(12, INPUT);
			messageSendInt(digitalRead(1));
			messageSendInt(digitalRead(7));
			messageSendInt(digitalRead(8));
			messageSendInt(digitalRead(11));
			messageSendInt(digitalRead(12));
			messageEnd();
			break;
		case 'a':
			messageSendChar('a');
			for (char i=2;i < 4; ++i)
				messageSendInt(analogRead(i));
			messageEnd();
			break;
		case 't':
			do
			{
				temperature_reading = (int)getTemp();
				--retries;
			}
			while ((temperature_reading > maximum_allowed_temperature || temperature_reading <= -1000) && retries > 0);
			messageSendChar('t');
			messageSendInt(temperature_reading);
			messageEnd();
			break;
	}
}

// This function is to get the temperature (in Celcius) from the sensor.
// It is taken from http://bildr.org/2011/07/ds18b20-arduino/
float getTemp()
{
	byte data[12];
	byte addr[8];
	if (!temp0.search(addr))
	{
		temp0.reset_search();
		return -1001;
	}
	if (OneWire::crc8(addr, 7) != addr[7])
	{
		return -1002;
	}
	if (addr[0] != 0x10 && addr[0] != 0x28)
	{
		return -1003;
	}
	temp0.reset();
	temp0.select(addr);
	temp0.write(0x44,1);
	delay(750);
	byte present = temp0.reset();
	temp0.select(addr);
	temp0.write(0xBE);
	for (int i=0; i < 9; ++i)
		data[i] = temp0.read();
	temp0.reset_search();
	byte MSB = data[1];
	byte LSB = data[0];
	float tempRead = ((MSB << 8) | LSB);
	return (tempRead / 16);
}

void ping()
{
	long duration;
        int cm;
	messageSendChar('q');
        pinMode(13, OUTPUT);
	digitalWrite(13, LOW);
	delayMicroseconds(2);
	digitalWrite(13, HIGH);
	delayMicroseconds(5);
	digitalWrite(13, LOW);
	pinMode(13, INPUT);
	duration = pulseIn(13, HIGH);
	cm = duration / 29 / 2;
        messageSendInt(cm);
        messageEnd();
}

void compass()
{
        byte headingData[2];
        int i, headingValue;
        messageSendChar('c');
        Wire.beginTransmission(slaveAddress);
        Wire.write("A");              // The "Get Data" command
        Wire.endTransmission();
        delay(10);                   // The HMC6352 needs at least a 70us (microsecond) delay
        // after this command.  Using 10ms just makes it safe
        // Read the 2 heading bytes, MSB first
        // The resulting 16bit word is the compass heading in 10th's of a degree
        // For example: a heading of 1345 would be 134.5 degrees
        Wire.requestFrom(slaveAddress, 2);        // Request the 2 byte heading (MSB comes first)
        i = 0;
      //  while(Wire.available() && i < 2)
        while(i < 2)
        { 
                headingData[i] = Wire.read();
                i++;
        }
        headingValue = headingData[0]*256 + headingData[1];  // Put the MSB and LSB together
        messageSendInt(int (headingValue / 10));
        messageEnd();
}

