# Gemstone

Gemstone runs GPU programs on a checked CPU emulator. It implements subsets of PTX, SASS, AMDGPU, and SPIR-V behind
native CUDA Driver, HIP, HSA, OpenCL, Vulkan, and Level Zero facades, on the JVM and Scala Native, for Linux hosts.
Unsupported instructions, shapes, and relocations fail explicitly with the kernel and instruction offset. PTX loading
checks every instruction's availability against the module's `.version` and `.target`.

## Layout

| Path | Contents |
| --- | --- |
| `core` | Refinement types, numerics, ELF and source text scanning, execution RPC and session, backend interface, VM scheduling |
| `isa/{ptx,sass,amdgpu,spirv}` | Decoding, validation, semantic compilation, and each ISA's backend |
| `isa/nvidia` | Shared NVIDIA execution IR for PTX and SASS |
| `server` | Backend registry, client lifecycle, transport, recording and replay, launching |
| `native` | CMake-built API facades and RPC client |
| `tools/codegen` | Generators for checked-in sources under `src/generated` and `native/generated` |
| `tools/conformance` | Conformance run and emulation orchestration |
| `tools/{isa,benchmarks,test-support}` | ISA tools, benchmarks, shared test utilities |
| `tools/codestyle` | C and C++ format, convention, and naming checks; terminology check |
| `codestyle` | Scalafix rules, the acyclic compiler plugin (reports source cycles as warnings), and their tests |
| `conformance` | Independent CMake conformance project, see [its README](conformance/README.md) |

`build.sbt` holds project wiring, `project/` generic build utilities, and `tools/codegen` the pinned references
(`sbt codegen` prepares them under `target/references/`). Generic test fixtures live in `tools/test-support`; fixtures
built on core types (memories, workgroups, ELF images) live in core's test sources, which dependent modules share.

## Requirements

JDK 25, sbt 2, CMake 3.25+, Ninja, Clang, `clang-format`, `clang-tidy`, a C++23 compiler, Python 3, and Podman (or
Docker with `CONTAINER_EXECUTABLE=docker`) for conformance and fixture builds. Downloads are cached in `.cache/`;
`GEMSTONE_OFFLINE=1` forbids them.

## Build and test

```console
sbt compile | test | testFull | lint | codegen
sbt '<project>/testOnly <suite-glob>'
sbt scalafmtAll scalafmtSbt | '<project>/scalafixAll'
sbt native/build | native/test | native/format | native/lint
sbt conformance/test | conformance/format | conformance/lint
sbt serverNative/nativeLink
sbt coverageJvm | 'coverageOnly <project> <suite-glob>'
sbt core/stryker | amdgpu/stryker | spirv/stryker
```

`test` includes native and conformance CTest; `testFull` forces every Scala test on JVM and Native. `codegen`
regenerates checked-in outputs and CI fails if it changes the tree. `native/lint` and `conformance/lint` check
`clang-format`, project conventions, and `.clang-tidy` identifier naming against each build's compile database. Coverage
writes `target/coverage/`; mutation writes each module's `target/stryker4s-report/`.

## Running

```console
sbt 'gemstoneRun [--apis cuda,hip,hsa,opencl,vulkan,levelzero] [--server <endpoint>] -- <command> [<argument> ...]'
sbt 'server/run serve [--listen tcp://<host>:<port>|unix:///<path>] [--endpoint-file <file>] [--record <journal>]'
sbt 'server/run shutdown --server <endpoint>'
sbt 'server/run inspect|replay <journal>'
```

`--apis` defaults to `hsa,cuda,opencl,vulkan,levelzero`; without `--server` the launcher owns a server. Clients read
`GEMSTONE_RPC_ENDPOINT` and `GEMSTONE_RPC_CLIENT`.

| Variable | Effect |
| --- | --- |
| `GEMSTONE_TRACE=cuda` | CUDA load and execution diagnostics |
| `GEMSTONE_CUDA_CODE=ptx\|sass` | Strict CUDA code selection |
| `GEMSTONE_EXECUTION_MAX_INSTRUCTIONS` | Per-task instruction budget |
| `GEMSTONE_EXECUTION_PARALLELISM` | Execution workers (default: half the cores, at most 64) |
| `GEMSTONE_VULKAN_API_VERSION=1.0\|1.2` | Vulkan version (default 1.2); not certified |

## Conformance

```console
sbt 'conformance/build [default|complete|legacy-gfx803]'
sbt 'conformance/run [--machine <profile>] [--workload <workload>] [<backend>[/<arch>[/clang|gcc]] ...]'
sbt 'conformance/emulate [--target-set <set>] [--workload <workload>] [all|<backend>|<backend>/<arch>|cuda-ptx|cuda-sass[/<arch>] ...]'
```

- Workloads: `compact` (default), `full`, `miniapps`, `cts-bounded`, `opencl-cts[-bounded]`, `sycl-cts[-bounded]`,
  `vulkan-cts[-bounded]`, `vulkan-info`. The Vulkan workloads serve both API versions.
- Profiles are `conformance/profiles/<profile>.toml`; `--machine` defaults to the local short hostname.
- `build` and `emulate` default to `conformanceTargetSet` (`default`: 11 routes); `emulate --target-set complete` runs
  what `build complete` built. `complete` adds every
  CUDA and GFX architecture: 123 routes sharing 97 artifacts. `cuda` and `cuda/sm_89` select both code variants;
  `cuda-ptx` and `cuda-sass` select one. Missing selected artifacts are errors.
- Artifacts go to `target/conformance/artifacts`; reports to `target/conformance/{direct,emulated}`.
- `emulate` runs one Gemstone server on a per-run Unix socket with an isolated client per test, and records JFR in
  `target/conformance/jfr/`, keeping the five newest recordings. Disable recording with
  `set conformance / conformanceJfr := false`.

| Variable | Effect |
| --- | --- |
| `CONFORMANCE_JOBS` | Concurrent emulated tests (default: half the cores, at most 16) |
| `CMAKE_BUILD_PARALLEL_LEVEL` | Job budget shared across artifact containers |
| `CONFORMANCE_ARTIFACT_PARALLELISM` | Concurrent artifact containers (default: cores / 8, at most 4) |
| `SYCL_CTS_ENABLE_FULL_CONFORMANCE=ON` | Full SYCL CTS type matrices |

## Benchmarks

```console
sbt 'benchmarks/run list'
sbt 'benchmarks/run run <case> [--warmup <n>] [--samples <n>] [--no-jfr] [-- <case argument> ...]'
sbt 'benchmarks/generateBenchmarkFixtures [cuda|hip|hsa|opencl]'
```

Cases: `amdgpu-{alu,atomic,dispatch-memory,dwordx4-copy,program-lifecycle,vector-copy}`,
`{cuda,hip}-cloverleaf-flux`, `journal-inspection`, `nvidia-control-flow`, `opencl-cts-async-copy`,
`ptx-{cache,calls,program-lifecycle,vector-add}`, `rpc-roundtrip`, `session-memory`, `spirv-execution`.
`--iterations` sets the measured operations per sample and `--workers` the host worker count. `amdgpu-vector-copy`
runs embedded gfx900 (wave64) instructions; `--image` loads `vector_copy` from a code object instead, such as the
gfx1030 (wave32) `hsa-vector-copy.hsaco` fixture. Use `benchmarksNative` for Scala Native. JVM runs record JFR in
`target/benchmarks/jfr/`. Fixtures go to `tools/benchmarks/target/generated-fixtures/`. Samples emit
`gemstone-benchmark-v2` JSON; pin CPUs (`taskset -c 0-7 sbt ...`) when comparing.

## ISA tools

```console
sbt 'isaTools/run disassemble --isa spirv|ptx|amdgpu|sass [--target gfx<n>|sm_<n>] [--wave-size 32|64] [--section <name>] [--output <file>] <input>'
sbt 'isaTools/run assemble --isa spirv|ptx|amdgpu|sass [--target gfx<n>|sm_<n>] [--wave-size 32|64] --output <file> <input>'
```

`--target` is required for AMDGPU and SASS; `--wave-size` applies to AMDGPU only; `--section` disassembles an ELF or
cubin section for AMDGPU and SASS. SASS round-trips exact bytes; PTX assembly emits canonical text. The `AsmBoundary`
Scalafix rule keeps the PTX and SASS runtimes free of their `asm` packages.
