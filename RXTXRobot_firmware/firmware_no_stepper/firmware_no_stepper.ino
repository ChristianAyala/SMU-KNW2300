/* 
----------------------------------------
|          RXTXRobot Firmware          |
----------------------------------------


 Control Arduino board functions with the following messages:
 
 r a -> read analog pins
 r d -> read digital pins
 r t -> read temperature sensor
 
 v [num] [position] -> move servo number [num] to position [position] (position is (0,180)
 p [num] [steps] -> move stepper motor [num] in direction [direction] [steps] steps
 d [num] [speed] [t] -> set dc motor number [num] to speed [speed] for time [t], if t=0 then keep on.
 
 The next 3 do the same thing for 2 motors as close to simultaneously as possible:
 V [position1] [position2]-> move servos to position1 and 2 [position] (position is (0,180)
 P [num1] [steps1] [num2] [steps2]-> move 2 stepper motors [num] in direction [direction] [steps] steps
 D [num1] [speed1] [num2] [speed2] [t] -> set dc motor number [num] to speed [speed] for time [t], if t=0 then keep on.
 
 The next 1 does the same thing but for 4 motors as close to simultaneously as possible:
 F [num1] [speed1] [num2] [speed2] [num3] [speed3] [num4] [speed4] [t] -> set dc motor number [num] to speed [speed] for time [t], if t=0 then keep on

 
Authors:
 Base: Thomas Ouellet Fredericks 
 Additions: Alexandre Quessy
 Motor Additions: Marc Christensen
 Temperature addition: Chris King
 Cleanup and complete rewrite: Chris King
 
 */

#define BAUD_RATE 9600


#include <SimpleMessageSystem.h>
#include <Servo.h>
#include <AFMotor.h>
#include <OneWire.h>


AF_DCMotor motor0(1);
AF_DCMotor motor1(2);
AF_DCMotor motor2(3);
AF_DCMotor motor3(4);

AF_DCMotor dc_motors[] = { motor0, motor1, motor2, motor3 };
int dc_motors_length = 4;

Servo servo0;
Servo servo1;

Servo servos[] = { servo0, servo1 };
int servos_length = 2;

OneWire temp0(2);

void setup()
{
	Serial.begin(BAUD_RATE);
	servo0.attach(9);
	servo1.attach(10);
	for (int x=0; x < dc_motors_length; ++x)
	{
		dc_motors[x].setSpeed(0);
		dc_motors[x].run(FORWARD);
	}
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
			case 'V':
				move2servo();
				break;
			case 'D':
				move2DCmotor();
				break;
			case 'F':
				move4DCmotor();
				break;
		}
	}
}


void moveDCmotor()
{
	int pin, speed, time, direction;
	messageSendChar('d');
	pin = messageGetInt();
	messageSendInt(pin);
	speed = messageGetInt();
	messageSendInt(speed);
	time = messageGetInt();
	messageSendInt(time);
	messageEnd();
	direction = FORWARD;
	if (speed < 0)
	{
		speed = -speed;
		direction = BACKWARD;
	}
	if (pin < 0 || pin >= dc_motors_length)
		return;
	dc_motors[pin].run(direction);
	dc_motors[pin].setSpeed(speed);
	if (time != 0)
	{
		delay(time);
		dc_motors[pin].setSpeed(0);
	}
}

void move2DCmotor()
{
	int pin1, speed1, pin2, speed2, time, direction1, direction2;
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
	direction1 = FORWARD;
	if (speed1 < 0)
	{
		speed1 = -speed1;
		direction1 = BACKWARD;
	}
	direction2 = FORWARD;
	if (speed2 < 0)
	{
		speed2 = -speed2;
		direction2 = BACKWARD;
	}
	if (pin1 < 0 || pin1 >= dc_motors_length || pin2 < 0 || pin2 > dc_motors_length)
		return;
	dc_motors[pin1].run(direction1);
	dc_motors[pin2].run(direction2);
	dc_motors[pin1].setSpeed(speed1);
	dc_motors[pin2].setSpeed(speed2);
	if (time != 0)
	{
		delay(time);
		dc_motors[pin1].setSpeed(0);
		dc_motors[pin2].setSpeed(0);
	}
}

void move4DCmotor()
{
	int pin1, speed1, pin2, speed2, pin3, speed3, pin4, speed4, time, direction1, direction2, direction3, direction4;
	messageSendChar('F');
	pin1 = messageGetInt();
	messageSendInt(pin1);
	speed1 = messageGetInt();
	messageSendInt(speed1);
	pin2 = messageGetInt();
	messageSendInt(pin2);
	speed2 = messageGetInt();
	messageSendInt(speed2);
	pin3 = messageGetInt();
	messageSendInt(pin3);
	speed3 = messageGetInt();
	messageSendInt(speed3);
	pin4 = messageGetInt();
	messageSendInt(pin4);
	speed4 = messageGetInt();
	messageSendInt(speed4);
	time = messageGetInt();
	messageSendInt(time);
	messageEnd();
	direction1 = direction2 = direction3 = direction4 = FORWARD;
	if (speed1 < 0)
	{
		speed1 = -speed1;
		direction1 = BACKWARD;
	}
	if (speed2 < 0)
	{
		speed2 = -speed2;
		direction2 = BACKWARD;
	}
	if (speed3 < 0)
	{
		speed3 = -speed3;
		direction3 = BACKWARD;
	}
	if (speed4 < 0)
	{
		speed4 = -speed4;
		direction4 = BACKWARD;
	}
	if (pin1 < 0 || pin1 >= dc_motors_length || pin2 < 0 || pin2 >= dc_motors_length || pin3 < 0 || pin3 > dc_motors_length || pin4 < 0 || pin4 > dc_motors_length)
		return;
	dc_motors[pin1].run(direction1);
	dc_motors[pin2].run(direction2);
	dc_motors[pin3].run(direction3);
	dc_motors[pin4].run(direction4);
	dc_motors[pin1].setSpeed(speed1);
	dc_motors[pin2].setSpeed(speed2);
	dc_motors[pin3].setSpeed(speed3);
	dc_motors[pin4].setSpeed(speed4);
	if (time != 0)
	{
		delay(time);
		dc_motors[pin1].setSpeed(0);
		dc_motors[pin2].setSpeed(0);
		dc_motors[pin3].setSpeed(0);
		dc_motors[pin4].setSpeed(0);
	}
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
			pinMode(13, INPUT);
			messageSendInt(digitalRead(13));
			messageEnd();
			break;
		case 'a':
			messageSendChar('a');
			for (char i=0;i < 6; ++i)
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

