// Without unaligned global access amdclang reassembles packed and halfword-aligned fields from byte and short accesses
// instead of one wide load. The host compile does not know the feature.
#ifdef __HIP_DEVICE_COMPILE__
  #define UNIFIED_STRUCT_ALIGNMENT_KERNEL_ATTRIBUTE __attribute__((target("no-unaligned-access-mode")))
#endif

#include "cases/hip_struct_alignment.inl"

TEST_CASE("memory/hip/struct-alignment/packed-bytewise", "[unified][memory]") { run<Packed>(); }

TEST_CASE("memory/hip/struct-alignment/packed-aligned-2-bytewise", "[unified][memory]") { run<Packed2>(); }

TEST_CASE("memory/hip/struct-alignment/mixed-packed-bytewise", "[unified][memory]") { run<MixedPacked>(); }
