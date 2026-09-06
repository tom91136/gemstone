# cuBLAS conformance

cuBLAS is not vendored here. NVIDIA's cuBLAS headers and libraries are proprietary
licensed deliverables, so this directory contains only small, self-authored API
clients. The clients are compiled as host-side C++ programs against a cuBLAS
installation supplied by the machine running the conformance suite.

The same executable, compiled and linked against the installed cuBLAS headers
and library, is used for both lanes:

1. the oracle runs with the installed CUDA driver and cuBLAS;
2. the Gemstone run preloads Gemstone's `libcuda.so.1`. The provider exports
   the narrow cuBLAS ABI needed by this suite, so the official binary's
   hardware-specific bootstrap is bypassed while allocations and copies still
   use the ordinary CUDA Driver RPC boundary.

This is intentional. cuBLAS is a host-side library that dispatches device work;
duplicating its entire ABI and implementation in generated JVM logic would not
test the boundary applications actually use. CUTLASS is a possible source-safe
implementation substrate for a future open Gemstone BLAS provider, but it is not
a drop-in implementation of the cuBLAS ABI and is not needed for this oracle
lane. The upstream project documents CUDA 11.4 as its minimum toolkit and CUDA
11.8 as the minimum for RTX 40/SM89 targets, so the installed CUDA 11.6 toolkit
is suitable for source inspection and older-target experiments but not a real
SM89 CUTLASS build. See the [upstream CUTLASS README](https://github.com/NVIDIA/cutlass/blob/main/README.md).

The current smoke client covers handle/version state and representative single-
precision Level 1, Level 2, and Level 3 operations (`Saxpy`, `Sgemv`, and
`Sgemm`) with deterministic host-side checks. Add a new `check_*.cu` client when
expanding coverage; `CUDAConformanceSuite` discovers it automatically.

Run it with the CUDA conformance command:

```console
GEMSTONE_CUDA_FILTER='^cublas-smoke$' sbt testCUDAConformance
```

The suite discovers cuBLAS below `CUDA_HOME` or `/usr/local/cuda*`. Set
`GEMSTONE_CUBLAS_INCLUDE` and `GEMSTONE_CUBLAS_LIBRARY` when the installation is
elsewhere. `GEMSTONE_CUBLAS_SKIP_ORACLE=1` is available for emulator-only
development and does not provide oracle evidence. `GEMSTONE_CUBLAS_CXX` can
select the host C++ compiler for the client.

The provider implementation is intentionally limited to the public BLAS calls
used by the conformance workload (handle/version state, Level 1, GEMV, GEMM,
and strided-batched GEMM for f32/f64). Unsupported cuBLAS entry points retain
the normal failure behavior. `GEMSTONE_CUBLAS_SKIP_ORACLE=1` runs this
provider-side implementation without oracle execution.

No private cuBLAS implementation or vendor source is reproduced in Gemstone;
the compatibility layer is a small host-side adapter over the public Driver
copy API.

No NVIDIA header, shared object, static library, or generated cuBLAS wrapper is
checked into this repository.
