#!/bin/sh

BASEDIR=$(dirname $0)
sudo apt-get update && sudo apt-get install arduino arduino-core screen
sudo $BASEDIR/alamode-setup/setup
sudo reboot

