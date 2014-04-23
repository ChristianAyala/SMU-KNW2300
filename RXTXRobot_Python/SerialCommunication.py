import serial


class SerialCommunication:

    BAUD_RATE = 9600

    def __init__(self):
        self.port = None
        self.connection = None

    def setPort(self, port):
        self.port = port

    def connect(self):
        try:
            self.connection = serial.Serial(self.port, SerialCommunication.BAUD_RATE)
            print("Successfully connected!")
        except serial.SerialException as e:
            print(format(e.errno, e.strerror))