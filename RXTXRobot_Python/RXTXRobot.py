__author__ = 'Chris'

import SerialCommunication
from Global import *


class RXTXRobot(SerialCommunication.SerialCommunication):

    SERVO1 = 0
    SERVO2 = 1
    SERVO3 = 2

    MOTOR1 = 0
    MOTOR2 = 1
    MOTOR3 = 2
    MOTOR4 = 3

    AVAILABLE_DIGITAL_PINS = [4, 11, 12]
    AVAILABLE_ANALOG_PINS = [1, 2, 3, 4, 5, 6]

    def __init__(self, usingArduinoProMini=False):
        super(RXTXRobot, self).__init__()
        self.motorsRunning = [False, False, False, False]

        if usingArduinoProMini:
            RXTXRobot.AVAILABLE_ANALOG_PINS.extend([7, 8])
        self.analogPinCache = [-1 for i in range(len(RXTXRobot.AVAILABLE_ANALOG_PINS))]
        self.digitalPinCache = [-1 for i in range(len(RXTXRobot.AVAILABLE_DIGITAL_PINS))]

    def connect(self):
        print("Connecting to robot, please wait...")
        super().connect()
        self.sleep(2500)
        print("Successfully connected to robot!")

        self.sendRawWithoutConfirmation("r a")

    def close(self):
        debug("Closing robot connection...")
        super().close()
        debug("Connection closed!")

    def numAnalogPins(self):
        return len(RXTXRobot.AVAILABLE_ANALOG_PINS)

    def refreshAnalogPins(self):
        if not self.isConnected():
            error("Robot is not connected!")
            return

        response = self.sendRaw("r a")
        if len(response) == 0:
            error("Empty response from the arduino")

        responseArr = response.split(" ")
        if responseArr.pop(0) != "a":
            error("Invalid command in the analog read response")

        if len(responseArr) != self.numAnalogPins():
            error("Incorrect number of analog pins in response: %d returned" % len(responseArr))

        self.analogPinCache = [int(value) for value in responseArr]
        return self.analogPinCache

    def getAnalogPin(self, pin):
        if not self.isConnected():
            error("Robot is not connected!")
            return

        if pin < 0 or pin >= self.numAnalogPins():
            error("Invalid pin value: %d" % pin)
            return

        if self.analogPinCache[0] == -1:
            self.refreshAnalogPins()

        return self.analogPinCache[pin]

    def numDigitalPins(self):
        return len(RXTXRobot.AVAILABLE_DIGITAL_PINS)

    def refreshDigitalPins(self):
        if not self.isConnected():
            error("Robot is not connected!")
            return

        response = self.sendRaw("r d")
        if len(response) == 0:
            error("Empty response from the arduino")

        responseArr = response.split(" ")
        if responseArr.pop(0) != "d":
            error("Invalid command in the digital read response")

        if len(responseArr) != self.numDigitalPins():
            error("Incorrect number of digital pins in response: %d returned" % len(responseArr))

        self.digitalPinCache = [int(value) for value in responseArr]
        return self.digitalPinCache

    def getDigitalPin(self, pin):
        if not self.isConnected():
            error("Robot is not connected!")
            return

        if pin < 0 or pin >= self.numDigitalPins() or (pin not in RXTXRobot.AVAILABLE_DIGITAL_PINS):
            error("Invalid pin value: %d" % pin)
            return

        if self.digitalPinCache[0] == -1:
            self.refreshDigitalPins()

        return self.digitalPinCache[RXTXRobot.AVAILABLE_DIGITAL_PINS.index(pin)]


