#if defined(CONFORMANCE_HIP_HAS_ROCBLAS)
  #include "backend_blas.hpp"
  #include "cases/blas.inl"

UNIFIED_BLAS_GRID(UNIFIED_BLAS)
#endif
