# Gemstone

Gemstone executes GPU programs on a checked CPU emulator for GPUless testing and debugging. It implements
subsets of PTX, SASS, AMDGPU, and SPIR-V behind native CUDA Driver, HIP, HSA, OpenCL, Vulkan, and Level Zero facades.
It runs on the JVM and Scala Native, with Linux as the supported host platform.

CUDA tensor kernels use the ordinary PTX execution path. Supported families include WMMA loads, stores and
matrix multiply-accumulate, dense `mma.sync` for FP16/BF16/TF32/FP64 and 8-bit integer inputs, `ldmatrix`/`stmatrix`
`m8n8.b16` with `x1`/`x2`/`x4` and transpose, and `cvt.rna.tf32.f32`. Unsupported shapes and qualifiers fail
explicitly; sparse MMA, WGMMA and `tcgen05` execution are not implemented. Tensor collectives require complete,
converged warps. WMMA uses a stable emulator fragment layout; MMA and matrix transfers follow the PTX lane layouts.

Gemstone does not intercept cuBLAS calls or provide BLAS-specific RPC operations. Vendor libraries use ordinary
CUDA module loading, allocation and kernel launches. The pinned CUDA 13.3 cuBLAS bundle contains PTX for `sm_120`
but also registers cubin-only modules. The generic `cuda-cubin` backend reads CUDA ELF metadata, binds
constant banks and globals (including named aliases into bank storage), and lazily compiles supported SASS
instructions directly into the shared NVIDIA execution IR. SASS and PTX are separate ISA modules; neither
frontend invokes the other. Both use the same checked NVIDIA execution engine.
Their handwritten text parsers share character scanning and source positions through the NVIDIA module.
It includes physical register pairs, predication, local memory, structured convergence, local device calls,
warp operations, atomics, dense HMMA and FP64 `DMMA.884`. Generated binary tables perform decoding,
and execution stays on Gemstone's CPU. Loading PTX or cubin images requires no NVIDIA toolkit;
unsupported binary forms report the kernel and instruction offset.
The CUDA 11.8 and 13.3 SDK pins are shared with the conformance artifact builder.
The synthetic CUDA device advertises four virtual SMs consistently through runtime and
driver queries. Libraries use this capacity to select grid dimensions; it is independent of CPU worker count.

Compiler-produced SAXPY fixtures cover sm_35, sm_50, sm_60, sm_70, sm_75, sm_80, sm_86, sm_87,
sm_89, sm_90, sm_100, sm_103 and sm_120. This is smoke coverage; broader legacy and Hopper/Blackwell
program coverage remains incomplete. The reconciliation onto `931d4fa` is validated by the current
[test record](GEMSTONE_NEW.md#local-sass-branch-reconciliation-record), including normal CUDA and
cubin-only WMMA runs. Earlier architecture-specific results remain in the preserved branch checkpoint.

Scala suites cover selected floating-point, packed-half, tensor, address, and carry behavior using
explicit launch metadata, text assembly and independent machine words. A small embedded ELF fixture
covers cubin loading; tests require neither NVIDIA tools nor checked-in binary files.
Maxwell/Pascal generic-memory lowering recognizes bounded compiler sequences with checked control flow;
those fixtures use CUDA 11.8 and independent VM results, without native Maxwell/Pascal measurements.

Unimplemented instructions, non-structured control-stack forms, external calls and unapplied
executable/global/constant relocations fail explicitly. CUDA 11.8 cuBLAS initializes through the generic
module path, but its legacy FP32 norm still requires the unverified `FCHK.DIVIDE` form. Full BLAS workloads
can exceed the default instruction limit; the validation record uses a two-billion-instruction budget.
No all-architecture BLAS or complete SASS conformance claim is made.

Conformance exposes `cuda-ptx` and `cuda-sass` routes with strict code selection.
`GEMSTONE_CUDA_CODE=ptx` requires PTX; `GEMSTONE_CUDA_CODE=sass` requires machine code
(`cubin` remains an alias). `CONFORMANCE_CUDA_CUBIN_ONLY=ON` builds conformance kernels
without embedded PTX. Set `GEMSTONE_TRACE=cuda` for load and execution diagnostics. PTX grid dependencies retain completion
ordering rather than launching dependent grids early. The CUDA case `runtime/cuda/tensor-wmma-gemm` checks an
nvcc-compiled WMMA kernel through normal allocation, launch and copy operations, with padded matrices, multiple
warps and fragment arithmetic. See [conformance](conformance/README.md) for mode-specific coverage.

## Layout

- `core`: portable ABI, memory, RPC execution, and VM scheduling.
- `api`: generated Scala API definitions.
- `isa/{ptx,sass,amdgpu,spirv}`: instruction decoding, validation and semantic compilation.
- `isa/nvidia`: shared NVIDIA execution IR, lane/warp state, memory and numerical semantics.
- `server`: backend composition, TCP/Unix transport, recording/replay, and process launching.
- `native`: CMake-built API facades and RPC client.
- `tools/{codegen,isa,benchmarks,test-support}`: generation, ISA tools, benchmarks, and shared Scala test utilities.
- `codestyle`: Scalafix rules and their tests.
- `conformance`: independent CMake/CTest conformance project.

Generated sources and fixtures are checked in under `src/generated` and `native/generated`.
Edit their generators and run `sbt codegen` to update them. Project dependencies, reference pins,
mutation campaigns, and the `GEMSTONE_*` environment table live in [build.sbt](build.sbt).
Reusable build utilities and bootstrap dependencies live in `project/`.

## Development

Use JDK 25, sbt 2 (pinned in `project/build.properties`), CMake 3.25+, Ninja, Clang, and a C++23 compiler.
Generation also needs Python 3 and builds a cached TableGen from the pinned LLVM source.
Native formatting needs `clang-format`. Native dependencies, including LZ4 and Zstandard, are fetched by CMake. Container artifact builds use Podman by default;
set `CONTAINER_EXECUTABLE=docker` to use Docker.

Run commands from the repository root:

| Command | Purpose |
| --- | --- |
| `sbt compile` | Compile JVM and Scala Native modules. |
| `sbt test` | Run affected Scala tests plus native and conformance unit tests. |
| `sbt testFull` | Force all Scala tests to run on JVM and Scala Native. |
| `sbt 'server/testOnly *'` | Force the server JVM tests to run. |
| `sbt lint` | Check generation freshness, environment names, Scala style, and C++ style. |
| `sbt scalafmtAll scalafmtSbt` | Format Scala sources and the build. |
| `sbt codegen` | Regenerate and format checked-in outputs. |
| `sbt native/build` | Build the native API facades and RPC client. |
| `sbt native/test` | Build and run native CTest tests. |
| `sbt conformance/test` | Build and run conformance unit tests. |
| `sbt serverNative/nativeLink` | Build the Scala Native CLI executable. |
| `sbt clean` | Remove build outputs, reports, and local reference caches. |

Scope commands to a project for focused work, such as `sbt 'ptx/testOnly *ArithmeticExecutionSuite'`.
Apply Scalafix rewrites one JVM project at a time, for example `sbt core/scalafixAll scalafmtAll`;
JVM and Native projects share source files. Use `native/format` and `conformance/format` for C++.
`GEMSTONE_OFFLINE=1` requires cached generator references and disables conformance artifact fetches.

Generic fixtures live in `tools/test-support`; API and ISA fixtures stay with their owning module.
`server/testOnly *CLIProcessSuite` exercises the CLI as a child process. To test a linked Native CLI,
set `server / Test / javaOptions += "-Dgemstone.cli.executable=/absolute/path/to/executable"`
before running that suite.

### Coverage and mutation testing

```console
sbt coverageJvm
sbt 'coverageOnly ptx *ArithmeticExecutionSuite'
sbt core/stryker
sbt amdgpu/stryker
sbt spirv/stryker
```

Coverage uses JaCoCo and merges execution across JVM modules, including library code reached through server tests.
Full HTML/XML reports are under `target/coverage/full/`; focused reports are under
`target/coverage/focused/<project>/`. Failed runs preserve the previous report.
Child CLI processes, Scala Native, and C++ are outside this coverage measurement.

Mutation campaigns target core memory/RPC helpers, the AMDGPU parser, and SPIR-V binary decoding.
Their source and suite selections are in `build.sbt`; reports are under each module's
`target/stryker4s-report/`. Review survivors for meaningful missing assertions; coverage and mutation
scores alone do not establish correctness or CTS conformance.

## Running Gemstone

`gemstoneRun` builds the native runtime and launches a command with the selected API facades:

```console
sbt 'gemstoneRun --apis cuda,opencl -- <command> [argument ...]'
```

Available APIs are `cuda`, `hip`, `hsa`, `opencl`, `vulkan`, and `levelzero`. The Linux default is
`hsa,cuda,opencl,vulkan,levelzero`, allowing a vendor HIP runtime to use Gemstone HSA.
SYCL adapter selection remains the application's responsibility.

The launcher owns a server for the child process unless given `--server <endpoint>`.
Endpoints use `tcp://host:port` or `unix:///absolute/path`; encode reserved URI characters such as spaces.
The default listener is `tcp://127.0.0.1:0`, which selects an available port.

```console
sbt 'server/run -- serve --endpoint-file target/endpoint.json --record target/run.gemstone'
sbt 'server/run -- shutdown --server <endpoint>'
sbt 'server/run -- inspect target/run.gemstone'
sbt 'server/run -- replay target/run.gemstone'
```

The endpoint file contains the endpoint URI and client identity. The launcher configures native clients through
`GEMSTONE_RPC_ENDPOINT` and `GEMSTONE_RPC_CLIENT`. Journals capture execution RPC traffic;
replay checks the recorded schema before execution.

## Conformance

```console
sbt conformance/build
sbt 'conformance/run --machine <machine> --workload compact'
sbt 'conformance/emulate --workload compact'
```

Builds write artifacts to `target/conformance/artifacts`. `run` uses the existing artifacts directly;
`emulate` runs them through a shared Gemstone server with an isolated client per test process.
Use `--workload full` for the bounded regression suite, including OpenCL, SYCL and Vulkan CTS.
`run` uses machine profiles; omitting `--machine` selects the local short hostname.
Builds and emulation use `conformanceTargetSet` (default `complete`): 13 CUDA architectures, each tested as PTX and SASS,
27 GFX targets each for HIP and HSA, OpenCL, SYCL, Level Zero and Vulkan. Both compact and full
run this entire matrix, independently of machine profiles. Append a backend or route such as
`vulkan`, `cuda-ptx/sm_89` or `cuda-sass/sm_89` to narrow execution. `cuda` and `cuda/sm_89`
select both code variants; `cuda-ptx` and `cuda-sass` select one across all CUDA architectures.
The 84 execution routes share 71 artifacts. Missing selected artifacts are errors.
For the smaller matrix, set `conformance / conformanceTargetSet := "default"` before building and running.
The full architecture matrix multiplies runtime compared with a single architecture per backend.
For `conformance/emulate`, concurrent test jobs default to half the available processors, rounded down,
with a minimum of 1 and a maximum of 16; `CONFORMANCE_JOBS` overrides this count.
The server uses a unique Unix socket per run; set `Global / conformanceServerListen := Some("tcp://127.0.0.1:0")`
to use TCP instead.
Emulation records the server with JFR by default in `target/conformance/jfr/emulate-*/server.jfr`
and prints the absolute path after shutdown, including failed runs. Set `Global / conformanceJfr := false` to disable it.
Recordings use the `profile` settings and retain up to 512 MiB (older events rotate out).
The shared execution pool also defaults to half the available processors, with a minimum of 1
and a maximum of 64; `GEMSTONE_EXECUTION_PARALLELISM` overrides its worker count.
GCC OpenMP's device team limit matches this worker count; `OMP_NUM_TEAMS_DEV` overrides it.
OpenMP threads per device team default to 2; `OMP_TEAMS_THREAD_LIMIT_DEV` overrides this limit.
GCC maps these threads to warps or waves, so this retains communication between them while reducing
runtime setup for small workloads. The workload and its iteration count are unchanged.
The shared server heap allows 2 GiB for shared state plus 6 GiB per test job, with an 8 GiB minimum
and a 28 GiB cap to retain compressed references. Its initial heap is 4 GiB.
`Global / conformanceServerJavaOptions` controls JVM options for this server only.
Client JVMs use separate 256 MiB heaps.

See [conformance/README.md](conformance/README.md) for target sets, toolchains, profiles, and standalone commands.

CTS workloads need no backend suffix, e.g. `conformance/emulate --workload sycl-cts-bounded`.
OpenCL emulation defaults to 256 elements, wimpy mode, and 16 atomic iterations with up to 128 threads.
Use `CONFORMANCE_JOBS=1` for serial execution. `compact` and `full` are regression selections;
see the conformance guide for CTS controls. For full SYCL type coverage, build with
`SYCL_CTS_ENABLE_FULL_CONFORMANCE=1`. `CMAKE_BUILD_PARALLEL_LEVEL` sets the shared build job budget;
`CONFORMANCE_ARTIFACT_PARALLELISM` caps concurrent artifact pipelines.

### Vulkan

Vulkan supports compute and headless graphics. Native code manages API objects and queues;
Scala executes shaders, rasterization and image operations through the shared execution service.

The default target is Vulkan 1.2. Set `GEMSTONE_VULKAN_API_VERSION=1.0` in the launcher environment
to select 1.0, and use the corresponding `vulkan10` or `vulkan12` conformance workload.
The implementation is not certified.

## Benchmarks

```console
sbt 'benchmarks/run -- list'
sbt 'benchmarks/generateBenchmarkFixtures all'
sbt 'benchmarks/run -- run opencl-cts-async-copy --warmup 1 --samples 5 -- --direction global-to-local --stride 2'
sbt 'benchmarks/run -- run cuda-cloverleaf-flux --warmup 1 --samples 5 -- --cells 64'
sbt 'benchmarks/run -- run ptx-cache --warmup 2 --samples 3 -- --threads 8 --images 8 --iterations 10'
sbt 'benchmarks/run -- run amdgpu-program-lifecycle --warmup 3 --samples 5 -- --image /path/to/kernel.co --iterations 10'
sbt 'benchmarks/run -- run nvidia-control-flow --warmup 3 --samples 5 -- --isa sass'
sbt 'benchmarks/run -- run session-memory --warmup 3 --samples 5 -- --workers 4 --shared'
sbt 'benchmarks/run -- run rpc-roundtrip --warmup 5 --samples 7 -- --transport uds --iterations 10000'
```

Cases cover PTX, SASS, AMDGPU, SPIR-V, and journal inspection, including bounded OpenCL CTS async-copy,
CUDA/HIP CloverLeaf flux, and HSA vector-copy probes. These ISA probes validate their outputs and measure
emulator work; API client startup and queue handling are outside their timings.
`ptx-calls` models nested GCC-style device calls, divergence, parameter spills, and shared-memory exchange with barriers.
`spirv-execution --mode arithmetic|calls` compares scalar arithmetic with typed function calls and verifies every invocation.
`nvidia-control-flow --isa ptx|sass` exercises predicate-heavy execution through each ISA's decoder and compiler.
`amdgpu-dispatch-memory` measures routing between ELF aliases, sections, and external memory.
`session-memory` exercises allocation lookup, read preparation, atomics, and device-heap access through a real
execution session. `--shared` selects contended words; otherwise workers use separate cache lines.
These cases use synthetic fixtures and verify their results. They exclude socket and native API overhead.
`rpc-roundtrip --transport tcp|uds` verifies pings over a persistent connection to a real server, excluding startup
and connection setup. Allocation measurements cover the client thread only.
The `ptx-cache` probe measures concurrent backend loads and unloads; use `--images 1` to measure
shared parsing. Its allocation count includes all client threads; operations count loads across those threads.
The `amdgpu-program-lifecycle` probe repeatedly loads an AMDGPU ELF code object and enumerates its
symbols. It reports module initialization time and allocation, including cache lookup, without RPC
or client startup costs. Supply a code object from the workload being investigated.

Fixture generation accepts `cuda`, `hip`, `hsa`, `opencl`, or `all`. It invokes the independent conformance
artifact builder's pinned containers and writes binaries to the ignored
`tools/benchmarks/target/generated-fixtures/` directory. Binary fixtures are not checked in.

Use `benchmarksNative` instead of `benchmarks` for Scala Native. JVM benchmarks use a 4 GB heap.
Each JVM benchmark invocation records JFR across warmup and samples in `target/benchmarks/jfr/`, relative to its
working directory, and prints the absolute path to stderr on completion. Recordings use `profile` settings and
retain up to 512 MiB. Pass `--no-jfr` before the case-argument `--` for timing comparisons without profiling.
Scala Native does not record JFR.
Each measured sample emits `gemstone-benchmark-v2` JSON with `elapsed_nanos`, `operation_count`,
`allocated_bytes`, and `allocation_scope`. Divide totals by operations for per-operation comparisons.
Timing covers the case's measured work; setup and result verification are excluded. Lifecycle cases intentionally
measure loading. The runner controls warmup and sample counts. Integer counters are decimal strings.
Allocation scope is `calling-thread`, `workers` (explicitly summed task measurements), or `unavailable`.
Calling-thread counts exclude allocations on worker threads; unavailable counts are null.
Use one worker for interpreter allocation comparisons; use concurrent cases to measure throughput and contention.

For performance work, pin CPUs (for example, prefix sbt with `taskset -c 0-7`) and keep worker counts and workload
arguments fixed. Iterate on the affected benchmarks and regression tests, use compact conformance to check whether
the benchmark gains carry over, and run full conformance after a batch of changes. If conformance exposes a cost
the benchmarks miss, add or adjust a case before the next optimization.

## ISA tools

```sh
sbt 'isaTools/run disassemble --isa spirv shader.spv'
sbt 'isaTools/run assemble --isa spirv --output shader.spv shader.spvasm'
sbt 'isaTools/run disassemble --isa amdgpu --target gfx1030 instructions.bin'
sbt 'isaTools/run disassemble --isa amdgpu --target gfx900 --section .text kernel.o'
sbt 'isaTools/run disassemble --isa sass --target sm_89 --section .text.kernel kernel.cubin'
sbt 'isaTools/run assemble --isa sass --target sm_89 --output kernel.bin kernel.sass'
sbt 'isaTools/run assemble --isa ptx --output canonical.ptx kernel.ptx'
```

Assembly tooling lives in each ISA’s `asm` package. Executable-input decoding stays shared, including PTX’s text parser.
SASS accepts raw code sections or cubin executable sections for disassembly and emits raw code sections for assembly.
Printed encoding annotations and legacy bundle directives preserve exact bytes; labels resolve to physical instruction addresses.
PTX assembly emits canonical UTF-8 PTX text. `sbt 'isaTools/nvidiaRuntimeCheck'` freshly compiles PTX and SASS
with their `asm` sources excluded and executes a kernel through each runtime.
SPIR-V uses typed instructions and preserves binary headers when disassembled and reassembled.
Text parsing/printing retains comments, normalizing their placement to statement boundaries;
ordinary source comments have no representation in the binary output.
AMDGPU supports scalar/vector ALU, DPP/SDWA lane controls, MFMA, dual-issue pairs,
interpolation, image/export, LDS, buffers and flat/global/scratch memory forms, including RDNA4
multiword layouts. Input can be a raw instruction stream or an ELF executable
section; extraction does not require kernel descriptors. Unresolved text relocations are rejected.
Coverage varies by target and operand form; unsupported forms fail explicitly.
AMDGPU assembly resolves labels and local branch offsets and supports text, data, and alignment directives.
ELF text relocations resolve local symbols; the API accepts external symbol and section addresses.
Use `--wave-size 64` for wave64 matrix assembly/disassembly (default: 32).
