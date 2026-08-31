# rocBLAS conformance

This lane uses the vendor-neutral header-only BLAS smoke harness in `conformance/blas/include`. The rocBLAS adapter is
compiled with HIP and the resulting client is paired through the public HIP boundary for every selected GFX target.
The identical harness and reference calculations are also provided for cuBLAS and oneMKL adapters.

The bounded suite covers strided `axpy`, `scal`, `copy`, `dot`, `nrm2`, and `asum` at n=64 (Level 1), plus column-major
3x2 GEMV (Level 2) and 2x2x3 GEMM (Level 3), parameterized over the numeric types each vendor API supports. This
exercises address arithmetic and reductions without importing the long-running upstream rocBLAS client matrix.

```console
sbt testROCmConformance
GEMSTONE_ROCBLAS_FILTER='^gfx900/f32/all$' sbt testROCmConformance
GEMSTONE_ROCBLAS_JOBS=1 sbt testROCmConformance
GEMSTONE_ROCBLAS_LEVELS=all sbt testROCmConformance
GEMSTONE_ROCBLAS_MATRIX_SIZE=64 GEMSTONE_ROCBLAS_LEVELS=level2,level3 sbt testROCmConformance
```

The suite defaults to four workers and `GEMSTONE_ROCBLAS_JOBS` may be set from 1 through 32. Use
`GEMSTONE_ROCBLAS_LIBRARY` when the installed shared library is not in a standard ROCm location and
`GEMSTONE_ROCBLAS_TENSILE_LIBPATH` when its code-object directory is not discoverable.

The default is `all` for both oracle and emulator execution. Set `GEMSTONE_ROCBLAS_LEVELS=level1`, `level2`, or
`level3` to isolate a BLAS level, and `GEMSTONE_ROCBLAS_TYPES=f16,f32,f64,i8` to override the default type matrix.
Operations absent from a vendor's public API are reported as explicit skips.
`GEMSTONE_ROCBLAS_MATRIX_SIZE` selects square GEMV and GEMM dimensions from 2 through 128 while leaving the default
tiny 3x2 GEMV and 2x2x3 GEMM smoke cases unchanged.

rocBLAS itself is HIP-facing; this lane therefore validates the HIP path. Direct-HSA coverage remains provided by the
HSA sample lane.
