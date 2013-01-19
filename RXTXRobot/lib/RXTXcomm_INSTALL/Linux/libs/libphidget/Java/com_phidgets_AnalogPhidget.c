#include "../stdafx.h"
#include "phidget_jni.h"
#include "com_phidgets_AnalogPhidget.h"
#include "../cphidgetanalog.h"

JNI_LOAD(analog, Analog)
}

JNI_CREATE(Analog)

JNI_GETFUNC(Analog, OutputCount, OutputCount, jint)
JNI_INDEXED_GETFUNC(Analog, Voltage, Voltage, jdouble)
JNI_INDEXED_SETFUNC(Analog, Voltage, Voltage, jdouble)
JNI_INDEXED_GETFUNC(Analog, VoltageMax, VoltageMax, jdouble)
JNI_INDEXED_GETFUNC(Analog, VoltageMin, VoltageMin, jdouble)
JNI_INDEXED_GETFUNCBOOL(Analog, Enabled, Enabled)
JNI_INDEXED_SETFUNC(Analog, Enabled, Enabled, jboolean)