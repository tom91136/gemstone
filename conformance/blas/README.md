# Vendor-neutral BLAS smoke harness

`include/gemstone/conformance/blas_smoke.hpp` owns the conformance logic: deterministic inputs, host reference
calculations, type-specific tolerances, and the bounded coverage modes. A backend only supplies native device-buffer
lifetime, transfers, and matching BLAS calls. The checked-in adapters demonstrate the same contract for rocBLAS, cuBLAS,
and oneMKL.

The default `all` mode covers strided Level-1 `axpy`, `scal`, `copy`, `dot`, `nrm2`, and `asum` at n=64, followed by
column-major 3x2 GEMV and 2x2x3 GEMM. `level1`, `level2`, and `level3` can be passed to a program independently, and
the optional second argument selects `f16`, `f32`, `f64`, or `i8` (default `f32`). Unsupported operations are reported
explicitly and skipped; an unsupported type never falls through to an incorrectly typed vendor call.
Set `GEMSTONE_BLAS_MATRIX_SIZE` from 2 through 128 to replace the tiny Level-2 and Level-3 cases with square GEMV and
GEMM dimensions; for example, `GEMSTONE_BLAS_MATRIX_SIZE=64 ./rocblas_smoke level3 f32` runs a 64x64x64 GEMM.

The standard smoke matrix is intentionally limited to the common real BLAS contract. f32 and f64 cover all three
levels on all adapters. Half coverage follows each vendor's public API: rocBLAS exposes AXPY/DOT/GEMM, cuBLAS exposes
GEMM, and oneMKL exposes AXPY/SCAL/DOT/NRM2/GEMM. i8 is recognized for future mixed-precision GEMM coverage but is not
enabled by default because its A/B/C operand types differ between vendor extensions.

Example vendor builds, with paths adjusted for the local installation:

```console
hipcc -std=c++17 -O2 -Iconformance/blas/include -Iconformance/blas/adapters \
  -I<rocblas-source>/library/include -I<generated-rocblas-include> \
  conformance/blas/programs/rocblas_smoke.cpp -lrocblas -o rocblas_smoke

./rocblas_smoke all f64

nvcc --allow-unsupported-compiler -std=c++17 -O2 -arch=sm_89 \
  -Iconformance/blas/include -Iconformance/blas/adapters \
  -I<cublas-include> conformance/blas/programs/cublas_smoke.cu \
  -lcublas -o cublas_smoke

icpx -fsycl -qmkl=parallel -std=c++17 -O2 \
  -Iconformance/blas/include -Iconformance/blas/adapters \
  -I<oneapi-mkl-include> conformance/blas/programs/oneapi_mkl_smoke.cpp \
  -o oneapi_mkl_smoke

./oneapi_mkl_smoke all f16
```
