#include "abi.h"

const char *getAbi() {
#ifdef __arm__
    return "arm32";
#elif __aarch64__
    return "arm64";
#elif __i386__
    return "x86";
#elif __x86_64__
    return "x86_64";
#else
    return "unknown";
#endif
}