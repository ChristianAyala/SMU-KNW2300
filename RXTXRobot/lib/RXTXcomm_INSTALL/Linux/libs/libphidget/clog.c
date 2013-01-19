#include "stdafx.h"
#include "clog.h"

static FILE *logFile = NULL;
static CPhidgetLog_level logging_level = 0;

/* Protects logFile, logging_level - thread safe */
int logLockInitialized = PFALSE;
CThread_mutex_t logLock;

static const char *
logLevelToStr(CPhidgetLog_level l)
{
	switch (l) {
	case PHIDGET_LOG_CRITICAL:
		return "CRIT";
	case PHIDGET_LOG_ERROR:
		return "ERR";
	case PHIDGET_LOG_WARNING:
		return "WARN";
	case PHIDGET_LOG_DEBUG:
		return "DEBUG";
	case PHIDGET_LOG_INFO:
		return "INFO";
	case PHIDGET_LOG_VERBOSE:
		return "VERBOSE";
	}
	return "???";
}

PHIDGET21_API int CCONV CPhidget_enableLogging(CPhidgetLog_level level, const char *outputFile)
{	
	if(!logLockInitialized)
	{
		CThread_mutex_init(&logLock);
		logLockInitialized = PTRUE;
	}
	CThread_mutex_lock(&logLock);
	logging_level = level;
	if(outputFile == NULL)
		logFile = NULL;
	else
	{
		logFile = fopen(outputFile,"a");
#ifndef _WINDOWS
		if(!logFile)
		{
			CThread_mutex_unlock(&logLock);
			switch(errno)
			{
				case EACCES:
					LOG(PHIDGET_LOG_WARNING, "Permission denied for specified logfile. Logging to stdout.");
					break;
				defualt:
					LOG(PHIDGET_LOG_WARNING, "Logfile fopen failed with code: %d",errno);
			}
			CThread_mutex_lock(&logLock);
		}
#endif
	}
	CThread_mutex_unlock(&logLock);
	LOG(PHIDGET_LOG_INFO, "Enabling logging");
	return EPHIDGET_OK;
}

PHIDGET21_API int CCONV CPhidget_disableLogging()
{
	if(!logLockInitialized)
	{
		CThread_mutex_init(&logLock);
		logLockInitialized = PTRUE;
	}
	LOG(PHIDGET_LOG_INFO, "Disabling logging");
	CThread_mutex_lock(&logLock);
	if(logFile!=NULL && logFile!=stdout && logFile!=stderr)
		fclose(logFile);
	logFile = NULL;
	logging_level = 0;
	CThread_mutex_unlock(&logLock);
	return EPHIDGET_OK;
}

//if outputting to a file, it is designed to be CSV compliant
//only print out debug logs in debug library
PHIDGET21_API int CCONV CPhidget_log(CPhidgetLog_level level, const char *msg, const char *fmt, ...)
{
	size_t threadID = 0;
	int logToStderr = (int)level & LOG_TO_STDERR;
	va_list va;
	level = level & 0xFF;
#ifdef DEBUG
	if(level <= logging_level || level == PHIDGET_LOG_DEBUG || logToStderr)
#else
	if((level <= logging_level && level != PHIDGET_LOG_DEBUG) || logToStderr)
#endif
	{
		if(!logLockInitialized)
		{
			CThread_mutex_init(&logLock);
			logLockInitialized = PTRUE;
		}
		CThread_mutex_lock(&logLock);
		if (!logFile)
			logFile = stdout;
#ifdef WINCE
		if(logToStderr)
			fprintf(stderr, "%s: ",logLevelToStr(level));
		else if(logFile==stdout)
			fprintf(logFile, "%s: ",logLevelToStr(level));
		else
			fprintf(logFile, "%d,\"%s\",%s,\"", threadID, msg, logLevelToStr(level));
		va_start(va, fmt);
		if(logToStderr)
		{
			vfprintf(stderr, fmt, va);
			va_end(va);
			fprintf(stderr, "\n");
			fflush(stderr);
		}
		else
		{
			vfprintf(logFile, fmt, va);
			va_end(va);
			if(logFile==stdout)
				fprintf(logFile, "\n");
			else
				fprintf(logFile, "\"\n");
			fflush(logFile);
		}
#else //!WINCE
		{
			char date[50];
			struct tm *tmp;
			time_t t;
			time(&t);
 #ifndef _WINDOWS
			struct tm tm;
			localtime_r(&t, &tm);
			tmp = &tm;
			threadID = (size_t)pthread_self();
 #else
			tmp = localtime(&t);
			threadID = (size_t)GetCurrentThreadId();
 #endif
			if (!strftime(date, sizeof (date), "%c", tmp))
				strncpy(date, "?", sizeof (date));
			if(logToStderr)
				fprintf(stderr, "%s: ",logLevelToStr(level));
			else if(logFile==stdout)
				fprintf(logFile, "%s: ",logLevelToStr(level));
			else
				fprintf(logFile, "%s,%d,\"%s\",%s,\"", date, (int)threadID, msg, logLevelToStr(level));
			va_start(va, fmt);
			if(logToStderr)
			{
				vfprintf(stderr, fmt, va);
				va_end(va);
				fprintf(stderr, "\n");
				fflush(stderr);
			}
			else
			{
				vfprintf(logFile, fmt, va);
				va_end(va);
				if(logFile==stdout)
					fprintf(logFile, "\n");
				else
					fprintf(logFile, "\"\n");
				fflush(logFile);
			}
		}
#endif
		CThread_mutex_unlock(&logLock);
	}
	return EPHIDGET_OK;
}
