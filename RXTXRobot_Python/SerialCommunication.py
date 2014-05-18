from __future__ import print_function
import platform
import serial
import glob
import Global


class SerialCommunication:

    BAUD_RATE = 9600

    def __init__(self):
        self.port = None
        self.serial = None

    def setPort(self, port):
        self.port = port

    def connect(self):
        try:
            self.serial = serial.Serial(self.port, SerialCommunication.BAUD_RATE, timeout=3)
            Global.debug("Successfully connected!")
            self.sendWithoutConfirmation("r a")
        except serial.SerialException as e:
            Global.error("Could not connect to the port {} with error:{}\n".format(self.port, e.strerror))
            Global.error("Possible Serial ports:")
            Global.error("\t\n".join(SerialCommunication.list_serial_ports()))

    @staticmethod
    def list_serial_ports():
        system_name = platform.system()
        if system_name == "Windows":
            # Scan for available ports.
            available = []
            for i in range(256):
                try:
                    s = serial.Serial(i)
                    available.append(i)
                    s.close()
                except serial.SerialException:
                    pass
            return available
        elif system_name == "Darwin":
            # Mac
            return glob.glob('/dev/tty.usb*')
        else:
            # Assume Linux or something else
            return glob.glob('/dev/ttyS*') + glob.glob('/dev/ttyUSB*') + glob.glob('/dev/ttyACM*')

    def sendWithoutConfirmation(self, message):
        message += '\r'
        self.serial.write(message.encode())

    def sendRaw(self, message):
        message += '\r'
        self.serial.write(message.encode())
        response = self.serial.readline()
        stringResponse = response.decode()
        stringResponse = stringResponse.rstrip()
        print(stringResponse)

    def close(self):
        self.serial.close()

    @staticmethod
    def setVerbose(verbose):
        Global.verbose = verbose

    @staticmethod
    def sleep(millis):
        Global.sleep(millis)