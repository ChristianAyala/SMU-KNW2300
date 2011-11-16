#!/bin/bash
if [ $# -ne 1 ] ; then
	echo "Run script: ./build.sh [options] version_number"
	exit
fi
echo "------------------------------------------------------------"
echo "|          Building version $1 of RXTXRobot Project        |"
echo "------------------------------------------------------------"
DIR="$HOME/Desktop/RXTXRobot_v$1"
if [ -d "$DIR" -o -e "$DIR.zip" ] ; then
	echo -n "WARNING: Version $1 already exists.  Overwrite? (y,n) "
	read input
	if [ "$input" = "y" ] ; then
		rm -rf "$DIR"
		rm -f "$DIR.zip"
	else
		echo "Exiting...."
		exit
	fi
fi
echo -n "         Making directory..............."
mkdir "$DIR"
echo "done"
echo -n "         Moving main readme............."
cp "$HOME/NetBeansProjects/Arduino/RXTXRobot/lib/README.txt" "$DIR/README.txt"
echo "done"
echo -n "         Moving RXTXcomm_INSTALL........"
cp -r "$HOME/NetBeansProjects/Arduino/RXTXRobot/lib/RXTXcomm_INSTALL" "$DIR/RXTXcomm_INSTALL"
echo "done"
echo -n "         Moving Javadocs................"
cp -r "$HOME/NetBeansProjects/Arduino/RXTXRobot/dist/javadoc" "$DIR/javadocs"
echo "done"
echo -n "         Moving API JAR................."
cp "$HOME/NetBeansProjects/Arduino/RXTXRobot/store/RXTXRobot.jar" "$DIR/RXTXRobot.jar"
echo "done"
echo -n "         Zipping up API................."
cd "$HOME/Desktop"
zip -r -9 -q "RXTXRobot_v$1.zip" -xi "RXTXRobot_v$1"
echo "done"
echo -n "         Cleaning up...................."
rm -rf "$DIR"
echo "done"
echo ""
echo "**************************************************************"
echo "*          RXTXRobot API version $1 built successfully       *"
echo "**************************************************************"
