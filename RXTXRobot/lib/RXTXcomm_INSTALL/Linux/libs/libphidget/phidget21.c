// phidget21.c : Defines the entry point for the DLL application.
//

#include "stdafx.h"
#include "phidget21int.h"
#include "cthread.h"
#include "cphidget.h"
#include "cusb.h"

#ifdef _WINDOWS
#ifdef _MANAGED
#pragma managed(push, off)
#endif

BOOL APIENTRY DllMain( HMODULE hModule,
                       DWORD  ul_reason_for_call,
                       LPVOID lpReserved
					 )
{
	switch (ul_reason_for_call)
	{
	case DLL_PROCESS_ATTACH:
		ActiveDevices = 0;
		//inst = (HINSTANCE)hModule;
	case DLL_THREAD_ATTACH:
	case DLL_THREAD_DETACH:
	case DLL_PROCESS_DETACH:
		break;
	}
    return TRUE;
}

#ifdef _MANAGED
#pragma managed(pop)
#endif
#endif


