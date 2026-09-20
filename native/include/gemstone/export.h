#ifndef GEMSTONE_EXPORT_H
#define GEMSTONE_EXPORT_H

#if defined(__GNUC__) || defined(__clang__)
  #define GEMSTONE_PUBLIC_API __attribute__((visibility("default")))
  #define GEMSTONE_INTERNAL_API __attribute__((visibility("hidden")))
#else
  #define GEMSTONE_PUBLIC_API
  #define GEMSTONE_INTERNAL_API
#endif

#endif
