#include "cases/hip_struct_alignment.inl"

TEST_CASE("memory/hip/struct-alignment/packed", "[unified][memory]") { run<Packed>(); }

TEST_CASE("memory/hip/struct-alignment/packed-aligned-2", "[unified][memory]") { run<Packed2>(); }

TEST_CASE("memory/hip/struct-alignment/packed-aligned-4", "[unified][memory]") { run<Packed4>(); }

TEST_CASE("memory/hip/struct-alignment/packed-aligned-8", "[unified][memory]") { run<Packed8>(); }

TEST_CASE("memory/hip/struct-alignment/natural", "[unified][memory]") { run<Natural>(); }

TEST_CASE("memory/hip/struct-alignment/aligned-16", "[unified][memory]") { run<Aligned16>(); }

TEST_CASE("memory/hip/struct-alignment/mixed-packed", "[unified][memory]") { run<MixedPacked>(); }

TEST_CASE("memory/hip/struct-alignment/mixed-natural", "[unified][memory]") { run<MixedNatural>(); }
