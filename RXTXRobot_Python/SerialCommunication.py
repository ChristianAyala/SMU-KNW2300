from __future__ import print_function
import platform
import serial
import glob
from Global import *

class SerialCommunication:

    BAUD_RATE = 9600

    def __init__(self):
        self.port = None
        self.serial = None

    def setPort(self, port):
        self.port = port

    def connect(self):
        if self.port is None:
            error("No port was specified to connect to!")
            error("Call the connect() method with one of these possible ports:")
            error("\n\t".join(SerialCommunication.list_serial_ports()), fatal=True)
        elif self.isConnected():
            error("Robot is already connected!", fatal=True)

        try:
            self.serial = serial.Serial(self.port, SerialCommunication.BAUD_RATE, timeout=3)

        except serial.SerialException as e:
            error("Could not connect to the port {} with error: {}\n".format(self.port, e.strerror))
            error("Possible Serial ports:")
            error("\n\t".join(SerialCommunication.list_serial_ports()), fatal=True)

    @staticmethod
    def list_serial_ports():
        system_name = platform.system()
        available = [""]
        if system_name == "Windows":
            # Scan for available ports.
            for i in range(256):
                try:
                    s = serial.Serial(i)
                    available.append("COM%i" % i)
                    s.close()
                except serial.SerialException:
                    pass
            return available
        elif system_name == "Darwin":
            # Mac
            available.extend(glob.glob('/dev/tty.usb*'))
            return available
        else:
            # Assume Linux or something else
            available.extend(glob.glob('/dev/ttyS*') + glob.glob('/dev/ttyUSB*') + glob.glob('/dev/ttyACM*'))
            return available

    def sendRawWithoutConfirmation(self, message):
        message += '\r'
        self.serial.write(message.encode())
        self.sleep(100)
        self.serial.readline()

    def sendRaw(self, message, sleepMillis=100):

        if not self.isConnected():
            error("Cannot send the message because the robot is not connected")
            return

        debug('Sending command: "%s"' % message)
        self.serial.write((message + "\r").encode())
        self.sleep(sleepMillis)

        response = self.serial.readline()
        response = response.decode()
        response = response.rstrip()

        debug('Received %d bytes from the robot' % len(response))
        debug('Response: "%s"' % response)
        return response

    def close(self):
        self.serial.close()
        self.serial = None

    def isConnected(self):
        return self.serial is not None

    @staticmethod
    def setVerbose(verbose):
        setGlobalVerbose(verbose)

    @staticmethod
    def sleep(millis):
        sleepMillis(millis)