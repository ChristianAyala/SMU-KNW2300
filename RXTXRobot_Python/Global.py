__author__ = 'Chris'

import sys
import time
import inspect


verbose = True


def debug(message):
    if verbose:
        print("----> " + message, file=sys.stdout)
        sys.stdout.flush()


def error(message, fatal=False):
    frm = inspect.stack()[1]
    mod = inspect.getmodule(frm[0])
    print("----> [%s.%s] %s " % (mod.__name__, frm[3], message), file=sys.stderr)
    sys.stderr.flush()

    if fatal:
        sys.exit(1)


def sleepMillis(millis):
    debug("Sleeping for %d milliseconds" % millis)
    time.sleep(millis/1000)


def setGlobalVerbose(v):
    global verbose
    verbose = v