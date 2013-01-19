#include "../stdafx.h"
#include "phidget_jni.h"
#include "com_phidgets_TextLEDPhidget.h"
#include "../cphidgettextled.h"

JNI_LOAD(led, TextLED)
}

JNI_CREATE(TextLED)
JNI_GETFUNC(TextLED, Brightness, Brightness, jint)
JNI_SETFUNC(TextLED, Brightness, Brightness, jint)
JNI_GETFUNC(TextLED, RowCount, RowCount, jint)
JNI_GETFUNC(TextLED, ColumnCount, ColumnCount, jint)

JNIEXPORT void JNICALL
Java_com_phidgets_TextLEDPhidget_setDisplayString(JNIEnv *env, jobject obj, jint index, jstring v)
{
	CPhidgetTextLEDHandle h = (CPhidgetTextLEDHandle)(uintptr_t)
	    (*env)->GetLongField(env, obj, handle_fid);
	int error;
    jboolean iscopy;
    const char *textString = (*env)->GetStringUTFChars(
                env, v, &iscopy);

	if ((error = CPhidgetTextLED_setDisplayString(h, index, (char *)textString)))
		PH_THROW(error);

	(*env)->ReleaseStringUTFChars(env, v, textString);
}
