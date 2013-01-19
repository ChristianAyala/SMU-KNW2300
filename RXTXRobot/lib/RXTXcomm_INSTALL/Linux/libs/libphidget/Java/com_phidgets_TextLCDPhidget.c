#include "../stdafx.h"
#include "phidget_jni.h"
#include "com_phidgets_TextLCDPhidget.h"
#include "../cphidgettextlcd.h"

JNI_LOAD(lcd, TextLCD)
}

JNI_CREATE(TextLCD)
JNI_GETFUNCBOOL(TextLCD, Backlight, Backlight)
JNI_SETFUNC(TextLCD, Backlight, Backlight, jboolean)
JNI_GETFUNC(TextLCD, Contrast, Contrast, jint)
JNI_SETFUNC(TextLCD, Contrast, Contrast, jint)
JNI_GETFUNC(TextLCD, Brightness, Brightness, jint)
JNI_SETFUNC(TextLCD, Brightness, Brightness, jint)
JNI_GETFUNCBOOL(TextLCD, Cursor, CursorOn)
JNI_SETFUNC(TextLCD, Cursor, CursorOn, jboolean)
JNI_GETFUNCBOOL(TextLCD, CursorBlink, CursorBlink)
JNI_SETFUNC(TextLCD, CursorBlink, CursorBlink, jboolean)
JNI_GETFUNC(TextLCD, RowCount, RowCount, jint)
JNI_GETFUNC(TextLCD, ColumnCount, ColumnCount, jint)
JNI_GETFUNC(TextLCD, ScreenCount, ScreenCount, jint)

JNIEXPORT void JNICALL
Java_com_phidgets_TextLCDPhidget_setDisplayString(JNIEnv *env, jobject obj, jint index, jstring v)
{
	CPhidgetTextLCDHandle h = (CPhidgetTextLCDHandle)(uintptr_t)
	    (*env)->GetLongField(env, obj, handle_fid);
	int error, i;
    jboolean iscopy;
	char string[TEXTLCD_MAXCOLS+2];

	//we can't use GetStringUTFChars here because it converts the UTF-16 to UTF-8 and this screws up the 0x80-0xFF characters
	const jchar *textString = (*env)->GetStringChars(env, v, &iscopy);

	for(i=0;i<TEXTLCD_MAXCOLS+2;i++)
	{
		string[i] = (char)textString[i];
		if(!textString[i])
			break;
	}
	string[TEXTLCD_MAXCOLS+1] = '\0';

	if ((error = CPhidgetTextLCD_setDisplayString(h, index, (char *)string)))
		PH_THROW(error);

	(*env)->ReleaseStringChars(env, v, textString);
}

JNIEXPORT void JNICALL
Java_com_phidgets_TextLCDPhidget_setDisplayCharacter(JNIEnv *env, jobject obj, jint row, jint column, jchar v)
{
	CPhidgetTextLCDHandle h = (CPhidgetTextLCDHandle)(uintptr_t)
	    (*env)->GetLongField(env, obj, handle_fid);
	int error;

	if ((error = CPhidgetTextLCD_setDisplayCharacter(h, row, column, (char)v)))
		PH_THROW(error);
}

JNIEXPORT void JNICALL
Java_com_phidgets_TextLCDPhidget_setCustomCharacter(JNIEnv *env, jobject obj, jint index, jint v, jint v2)
{
	CPhidgetTextLCDHandle h = (CPhidgetTextLCDHandle)(uintptr_t)
	    (*env)->GetLongField(env, obj, handle_fid);
	int error;
	if ((error = CPhidgetTextLCD_setCustomCharacter(h, index, v, v2)))
		PH_THROW(error);
}

JNI_GETFUNC(TextLCD, Screen, Screen, jint)
JNI_SETFUNC(TextLCD, Screen, Screen, jint)

JNIEXPORT jint JNICALL
Java_com_phidgets_TextLCDPhidget_getScreenSize(JNIEnv *env, jobject obj)
{
	CPhidgetTextLCDHandle h = (CPhidgetTextLCDHandle)(uintptr_t)
	    (*env)->GetLongField(env, obj, handle_fid);
	int error;
	CPhidgetTextLCD_ScreenSize v;
	if ((error = CPhidgetTextLCD_getScreenSize(h, &v)))
		PH_THROW(error);
	return (jint)v;
}

JNI_SETFUNC(TextLCD, ScreenSize, ScreenSize, jint)

JNIEXPORT void JNICALL
Java_com_phidgets_TextLCDPhidget_initialize(JNIEnv *env, jobject obj)
{
	CPhidgetTextLCDHandle h = (CPhidgetTextLCDHandle)(uintptr_t)
	    (*env)->GetLongField(env, obj, handle_fid);
	int error;

	if ((error = CPhidgetTextLCD_initialize(h)))
		PH_THROW(error);
}
