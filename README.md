# Gemstone

Gemstone runs GPU programs on a checked CPU emulator. It implements subsets of PTX, SASS, AMDGPU, and SPIR-V behind
native CUDA Driver, HIP, HSA, OpenCL, Vulkan, and Level Zero facades, on the JVM and Scala Native, for Linux hosts.
Unsupported instructions, shapes, and relocations fail explicitly with the kernel and instruction offset. PTX loading
checks every instruction's availability against the module's `.version` and `.target`.

## Layout

| Path | Contents |
| --- | --- |
| `core` | Refinement types, numerics, ELF and source text scanning, execution RPC and session, backend interface, VM scheduling |
| `isa/{ptx,sass,amdgpu,spirv}-asm` | Instruction model, decoding, encoding, assembly text, containers, and validation |
| `isa/{ptx,sass,amdgpu,spirv}-vm` | Semantic compilation, execution, and each ISA's backend |
| `isa/nvidia` | Shared NVIDIA execution IR for PTX and SASS |
| `server` | Backend registry, client lifecycle, transport, recording and replay, launching |
| `native` | CMake-built API facades and RPC client |
| `tools/codegen` | Generators for checked-in sources under `src/generated` and `native/generated` |
| `tools/conformance` | Conformance run and emulation orchestration |
| `tools/{isa,benchmarks,test-support}` | ISA tools, benchmarks, shared test utilities |
| `samples` | Library API samples: assemble, run, disassemble and edit a kernel for each ISA (`sbt samples/run`) |
| `tools/codestyle` | C and C++ format, convention, and naming checks; terminology and binary file checks |
| `codestyle/publicapi` | Compiler plugin that limits the server to the public API of core and the ISAs; sbt-baumkuchen provides the Scalafix rules and cycle checks |
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
sbt core/stryker | amdgpuAsm/stryker | spirvAsm/stryker
```

`test` includes native and conformance CTest; `testFull` forces every Scala test on JVM and Native. `codegen`
regenerates checked-in outputs and CI fails if it changes the tree. `lint` rejects binary files: embed binary data as
text at its use site, with the commands that produce it in a comment. `native/lint` and `conformance/lint` check
`clang-format`, project conventions, and `.clang-tidy` identifier naming against each build's compile database. Coverage
writes `target/coverage/`; mutation writes each module's `target/stryker4s-report/`.

`codegen` recovers the SASS encoding tables from pinned NVIDIA `nvdisasm` releases, which needs Linux x86_64 and
`gcc`; decoding and assembly use the checked-in tables and need no NVIDIA tools. PTX and SASS test fixtures record
the recipe that produced them; `GEMSTONE_VERIFY_FIXTURES=1` reproduces each recipe with the pinned tools and compares
the result.

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

## Library

Each ISA is two artifacts on `gemstone-core`: `gemstone-isa-<isa>-asm` (instruction model, codecs, assembly text,
containers, validation) and `gemstone-isa-<isa>-vm` (lowering, execution, backend), for `ptx`, `sass`, `amdgpu` and
`spirv`. PTX and SASS execution also bring `gemstone-isa-nvidia`. Nothing is on Maven Central yet; `sbt publishLocal`
installs the snapshot.

```scala
libraryDependencies += "net.kurobako" %% "gemstone-isa-ptx-vm" % "0.1.0-SNAPSHOT"
```

A `Device` loads programs through a `BackendRegistry` and runs them against one `AddressSpace`, such as the reference
`ArrayMemory`:

```scala
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.ptx.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

val memory = ArrayMemory()
val output = memory.allocate(4L)
val device = Device(BackendRegistry(Vec(PTXBackend())), memory)
for {
  program <- device.load(ProgramFormat.PTXText, image)
  task    <- KernelTask.validated("answer", Dimensions.One, Dimensions.One, Vec(KernelTask.Argument.Memory(output, 0L)))
               .left.map(detail => Fault.InvalidRequest(detail))
  events  <- device.launch(program, task)
} yield memory.getInt(output, 0)
```

- `Device.launch` takes an optional `LaunchObserver`, which sees every instruction with the ISA's view of its state
  (`InvocationView`, `CohortView`, `WaveView`) and every device memory access; blocking it single-steps the launch.
- Faults carry a `Fault.Location`: kernel, function, instruction address, workgroup and lane.
- `ReplayDevice` resumes a launch from any observed instruction by deterministic replay.
- `Cubin.patchKernel`/`replaceKernel` and `CodeObject.patchKernel`/`replaceKernel` write edited kernels back into
  their containers, relinking when the code changes size.

`samples/` assembles, runs, disassembles and edits a kernel for every ISA (`sbt samples/run`); `API.md` records the
API's design and its known gaps. Versions follow early semantic versioning, and `lint` checks binary compatibility
against `Publishing.previousRelease` with MiMa once a release exists.

## Conformance

```console
sbt 'conformance/build [default|complete|legacy-gfx803]'
sbt 'conformance/run [--machine <profile>] [--workload <workload>] [<backend>[/<arch>[/clang|gcc]] ...]'
sbt 'conformance/emulate [--target-set <set>] [--workload <workload>] [all|<backend>|<backend>/<arch>|cuda-ptx|cuda-sass[/<arch>] ...]'
```

- Workloads: `compact` (default), `full`, `miniapps`, `cts-bounded`, `opencl-cts[-bounded]`, `sycl-cts[-bounded]`,
  `vulkan-cts[-bounded]`, `vulkan-info`. The Vulkan workloads serve both API versions.
- Profiles are `conformance/profiles/<profile>.toml`; `--machine` defaults to the local short hostname.
- `build` and `emulate` default to `conformanceTargetSet` (`default`: 11 routes); `emulate` builds its target set
  first. `complete` adds every CUDA and GFX architecture: 123 routes sharing 97 artifacts. `cuda` and `cuda/sm_89`
  select both code variants; `cuda-ptx` and `cuda-sass` select one.
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
cubin section for AMDGPU and SASS. SASS round-trips exact bytes; PTX assembly emits canonical text.
