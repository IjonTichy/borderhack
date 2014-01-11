#include <X11/Xlib.h>

#include "linuxhacks.h"

JNIEXPORT void JNICALL Java_core_GameWindow_XInitThreads(JNIEnv * env, jclass cls)
{
    XInitThreads();
    return;
}
