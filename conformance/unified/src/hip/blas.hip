#if defined(CONFORMANCE_HIP_HAS_ROCBLAS)
  #include <catch2/catch_test_macros.hpp>
  #include <catch2/generators/catch_generators_adapters.hpp>
  #include <catch2/generators/catch_generators_range.hpp>

  #include "backend_blas.hpp"
  #include "cases/blas.inl"
  #include <magic_enum/magic_enum.hpp>
#endif
