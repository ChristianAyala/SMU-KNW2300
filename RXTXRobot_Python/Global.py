__author__ = 'Chris'

import sys
import time


verbose = False


def debug(message):
    if verbose:
        print("----> " + message, file=sys.stdout)


def error(message):
    print("----> " + message, file=sys.stderr)


def sleep(millis):
    seconds = millis/1000
    debug("Sleeping for {} seconds".format(seconds))
    time.sleep(millis/1000)