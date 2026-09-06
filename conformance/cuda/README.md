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

To establish only the physical NVIDIA baseline before exercising the emulator,
set `GEMSTONE_ORACLE_ONLY=1`. This compiles and runs each vendor artifact but
does not build or invoke Gemstone's provider:

```console
GEMSTONE_ORACLE_ONLY=1 sbt testCUDAConformance
```

The mode requires oracle execution; do not combine it with
`GEMSTONE_NO_ORACLE=1`, `GEMSTONE_THRUST_SKIP_ORACLE=1`, or
`GEMSTONE_CUBLAS_SKIP_ORACLE=1`.

No Scala registry needs updating when a translation unit is added. Every case is compiled once through the normal
vendor command, then that exact executable runs once as the NVIDIA oracle and once through Gemstone. Unsupported
Gemstone behavior is a named red result; the only local skips are individually documented units proven impractical
for the emulator.

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
`GEMSTONE_CONFORMANCE_TIMEOUT_SECONDS` bounds each compiler/test process to
120 seconds by default; increase it only for a specifically justified case.

More substantial reference cases live under `samples/` and `thrust/`. Their implementation sources and licences are
vendored under `vendor/`; the system toolkit supplies only the CUDA compiler, ABI/runtime headers, and reference
runtime/driver libraries. Thrust cases use the additional metadata and command variables:

```cpp
#pragma region vendored: thrust,cub
#pragma region do: {nvcc} {cuda_vendor_includes} -MD -MF {dependencies} -o {output} {input}
```

The suite audits the dependency file and fails if compilation falls back to toolkit-provided Thrust or CUB headers.
See `vendor/README.md` for exact upstream revisions and licence details.

The `cublas/` lane follows a different boundary: cuBLAS is proprietary and is therefore neither copied nor generated
into the repository. Its small, self-authored clients link against the installed cuBLAS library for the oracle. In the
Gemstone lane, the preloaded provider supplies a narrow, versioned host-side cuBLAS compatibility surface for the
covered BLAS calls and executes their data movement through the CUDA Driver RPC. See `cublas/README.md` for the
toolkit path controls and licensing boundary.

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
The Thrust MUnit suite runs a matrix of `(translation unit, compute capability)` cases. By default it selects the
representative targets `compute_75`, `compute_80`, `compute_86`, `compute_89`, and `compute_90`, filtering out targets
unsupported by the detected NVCC. Each target has an independent framework/object cache and appears in MUnit as
`thrust/<case>[compute_N]`.

Useful controls are:

```console
GEMSTONE_THRUST_FILTER='^(transform|cuda\\.transform)$' sbt testCUDAConformance
GEMSTONE_THRUST_SIZES=default sbt testCUDAConformance
GEMSTONE_THRUST_ARCH=compute_75 sbt testCUDAConformance
GEMSTONE_THRUST_ARCHES='compute_75,sm_80,cc86' sbt testCUDAConformance
GEMSTONE_THRUST_ORACLE_ARCHES='compute_75,compute_89' sbt testCUDAConformance
GEMSTONE_THRUST_JOBS=8 sbt testCUDAConformance
GEMSTONE_THRUST_INCLUDE_DISABLED=1 sbt testCUDAConformance
GEMSTONE_THRUST_SKIP_ORACLE=1 sbt testCUDAConformance
```

`GEMSTONE_CUDA_FILTER` applies a regular expression to the compact CUDA case names, which is useful for running one
small lane without scheduling the rest of the corpus.

`GEMSTONE_THRUST_FILTER` is a Scala regular expression over upstream-relative names. `GEMSTONE_THRUST_JOBS` bounds
compilation and execution concurrency from 1 to 32 and defaults to the smaller of four and the available processor
count. `GEMSTONE_CONFORMANCE_TIMEOUT_SECONDS` bounds each Thrust compiler/test process to 120 seconds by default.
Each vendor-standard executable runs first against NVIDIA and then unchanged through Gemstone. Dependency
files are audited to ensure Thrust and CUB never fall back to toolkit copies. The small set of upstream units named
`*BigIndexes` (and the async `large_indices` units) intentionally launch virtual ranges of 2^30--2^33 elements.
Those units are explicitly blacklisted for the emulator because a JVM interpreter would visit every element; the
remaining units in their translation units still run through both lanes. Extend the named list for a newly proven
emulator-impossible unit with `GEMSTONE_THRUST_EMULATION_BLACKLIST=case:unit,case:unit`.
`GEMSTONE_THRUST_ARCH` remains a single-target compatibility override. `GEMSTONE_THRUST_ARCHES` accepts a comma-separated
list of `compute_N`, `sm_N`, `ccN`, `cc_N`, or bare numeric values; `sm_`/`cc`/numeric forms are normalized to the
PTX-producing `compute_N` form. Explicit targets are validated against NVCC's supported architecture list.
When device oracles are enabled, the suite queries the visible NVIDIA devices and runs the oracle on matrix targets no
newer than the least-capable device. `GEMSTONE_THRUST_ORACLE_ARCHES` can explicitly select a subset of the matrix; a
target newer than the available device is otherwise reported as oracle-skipped while its Gemstone/emulator lane still
runs. This is necessary because a `compute_90` executable cannot execute on an `sm_89` device.

On an emulator-only machine, set `GEMSTONE_THRUST_SKIP_ORACLE=1` to omit the NVIDIA lane and run only the Gemstone
lane. This is a diagnostic mode and does not provide oracle conformance results.

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
