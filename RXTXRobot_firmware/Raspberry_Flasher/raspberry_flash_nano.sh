#!/bin/sh

BASEDIR=$(dirname $0)
git pull origin master
/usr/share/arduino/hardware/tools/avrdude -C/usr/share/arduino/hardware/tools/avrdude.conf -patmega328p -carduino -P/dev/ttyUSB0 -b57600 -D -Uflash:w:$BASEDIR/RXTXRobot_firmware.cpp.hex:i

