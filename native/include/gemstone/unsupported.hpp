#pragma once

#include <type_traits>

#include <gemstone/export.h>

// Unimplemented entry points are strong aliases of one stub returning the API's not-supported status. The alias keeps
// the vendor declaration's type, so a name absent from the pinned headers fails to compile. GenerateNativeBindings emits
// the inventories from the functions a facade does not list as implemented.
#pragma GCC diagnostic ignored "-Wdeprecated-declarations"
#if defined(__GNUC__) && !defined(__clang__)
  #pragma GCC diagnostic ignored "-Wattribute-alias"
  #pragma GCC diagnostic ignored "-Wmissing-attributes"
#endif

#define GEMSTONE_UNSUPPORTED_AS(type, symbol, target)                                                                                      \
  extern "C" GEMSTONE_PUBLIC_API std::type_identity_t<type> gemstone_unsupported_##symbol __asm__(#symbol) __attribute__((alias(#target)));
#define GEMSTONE_UNSUPPORTED(symbol, target)                                                                                               \
  extern "C" GEMSTONE_PUBLIC_API decltype(symbol) gemstone_unsupported_##symbol __asm__(#symbol) __attribute__((alias(#target)));
