/* 
----------------------------------------
|          RXTXRobot Firmware          |
----------------------------------------


 Control Arduino board functions with the following messages:
 
 r a -> read analog pins
 r d -> read digital pins
 r t -> read temperature sensor from pin 4
 q -> Gets a ping result on pin 13, in centimeters
 c -> Gets a conductivity reading

 v [num] [position] -> move servo number [num] to position [position] (position is (0,180)
 d [num] [speed] [t] -> set dc motor number [num] at speed [speed] for time [t], if t=0 then keep on.
 e [num] [speed] [ticks] -> run encoded dc motor number [num] at speed [speed] for ticks [ticks]
 p [num] -> get the current encoder tick value for motor number [num]
 z [num] -> zero out the encoder tick value for motor number [num]
 
 The next 3 do the same thing for 2 motors as close to simultaneously as possible:
 V [position1] [position2] -> move servos to position 1 and 2 [position] (position is (0,180)
 D [num1] [speed1] [num2] [speed2] [t] -> set dc motor number [num] at speed [speed] for time [t], if t=0 then keep on.
 E [num1] [speed1] [ticks1] [num2] [speed2] [ticks2] -> run 2 encoded dc motors at same time



Sensor layout:
	Digital Pins:
		1 - Unused
		2 - Motor 1 Encoder
		3 - Motor 2 Encoder
		4 - Temperature
		5 - Encoded DC Motor 1
		6 - Encoded DC Motor 2
		7 - DC Motor 1
		8 - DC Motor 2
		9 - Servo
		10 - Servo
		11 - Conductivity Digital Pin 1
		12 - Conductivity Digital Pin 2
		13 - Ping Sensor

	Analog Pins:
		0 - Unused
		1 - Unused
		2 - Unused
		3 - Unused
		4 - Conductivity Analog Pin 1
		5 - Conductivity Analog Pin 2


 
Authors:
 Base: Thomas Ouellet Fredericks 
 Additions: Alexandre Quessy
 Motor Additions: Marc Christensen
 Temperature addition: Chris King
 Cleanup and complete rewrite: Chris King
 Version 4 Update: Charlie Albright
 Conductivity addition: Chris Ayala
 
ATTENTION:
NEW CHANGES FOR VERSION 4:
  + added speed control for DC motors
    + speed range is now -500 to 500 (based on pulse width)
    + safeguards are in place if speed inputted is over 500 or under -500
  * fixed encoding logic by implementing interrupt pins on Digital 2 and 3 
    * allows for more accurate tracking of ticks as well as speed control
  - got rid of the compass implementation to free up Analog pin space
  |||CLEANED AND TESTED|||
  + Motors with encoders can now track how far they've travelled.
 */

#define BAUD_RATE 9600


#include <SimpleMessageSystem.h>
#include <Servo.h>
#include <AFMotor.h>
#include <OneWire.h>

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
Servo dc_motors[] = {encodedMotor0, encodedMotor1, motor0, motor1};

const long forward = 1;
const long backward = -1;
int halt = 1500;
int encoders_length = 2;
long encoderPositions[] = {0L, 0L};
long encoderTicks[] = {0L, 0L};
long encoderDirections[] = {forward, backward};

OneWire temp0(4);


void setup()
{
	Serial.begin(BAUD_RATE);
	servo0.attach(9);
	servo1.attach(10);

	encodedMotor0.attach(5);
	encodedMotor1.attach(6);
        motor0.attach(7);
	motor1.attach(8);

        attachInterrupt(0, incrementEncoder1, RISING);
        attachInterrupt(1, incrementEncoder2, RISING);
        
        slaveAddress = HMC6352Address >> 1;
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
                        case 'p':
                                getEncoderPosition();
                                break;
                        case 'z':
                                zeroEncoderPosition();
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
                                getPing();
            	                break;
                        case 'c':
                                getConductivity();
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
        if (pin < encoders_length)
                encoderDirections[pin] = (speed > halt) ? forward : backward;
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
	int pin, speed, tickInput;
	
	pin = messageGetInt();
	speed = messageGetInt();
	tickInput = messageGetInt();
	        
        long ticks = (long)tickInput * 100;
        speed += 1500;
        
	if (pin < 0)
		return;
        
        encoderTicks[pin] = 0L;

        encoderDirections[pin] = (speed > halt) ? forward : backward;
        if(speed > 2000)
                dc_motors[pin].write(2000);
        else if(speed < 1000)
                dc_motors[pin].write(1000);
        else
                dc_motors[pin].write(speed);
        
        while (encoderTicks[pin] < ticks)
        {
                delayMicroseconds(2);
        }
        
        dc_motors[pin].write(halt);
        encoderTicks[pin] = 0L;
        
        messageSendChar('e');
        messageSendInt(pin);
        messageSendInt(speed-1500);
        messageSendInt(tickInput);
        messageEnd();
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

        if (pin1 < encoders_length)
                encoderDirections[pin1] = (speed1 > halt) ? forward : backward;
        if (pin2 < encoders_length)
                encoderDirections[pin2] = (speed2 > halt) ? forward : backward;
        
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
	int pin1, speed1, tickInput1, pin2, speed2, tickInput2;
	pin1 = messageGetInt();
	speed1 = messageGetInt();
	tickInput1 = messageGetInt();
	pin2 = messageGetInt();
	speed2 = messageGetInt();
	tickInput2 = messageGetInt();

        long ticks1 = (long)tickInput1 * 100;
        long ticks2 = (long)tickInput2 * 100;
        
        speed1 += 1500;
        speed2 += 1500;
	if (pin1 < 0 || pin2 < 0)
		return;

        encoderTicks[pin1] = 0L;
        encoderTicks[pin2] = 0L;
        encoderDirections[pin1] = (speed1 > halt) ? forward : backward;
        encoderDirections[pin2] = (speed2 > halt) ? forward : backward;
        
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
                
        while (encoderTicks[pin1] < ticks1 || encoderTicks[pin2] < ticks2)
        {
                delayMicroseconds(2);
                if (encoderTicks[pin1] > ticks1)
                        dc_motors[pin1].write(halt);
                if (encoderTicks[pin2] > ticks2)
                        dc_motors[pin2].write(halt);
        }
        
        /*
        if(pin1 == 0 && pin2 == 1)
        {
                while(encoder1 < ticks1 || encoder2 < ticks2)
                {
                        if(encoder1 >= ticks1)
                                dc_motors[pin1].write(halt);
                        if(encoder2 >= ticks2)
                                dc_motors[pin2].write(halt);
                }
        }
        else if(pin1 == 1 && pin2 == 0)
        {
                while(encoder1 < ticks2 || encoder2 < ticks1)
                {
                        if(encoder1 >= ticks2)
                                dc_motors[pin2].write(halt);
                        if(encoder2 >= ticks1)
                                dc_motors[pin1].write(halt);
                }
        }
        
        else
                return;
        */
        
        //encoder1 = 0;
        //encoder2 = 0;
        encoderTicks[pin1] = encoderTicks[pin2] = 0L;
	dc_motors[pin1].write(halt);
	dc_motors[pin2].write(halt);

        messageSendChar('E');
        messageSendInt(pin1);
        messageSendInt(speed1-1500);
        messageSendInt(tickInput1);
        messageSendInt(pin2);
        messageSendInt(speed2-1500);
        messageSendInt(tickInput2);
        messageEnd();
}

void getEncoderPosition()
{
        int pin;
        messageSendChar('p');
        pin = messageGetInt();
        messageSendInt(pin);
        messageSendInt(int(encoderPositions[pin]/100));
        messageEnd();
}

void zeroEncoderPosition()
{
        int pin;
        messageSendChar('z');
        pin = messageGetInt();
        messageSendInt(pin);
        encoderPositions[pin] = 0L;
        messageEnd();
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
			pinMode(11, INPUT);
			pinMode(12, INPUT);
			messageSendInt(digitalRead(1));
			messageSendInt(digitalRead(11));
			messageSendInt(digitalRead(12));
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

void getPing()
{
	long duration;
        int cm;
        int pin = 12;
	messageSendChar('q');
        pinMode(pin, OUTPUT);
	digitalWrite(pin, LOW);
	delayMicroseconds(2);
	digitalWrite(pin, HIGH);
	delayMicroseconds(5);
	digitalWrite(pin, LOW);
	pinMode(pin, INPUT);
	duration = pulseIn(pin, HIGH);
	cm = duration / 29 / 2;
        messageSendInt(cm);
        messageEnd();
}

void getConductivity()
{
        int dPin1 = 11, dPin2 = 12;
        int aPin1 = 4,  aPin2 = 5;
        int reading1, reading2;
        int readingTime = messageGetInt();
        unsigned long loopCount = ((unsigned long)readingTime)*100ul*1000ul;
        
        pinMode(dPin1, OUTPUT);
        pinMode(dPin2, OUTPUT);
        digitalWrite(dPin1, LOW);
        digitalWrite(dPin2, LOW);
        
        //For this to work, we needed simultaneous digital pin writes. This
        //was not possible with digitalWrite(). Refer to
        //http://www.arduino.cc/en/Reference/PortManipulation
        for (unsigned long i = 0; i < loopCount; ++i) 
        {
                //The AND turns off pin 12, OR turns on pin 11
                PORTB = B00001000 | (PORTB & B11101111);
                delayMicroseconds(5);

                //AND turns off pin 11, OR turns on pin 12
                PORTB = B00010000 | (PORTB & B11110111);
                delayMicroseconds(5);
        }  
      
        reading1 = analogRead(aPin1);
        reading2 = analogRead(aPin2);
        messageSendChar('c');
        messageSendInt(readingTime);
        messageSendInt(abs(reading1 - reading2));
        digitalWrite(dPin1, LOW);
        digitalWrite(dPin2, LOW);

        messageEnd();
}

