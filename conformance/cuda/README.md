# CUDA conformance tests

This is the active CUDA compatibility corpus. Every test is one self-contained `.cu` translation unit with a
`main`, performs its own assertions, prints exactly `pass` on success, and returns zero only when all checks pass.
The commands and expected output are source metadata using the same durable form as Polyregion's native tests:

```cpp
#pragma region case: runtime-malloc-copy
#pragma region do: {nvcc} --allow-unsupported-compiler -std=c++17 -O2 -o {output} {input}
#pragma region do: {output}
#pragma region requires: pass
```

`CUDAConformanceSuite` discovers every `check_*.cu` recursively and registers one MUnit test per translation unit.
Run the complete CUDA lane with:

```console
sbt testCUDAConformance
```

No Scala registry needs updating when a translation unit is added. Every case is compiled once through the normal
vendor command, then that exact executable runs once as the NVIDIA oracle and once through Gemstone. Unsupported
Gemstone behavior is a named red result; there is no local backend allowlist.

The Driver callback case mixes `cuLaunchHostFunc` and `cuStreamAddCallback` on one stream, checks ordered shared-state
transitions and callback-local TLS, and detects overlapping or misrouted callback execution without assuming a
vendor-specific worker-thread identity.

The suite builds the Driver facade, executes each case in an isolated directory, and compares stdout exactly with
`requires:`. Each Gemstone child has a fresh random authentication token and must make an authenticated Driver RPC;
otherwise it fails even if the process returned zero. `NVCC` or `CUDA_HOME` selects a compiler explicitly; otherwise
the newest detected CUDA release is used. There are no Gemstone compile defines, alternate link lines, or private
CUDA Runtime replacement: Runtime cases use stock `libcudart` and are intercepted at its Driver calls.

Semantic cases additionally declare `ptx-kernel`, `ptx-shape`, `ptx-input`, and `ptx-output` pragmas and embed their
module in an `R"ptx(...)ptx"` literal. The CUDA test loads that literal through NVIDIA's Driver API. The same Scala
suite discovers and extracts the identical PTX, runs it through Gemstone, and compares the declared output, so adding
a semantic case does not require a Scala registry edit. Enabled semantic cases also run end to end through Gemstone's
generated `libcuda.so.1`, RPC server, module registry, and PTX executor.

Independent cases execute concurrently in isolated directories. `GEMSTONE_TEST_JOBS` sets the bounded worker count
(1-32); it defaults to the smaller of eight and the available processor count.

More substantial reference cases live under `samples/` and `thrust/`. Their implementation sources and licences are
vendored under `vendor/`; the system toolkit supplies only the CUDA compiler, ABI/runtime headers, and reference
runtime/driver libraries. Thrust cases use the additional metadata and command variables:

```cpp
#pragma region vendored: thrust,cub
#pragma region do: {nvcc} {cuda_vendor_includes} -MD -MF {dependencies} -o {output} {input}
```

The suite audits the dependency file and fails if compilation falls back to toolkit-provided Thrust or CUB headers.
See `vendor/README.md` for exact upstream revisions and licence details.

## Official Thrust suite

The full, unmodified Thrust 1.15.0 test tree and unittest harness are vendored under `vendor/thrust/testing`. They run
in a dedicated SBT configuration because compiling 237 CUDA executables is intentionally much more expensive than
the compact conformance corpus:

```console
sbt testCUDAConformance
```

The Scala suite discovers the same 160 common, 16 async, and 61 CUDA-specific translation units selected by Thrust's
own CPP/CUDA CMake configuration. Each executable links the official two-file unittest framework and runs at the
`tiny` size tier by default. Successful compilation is cached under `target/conformance/thrust-1.15.0`; source,
compiler, architecture, and compile-command changes invalidate it.

Useful controls are:

```console
GEMSTONE_THRUST_FILTER='^(transform|cuda\\.transform)$' sbt testCUDAConformance
GEMSTONE_THRUST_SIZES=default sbt testCUDAConformance
GEMSTONE_THRUST_ARCH=sm_89 sbt testCUDAConformance
GEMSTONE_THRUST_JOBS=8 sbt testCUDAConformance
GEMSTONE_THRUST_INCLUDE_DISABLED=1 sbt testCUDAConformance
```

`GEMSTONE_THRUST_FILTER` is a Scala regular expression over upstream-relative names. `GEMSTONE_THRUST_JOBS` bounds
compilation and execution concurrency from 1 to 32 and defaults to the smaller of four and the available processor
count. Each vendor-standard executable runs first against NVIDIA and then unchanged through Gemstone. Dependency
files are audited to ensure Thrust and CUB never fall back to toolkit copies.

## Provenance and licence boundary

These are compact adaptations of tests in GPUOcelot revision `b16039dc940dc6bc4ea0a98380495769ff35ed99` under
`ocelot/src/cuda/test`. They retain GPUOcelot's BSD-3-Clause licence in this directory. The mapping is:

| Gemstone test | GPUOcelot source |
| --- | --- |
| `driver/check_memory.cu` | `driver/sequence.cpp` (Driver API setup and memory flow) |
| `driver/check_module_launch.cu` | `driver/sequence.cpp` and `driver/sequence.ptx` |
| `runtime/check_malloc_copy.cu` | `TestCUDAMalloc.cu` |
| `runtime/check_pointer_offsets.cu` | `TestCUDAMalloc.cu` |
| `runtime/check_pitched_copy.cu` | `TestCUDAMalloc.cu` |
| `runtime/check_sequence.cu` | `TestCUDASequence.cu` |
| `runtime/check_shared_barrier.cu` | `TestCUDASequence.cu` |
| `runtime/check_globals.cu` | `TestCUDAGlobals.cu` |
| `runtime/check_device_function.cu` | `TestFunctionCall.cu` |
| `runtime/check_indirect_function.cu` | `TestIndirectFunctionCall.cu` |
| `runtime/check_array_copy.cu` | `memory/arrays.cu` |

GPUOcelot's `tests/cuda2.2`, `tests/cuda2.3`, and `tests/cuda3.2` trees are intentionally excluded: they largely
contain NVIDIA SDK samples with separate or unclear redistribution terms. Ocelot-internal parser/executive unit
tests are also not copied here because they test GPUOcelot's C++ internals rather than an observable CUDA ABI.

The donor tree itself is not retained. Adapted tests carry their provenance, copyright, and BSD-3-Clause notice here.
