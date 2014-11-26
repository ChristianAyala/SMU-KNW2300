#!/bin/sh

git pull origin master
/usr/share/arduino/hardware/tools/avrdude -C/usr/share/arduino/hardware/tools/avrdude.conf -patmega328p -carduino -P/dev/ttyACM0 -b115200 -D -Uflash:w:/home/pi/SMU-KNW2300/RXTXRobot_firmware/RXTXRobot_firmware.cpp.hex:i

