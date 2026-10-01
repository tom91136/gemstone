# Gemstone

Gemstone executes GPU programs on a checked CPU emulator for GPUless testing and debugging. It implements
subsets of PTX, AMDGPU, and SPIR-V behind native CUDA Driver, HIP, HSA, OpenCL, Vulkan, and Level Zero facades.
It runs on the JVM and Scala Native, with Linux as the supported host platform.

## Layout

- `core`: portable ABI, memory, RPC execution, and VM scheduling.
- `api`: generated Scala API definitions.
- `isa/{ptx,amdgpu,spirv}`: parsing, validation, lowering, and CPU execution.
- `server`: backend composition, TCP/Unix transport, recording/replay, and process launching.
- `native`: CMake-built API facades and RPC client.
- `tools/{codegen,benchmarks,test-support}`: generation, benchmarks, and shared Scala test utilities.
- `codestyle`: Scalafix rules and their tests.
- `conformance`: independent CMake/CTest conformance project.

Generated sources and fixtures are checked in under `src/generated` and `native/generated`.
Edit their generators and run `sbt codegen` to update them. Project dependencies, reference pins,
mutation campaigns, and the `GEMSTONE_*` environment table live in [build.sbt](build.sbt).
Reusable build utilities and bootstrap dependencies live in `project/`.

## Development

Use JDK 25, sbt 2 (pinned in `project/build.properties`), CMake 3.25+, Ninja, Clang, and a C++23 compiler.
Generation also needs Python 3; native formatting needs `clang-format`. Native dependencies, including
LZ4 and Zstandard, are fetched by CMake. Container artifact builds use Podman by default;
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
`emulate` uses `conformanceEmulationTargets` from `build.sbt`, independently of the host profile:
CUDA sm_89, HIP/HSA gfx1030, OpenCL, SYCL, Level Zero and Vulkan. Append a backend or route such as
`vulkan` or `cuda/sm_89` to narrow the selection. Missing selected artifacts are errors.
`CONFORMANCE_JOBS` controls emulated CTest parallelism.

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
```

Cases cover PTX, AMDGPU, SPIR-V, and journal inspection, including bounded OpenCL CTS async-copy,
CUDA/HIP CloverLeaf flux, and HSA vector-copy probes. These ISA probes validate their outputs and measure
emulator work; API client startup and queue handling are outside their timings.

Fixture generation accepts `cuda`, `hip`, `hsa`, `opencl`, or `all`. It invokes the independent conformance
artifact builder's pinned containers and writes binaries to the ignored
`tools/benchmarks/target/generated-fixtures/` directory. Binary fixtures are not checked in.

Use `benchmarksNative` instead of `benchmarks` for Scala Native. JVM benchmarks use a 4 GB heap.
Results include versioned JSON. The runner's `elapsed_nanos` covers setup, execution, and verification;
use case-specific timing fields such as `nsPerDispatch` when comparing execution alone.
