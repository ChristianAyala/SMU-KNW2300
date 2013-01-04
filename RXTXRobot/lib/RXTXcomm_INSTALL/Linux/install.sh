#!/bin/bash

# RXTXRobot Linux Installation Script
# Written by Christopher King

echo "========================================"
echo "|   RXTXRobot Installation for Linux   |"
echo "========================================"
echo ""
echo "Gaining root privileges..."
sudo touch getRoot
if [ "$?" -ne 0 ]
then
	echo "FATAL ERROR: Could not get root access.  Make sure you are in the sudoers file!"
	exit
fi
sudo rm getRoot
echo ""
echo "Looking for Java installation..."
while [ ! "$JAVA_HOME"  -o -f "$JAVA_HOME/jre/lib" ]
do
	echo ""
	echo "JAVA_HOME was not found.  Please input the location of your Java installation (\$JAVA_HOME/jre/lib must exist)."
	echo -n "(JAVA_HOME)-> "
	read $input
	JAVA_HOME=$input
done

echo "JAVA_HOME was found successfully!"
echo ""
echo "Looking for libraries..."
if [ ! -f libs/librxtxSerial_x64.so -o ! -f libs/librxtxSerial_x86.so ]
then
	echo "FATAL ERROR:libs folder was not found to be correct.  Make sure the \"libs\" folder is in the same directory as this script."
	exit
fi
echo "Found libraries!"
echo ""
echo "Looking for 32-bit or 64-bit..."
LIBFILE=libs/
ARCH=`uname -m`

if [ "$ARCH" == "x86_64" ]
then
	LIBFILE=libs/librxtxSerial_x64.so
	echo "Found 64-bit!"
else
	LIBFILE=libs/librxtxSerial_x86.so
	echo "Found 32-bit!"
fi
echo ""
echo "Finding installation location..."
INSTLOC=
if [ -d "$JAVA_HOME/jre/lib/amd64" ]
then
	INSTLOC=$JAVA_HOME/jre/lib/amd64
	echo "Found amd64!"
elif [ -d "$JAVA_HOME/jre/lib/i386" ]
then
	INSTLOC=$JAVA_HOME/jre/lib/i386
	echo "Found i386!"
else
	while [ ! "$INSTLOC" -o ! -d "$INSTLOC" ]
	do
		echo "Possible Directory List:"
		ls -l "$JAVA_HOME/jre/lib/" | grep -P '^d' | awk '{print $9}' | sed "s/^/      /"
		echo "Could not find installation location!  Please choose from the above list."
		echo "Location :-> "
		read $input
		INSTLOC=$JAVA_HOME/jre/lib/$input
	done
fi
echo "Location found!"
echo ""
echo "Installing Libraries..."
sudo cp "$LIBFILE" "$INSTLOC/librxtxSerial.so"
if [ $? -ne 0 ]
then
	echo "FATAL ERROR: Could not install libraries correctly"
	exit
else
	echo "Libraries installed correctly!"
fi
echo ""
echo "Adding user to the required groups..."
sudo usermod -a -G lock,uucp,dialout "$USER" &> /dev/null
echo "User \"$USER\" added to the \"lock\", \"uucp\", and \"dialout\" groups successfully!"
echo ""
echo "Setting the lock file permissions..."
sudo mkdir -p /lock && sudo chmod -R 777 /lock && sudo chmod -R 777 /run/lock && sudo chmod -R 777 /var/lock 2> /dev/null
echo "Permissions set and complete!"
echo ""
echo ""
echo ""
echo "Installation is complete!"
echo ""
echo "Note: In Linux, Arduino devices will be \"/dev/ttyACM0\" and XBee devices will be \"/dev/ttyUSB0\""


