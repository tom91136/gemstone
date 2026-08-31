# cuBLAS conformance

cuBLAS is not vendored here. NVIDIA's cuBLAS headers and libraries are proprietary
licensed deliverables, so this directory contains only small, self-authored API
clients. The clients are compiled as host-side C++ programs against a cuBLAS
installation supplied by the machine running the conformance suite.

The same executable, compiled and linked against the installed cuBLAS headers
and library, is used for both lanes:

1. the oracle runs with the installed CUDA driver and cuBLAS;
2. the Gemstone run preloads Gemstone's `libcuda.so.1`, leaving cuBLAS itself
   unchanged while routing its CUDA Driver calls through the existing RPC
   boundary.

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

The Gemstone lane does not export or implement cuBLAS symbols. If the installed
cuBLAS library cannot initialize against the emulator, that is a Driver API or
private-interface compatibility failure to fix at the RPC boundary. The
installed cuBLAS library remains the implementation under test, and
`GEMSTONE_CUBLAS_SKIP_ORACLE=1` runs it against the emulator without oracle
execution.

The CUDA 11.6 smoke case runs through the emulator as well as the device oracle.
The compatibility layer accepts the CUDA runtime's header-only bootstrap
modules and null device frees, while all public BLAS work remains in the
installed cuBLAS library. No private cuBLAS implementation is reproduced in
Gemstone.

No NVIDIA header, shared object, static library, or generated cuBLAS wrapper is
checked into this repository.
