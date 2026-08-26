#pragma once

// GCC 14 and newer expose language features to their own libstdc++ that the
// CUDA device front end may not parse yet. Include the configuration once,
// then select libstdc++'s portable implementations for device compilation.
#if defined(__CUDACC__)
#include <bits/c++config.h>

#undef _GLIBCXX_EXPLICIT_THIS_PARAMETER
#define _GLIBCXX_EXPLICIT_THIS_PARAMETER 0

#undef _GLIBCXX_USE_BUILTIN_TRAIT
#define _GLIBCXX_USE_BUILTIN_TRAIT(trait) 0
#endif
