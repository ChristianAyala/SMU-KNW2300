#!/bin/bash

file_prefix=RXTXRobot_v
version_number=
save_dir=$HOME/Desktop
source_dir=$(dirname $0)
echo ""
echo "   RXTXRobot build tool, written by Chris King"
echo "-------------------------------------------------"
echo ""
echo "  Insert a version number to build:"
while [ -z $version_number ]
do
	echo -n "   >> "
	read version_number
done
echo ""
echo "  Location to save the ZIPs:"
echo -n "   [${save_dir}] >> "
read save_dir_input
if [ -z "$save_dir_input" ]
then
	save_dir_input=${save_dir}
fi
while [ ! -d "$save_dir_input" ]
do
	echo "  Error: Directory does not exist"
	echo -n "   [${save_dir}] >> "
	read save_dir_input
	if [ -z "$save_dir_input" ]
	then
		save_dir_input=$save_dir
	fi
done
save_loc="${save_dir_input}/${file_prefix}${version_number}"
if [ -f "${save_loc}_JAR.zip" -o -f "${save_loc}_LIB.zip" ]
then
	echo "\n  Error: Version ${version_number} ZIPs already exists here"
	exit
fi
echo ""
echo "  Source location:"
echo -m "   [${source_dir}] >> "
read source_dir_input
if [ -z "$source_dir_input" ]
then
	source_dir_input=$source_dir
fi
while [ ! -d "$source_dir_input" ]
do
	echo "  Error: Directory does not exist"
	echo -n "   [${source_dir}] >> "
	read source_dir_input
	if [ -z "$source_dir_input" ]
	then
		source_dir_input=$source_dir
	fi
done

echo ""

if [ ! -f "${source_dir_input}/RXTXRobot/store/RXTXRobot.jar" ]
then
	echo "  Error: ${source_dir_input}/RXTXRobot/store/RXTXRobot.jar does not exist"
	exit
fi





















