# OpenCL CTS conformance

This lane uses the official Khronos OpenCL CTS tag `v2026-07-09-00`, pinned by archive SHA-256 in `build.sbt`. The
source, OpenCL Headers, and SPIR-V Headers are downloaded into `~/.cache/gemstone/conformance`; no upstream source is
checked in or patched.

The CTS quick inventory is the upstream bounded suite. Gemstone registers each bounded provider-supported entry as
an independent MUnit test: image/mipmap entries are omitted because this provider reports no image support, while
CPU-qualified and disabled GL-interop entries are omitted because this lane explicitly selects a GPU and configures
the CTS without graphics interop. The entire suite is compiled once against the stock OpenCL loader. Each command
can run against the installed vendor ICD as an oracle and against Gemstone's isolated ICD directory; set
`GEMSTONE_OPENCL_ORACLE=0` for an emulator-only run that cannot touch a physical GPU.

The interpreter lane excludes monolithic entries whose upstream quick command still performs fixed-million-element,
exhaustive, compile-scaling, repeated type-matrix, or advertised-maximum allocation sweeps: Basic, API, Compiler,
Common, Geometric, Thread Dimensions, Atomics, Events, both Allocations entries, Buffers, Select, Contractions, Math,
Integer Ops, Half Ops, C11 Atomics, Non Uniform Work Groups, SVM, and SPIRV new. Their functional clusters are run
during development with executable selectors, while
`GEMSTONE_OPENCL_INCLUDE_STRESS=1` restores the unchanged full commands. Printf is excluded until the execution RPC
can return kernel output to the client process; `GEMSTONE_OPENCL_INCLUDE_PRINTF=1` restores it. Relationals is
excluded because three generated cases use LLVM translator-invalid vector widths 1, 14, and 15; the other 14 cases
pass, and `GEMSTONE_OPENCL_INCLUDE_RELATIONALS=1` restores the full entry.

OpenCL C source programs are compiled inside the provider with Intel's OpenCL-capable `icpx` followed by `llvm-spirv`.
The defaults target the installed oneAPI 2026.1 toolchain; set `GEMSTONE_OPENCL_COMPILER` and
`GEMSTONE_OPENCL_LLVM_SPIRV` to override those executables when running outside this environment.
For the interpreter-backed device, `GEMSTONE_OPENCL_MAX_MEM_ALLOC_SIZE` can lower the reported per-allocation
limit in bytes; this is useful with upstream CTS tests whose bounded problem size is derived from that limit. The
default is 256 MiB. `GEMSTONE_OPENCL_LOCAL_MEM_SIZE` can similarly lower the advertised local-memory size; this keeps
fixed-workgroup CTS cases such as async-copy tractable on the interpreter. Pointer relocation scanning of ordinary
buffers can be enabled with `GEMSTONE_OPENCL_SCAN_BUFFER_POINTERS=1`; SVM and USM allocations are scanned
automatically.
`GEMSTONE_OPENCL_TIMEOUT_SECONDS` sets the per-inventory-entry timeout (120 seconds by default), allowing short
parallel sweeps to classify hangs quickly during development.
`GEMSTONE_OPENCL_CASE_ARGS` appends an executable-specific test selector when iterating a single filtered inventory
entry.

```console
systemd-run --user --scope -p MemoryMax=32G -p MemorySwapMax=0 \
  env GEMSTONE_OPENCL_ORACLE=0 GEMSTONE_OPENCL_JOBS=8 \
  sbt --server -batch testOpenCLConformance

GEMSTONE_OPENCL_FILTER='^(Basic|API)$' sbt --server -batch testOpenCLConformance
GEMSTONE_OPENCL_SHARD=0/8 GEMSTONE_OPENCL_JOBS=8 sbt --server -batch testOpenCLConformance
```

For a vendor-only baseline, set `GEMSTONE_ORACLE_ONLY=1`. The selected CTS
commands are compiled and run against the installed OpenCL ICD, while
Gemstone's ICD is not built or loaded. This requires the oracle to be enabled
and cannot be combined with `GEMSTONE_NO_ORACLE` or `GEMSTONE_OPENCL_ORACLE=0`.

Cases are target-qualified and the Gemstone lane runs once for each target in
the shared AMDGPU matrix. The OpenCL CTS artifact is compiled once because it
contains SPIR-V rather than target-specific AMDGPU code:

```console
GEMSTONE_AMDGPU_TARGETS='gfx900,gfx1036,gfx1100' GEMSTONE_OPENCL_FILTER='^gfx1036/(Basic|API)$' sbt testOpenCLConformance
```

The oracle remains the installed physical OpenCL device. The shared
`GEMSTONE_AMDGPU_ORACLE_TARGET` setting is only needed when a lane requires a
physical-target code object, such as the direct-HSA samples in rocThrust.

Set `GEMSTONE_OPENCL_ICD` to a directory containing only Gemstone's `.icd` file. The provider must authenticate with
the execution server; missing provider discovery, silent vendor fallback, or a process that never uses Gemstone fails
loudly. `GEMSTONE_OPENCL_LOADER` may override the stock loader used at compile/link time.
