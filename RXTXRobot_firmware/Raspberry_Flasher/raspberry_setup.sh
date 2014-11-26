#!/bin/sh

BASEDIR=$(dirname $0)
sudo apt-get update && sudo apt-get install arduino arduino-core screen
cd $BASEDIR/alamode-setup
sudo ./setup
sudo reboot

