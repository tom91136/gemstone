# Gemstone

Gemstone executes GPU programs on a checked CPU emulator for GPUless testing and debugging. It implements useful
subsets of PTX, AMDGPU, and SPIR-V behind native CUDA Driver, HIP, HSA, OpenCL, Vulkan, and Level Zero facades. Unsupported
behaviour fails explicitly; Gemstone never silently delegates to a physical GPU.

## Layout

- `core`: portable ABI, memory, RPC, and execution primitives.
- `api`: generated Scala API surfaces.
- `isa/{ptx,amdgpu,spirv}`: parsing, validation, lowering, and CPU execution.
- `server`: JVM and Scala Native execution server and process launcher.
- `native`: CMake-built API facades and RPC client.
- `tools/{codegen,benchmarks}`: generation and opt-in benchmarks.
- `conformance`: independent CMake/CTest conformance project.

Generated Scala sources and fixtures are checked in under each module's `src/generated` tree; native bindings are in
`native/generated`. Pinned dependency versions and hashes live in `build.sbt`.

## Development

The build requires JDK 25, sbt 2, CMake, Ninja, Clang, a C++ compiler, Python 3, SPIR-V Tools, and the LZ4 and Zstandard
development libraries. Building GPU conformance artifacts additionally requires a container engine.

| Command | Purpose |
| --- | --- |
| `sbt compile` | Compile all JVM and Scala Native modules. |
| `sbt test` | Run all Scala tests, building the native runtime as needed. |
| `sbt lint` | Check generated sources, Scala style, and native/conformance C++ style; do not run tests. |
| `sbt codegen` | Regenerate every checked-in output, then format it. |
| `sbt native/build` | Configure and build the CMake native runtime. |
| `sbt native/test` | Build and run the native CTest suite. |
| `sbt native/format` | Format the native C and C++ sources. |
| `sbt native/lint` | Check native formatting and code style. |
| `sbt 'conformance/build [target-set]'` | Build a conformance artifact matrix; the target set defaults to `default`. |
| `sbt conformance/test` | Build and run the conformance unit tests. |
| `sbt conformance/format` | Format the conformance C and C++ sources. |
| `sbt conformance/lint` | Check conformance formatting and code style. |
| `sbt 'conformance/run --machine <machine> --workload <compact\|full>'` | Run the selected workload directly against existing conformance artifacts. |
| `sbt 'conformance/emulate --machine <machine> --workload <compact\|full>'` | Run the selected workload through the emulator. |
| `sbt clean` | Remove sbt, native, and conformance build outputs. |

Use normal sbt scoping for focused work, for example `sbt ptx/test`, `sbt server/test`, or
`sbt serverNative/nativeLink`. Apply Scala formatting and rewrites without the other lint checks with
`sbt scalafmtAll scalafixAll`. Set `GEMSTONE_ISA_REFERENCES_OFFLINE=1` to require generation and freshness checks to
use the local pinned-reference cache.

## Running Gemstone

`gemstoneRun` builds the native runtime and runs a command with selected Gemstone API implementations:

```console
sbt 'gemstoneRun [--apis hip,hsa,cuda,opencl,vulkan,levelzero] -- <command> [argument ...]'
```

The APIs are independently selectable and may coexist. Linux defaults to `hsa,cuda,opencl,levelzero`, allowing a
vendor HIP runtime to use Gemstone HSA naturally; other platforms default to `hip,cuda,opencl,levelzero`. SYCL runtime
selection remains the application's responsibility through its OpenCL and Level Zero adapters. By default the launcher
owns an RPC server for the lifetime of the child process; `--server-port` instead registers the child with an existing
shared server.

The server and journal tools are also directly available:

```console
sbt 'server/run -- serve --endpoint-file target/endpoint.json --record target/run.gemstone'
sbt 'server/run -- shutdown --port <port>'
sbt 'server/run -- inspect target/run.gemstone'
sbt 'server/run -- replay target/run.gemstone'
```

## Conformance

Build the default artifact set through the independent, serial container matrix:

```console
sbt conformance/build
```

Artifacts are written to `target/conformance/artifacts`. Use `sbt 'conformance/build complete'` for the maintained
multi-generation matrix or `sbt 'conformance/build legacy-gfx803'` for the boxed legacy build. The default set
contains CUDA `sm_89`, HIP and HSA `gfx1030`, OpenCL, and SYCL. Architecture backends share runtime support
bundles rather than duplicating libraries in every ISA directory; the root driver is built once, and CUDA bundles are keyed by SDK.
Override `CONFORMANCE_BACKENDS`, `CUDA_ARCHITECTURES`, or `AMDGPU_ARCHITECTURES` when using the CMake project directly.
The conformance README documents the standalone workflow.

The run tasks build the generic conformance executable and launch existing artifacts directly or through the emulator:

```console
sbt 'conformance/run --machine <machine> --workload compact'
sbt 'conformance/run --machine <machine> --workload full'
sbt 'conformance/emulate --machine <machine> --workload compact'
sbt 'conformance/emulate --machine <machine> --workload full'
```

Pass a backend or target name to narrow the run. `--workload compact|full` selects a runtime workload catalogue; all
artifacts contain the full practical test inventory. Each invocation selects one machine profile; omit `--machine` to
use the profile matching the local short hostname.

```console
sbt 'conformance/run --machine <machine> hsa'
```

The standalone equivalents are:

```console
target/conformance/executable/bin/conformance run --profiles conformance/profiles --artifacts target/conformance/artifacts \
  --machine <machine> --workload full --output target/conformance/direct --ctest ctest
# Start this in a separate terminal and read the published port.
gemstone serve --port-file target/conformance-server.port
target/conformance/executable/bin/conformance run --profiles conformance/profiles --artifacts target/conformance/artifacts \
  --machine <machine> --workload full --output target/conformance/emulated --ctest ctest --isolated-launcher \
  --launcher -- gemstone run --server-port <port-from-file> --apis hsa,cuda,opencl,levelzero
```

The sbt emulation task owns one shared server and gives each test process an isolated server client. `CONFORMANCE_JOBS`
controls CTest parallelism. `GEMSTONE_CONFORMANCE_SERVER_HEAP` sets the shared server heap and defaults to 8 GB. See
[conformance/README.md](conformance/README.md) for artifact builds, profiles, workloads, and layout.

## Benchmarks

```console
sbt 'benchmarks/run -- list'
sbt 'benchmarks/run -- run <case> --warmup 1 --samples 5 -- [case arguments]'
```

The JVM benchmark process uses a 4 GB heap. `ptx-program-lifecycle` isolates PTX decode and launch preparation;
`ptx-register-index` measures register-name handling. Use `benchmarksNative` instead of
`benchmarks` for Scala Native. Results are emitted as versioned JSON.

Gemstone currently supports local JVM and Scala Native endpoints on Linux. Remote execution, physical-GPU forwarding,
and fault-driven UVM are out of scope.
