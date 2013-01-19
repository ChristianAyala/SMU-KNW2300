#include "../stdafx.h"
#include "phidget_jni.h"
#include "com_phidgets_EncoderPhidget.h"
#include "../cphidgetencoder.h"

EVENT_VARS(inputChange, InputChange)
EVENT_VARS(encoderPositionChange, EncoderPositionChange)

JNI_LOAD(enc, Encoder)
	EVENT_VAR_SETUP(enc, inputChange, InputChange, IZ, V)
	EVENT_VAR_SETUP(enc, encoderPositionChange, EncoderPositionChange, III, V)
}

EVENT_HANDLER_INDEXED(Encoder, inputChange, InputChange, 
					  CPhidgetEncoder_set_OnInputChange_Handler, int)
EVENT_HANDLER_INDEXED2(Encoder, encoderPositionChange, EncoderPositionChange, 
					  CPhidgetEncoder_set_OnPositionChange_Handler, int, int)

JNI_CREATE(Encoder)
JNI_INDEXED_GETFUNC(Encoder, Position, Position, jint)
JNI_INDEXED_SETFUNC(Encoder, Position, Position, jint)
JNI_INDEXED_GETFUNCBOOL(Encoder, InputState, InputState)
JNI_GETFUNC(Encoder, EncoderCount, EncoderCount, jint)
JNI_GETFUNC(Encoder, InputCount, InputCount, jint)
JNI_INDEXED_GETFUNC(Encoder, IndexPosition, IndexPosition, jint)
JNI_INDEXED_GETFUNCBOOL(Encoder, Enabled, Enabled)
JNI_INDEXED_SETFUNC(Encoder, Enabled, Enabled, jboolean)

//Deprecated
JNI_INDEXED_GETFUNC(Encoder, EncoderPosition, Position, jint)
JNI_INDEXED_SETFUNC(Encoder, EncoderPosition, Position, jint)
