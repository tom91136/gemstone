# GEMSTONE

GEMSTONE (GPU Execution Model for State-Tracked, Offload-Neutral Emulation) is a deterministic, vendor-neutral GPU
execution sandbox. The command and native-library name are `gemstone`; Scala packages live under
`net.kurobako.gemstone`.

This repository currently contains the first vertical scaffold:

- sbt 2 with Scala 3.9 and JVM/Scala Native project-matrix rows;
- official-header-validated API traits and deterministic JVM generators;
- a Scala 3 macro-derived `CUDA` trait contract with name-derived IDs, schema negotiation, and generated client/server dispatch;
- canonical runtime objects and a CUDA-shaped logical API handler;
- bounds-checked guest memory with provenance and use-after-free detection;
- deterministic block/lane ordering;
- aligned, version-packaged PTX 9.3 and AMDGPU ISA models with checked-in generated catalogs and oracle corpora;
- a version-packaged SPIR-V 1.6 binary model with the complete grammar catalogue, exact endian round trips, OpenCL
  Compute validation, and an initial bounded integer/control/memory executor;
- generated exact-C-name OpenCL and Level Zero API surfaces;
- a bounded PTX integer/control executor wired through module load, function lookup, and kernel launch RPC;
- deterministic JSON-lines instruction and output-write traces;
- a generated `libcuda.so.1` with authenticated RPC, recording, and reverse callbacks;
- an attested `gemstone run` launcher contract for CUDA Driver, HIP, direct HSA, OpenCL ICD, Level Zero, and SYCL;
- the same semantic tests compiled and run on the JVM and Scala Native.

The generated message boundary keeps native application pointers and callback entry out of managed server runtimes.
See [docs/API_TRANSPORT.md](docs/API_TRANSPORT.md). The hand-written semantics boundary and scheduler design are
described in [docs/ISA_EXECUTION.md](docs/ISA_EXECUTION.md).

The build separates reusable ISA models from native runtime frontends. `isa/{ptx,amdgpu,spirv}` contains the three
JVM/Scala Native project matrices; `runtime/{cuda,hip,hsa,opencl,levelzero}` contains the vendor-facing native libraries and
their CTest suites. The aggregate `isa` and `runtime` projects provide grouped commands without owning sources.

`core` is the dependency root and owns execution, memory, tracing, and the runtime collection boundary. `api` and each
ISA module depend on `core`; `codegen` depends on `api`; `server` composes `api`, `core`, and all three ISAs. Runtime
models use `Col[A]`, currently backed by `ArraySeq[A]`, so collection representation experiments are isolated to
`core/src/main/scala/net/kurobako/gemstone/Col.scala`. The project-wide Scalafix check rejects direct `Vector`,
`List`, `Seq`, `IArray`, or `ArraySeq` use in handwritten and generated runtime sources.

## Build

```console
sbt scalafmtCheckAll
sbt 'scalafixAll --check'
sbt 'codegen / checkGeneratedAPISources'
sbt 'ptx / generatePTXSources; ptx / generatePTXReferenceTests'
sbt 'amdgpu / generateAMDGPUSources; amdgpu / generateAMDGPUOracle'
sbt 'codegen / generateAPISources; spirv / generateSPIRVSources; spirv / generateSPIRVReferenceTests'
sbt test
sbt testFull
sbt testAPIConformance
sbt testCUDAConformance
sbt testROCmConformance
sbt testOpenCLConformance
sbt testSYCLConformance
sbt testConformance
sbt serverNative/compile
sbt check

cmake -S conformance/unified -B target/unified-conformance
cmake --build target/unified-conformance --parallel
ctest --test-dir target/unified-conformance --output-on-failure --parallel
```

`test` is incremental in sbt 2. Use `testFull` when every JVM and Native suite must be executed regardless of the
incremental test cache.

ISA generation is explicit and updates checked-in Scala sources. Compilation does not download references or rewrite
the worktree; conformance tests prepare their pinned, cached reference inputs without changing checked-in sources.

`testAPIConformance` registers every normalized CUDA Driver/Runtime, HIP, HSA plus AMD extension, OpenCL plus Intel
extension, and Level Zero declaration as an independent MUnit case. A missing or changed declaration therefore names
the exact C function instead of collapsing the API into one count failure. The current manifest contains 2,249
official declarations plus ten required CUDA compatibility/compiler ABI additions.

The fast `check` lane runs focused semantic and native boundary tests. Exhaustive vendor-library runs remain explicit.
`testCUDAConformance` covers the compact CUDA corpus and all 237 enabled Thrust 1.15 translation units at `tiny` size.
`testROCmConformance` covers focused HSA tests, 348 upstream rocThrust CTest instances, five HIP/HSA sample cases,
and the pinned rocBLAS smoke dataset.
`testOpenCLConformance` covers the 57 compute-applicable entries in the official Khronos OpenCL CTS quick inventory.
`testSYCLConformance` runs all 902 upstream-enabled oneDPL instances and three samples through both Level Zero and
OpenCL, retaining each backend/case pair as a separate result. Upstream disabled-test decisions remain authoritative.

`testConformance` is the local completion dashboard: per-declaration ABI checks, compact CUDA translation units, HSA
and Level Zero native CTest cases, and all three exhaustive vendor-library/sample lanes above. It is deliberately not
part of `check`; it requires NVCC, HIP/AMDGPU Clang, and DPC++ and is expected to expose unsupported Gemstone behavior
as named test failures while the implementation is being expanded. Filters, deterministic shards, and bounded worker
controls are documented in each conformance directory.

`conformance/unified` is an additional header-only C++20 workload suite; it does not replace the Scala/vendor
dashboards above. Its common catalog runs deterministic algorithm, BLAS, edge-case, and bounded-stress workloads
through host, CUDA/Thrust/cuBLAS, HIP/rocThrust/rocBLAS, or SYCL/oneDPL/oneMKL adapters. The supplied CMake runners are
split by suite and scalar type for practical vendor-compiler memory use while the reusable API remains header-only.

The AMDGPU-facing executable conformance suites share `GEMSTONE_AMDGPU_TARGETS`,
a comma-separated GFX matrix defaulting to `gfx900`, and
`GEMSTONE_AMDGPU_ORACLE_TARGET`, the physical target used where an oracle code
object must be selected. HIP fatbinaries and HSA code objects are target-aware;
OpenCL CTS and oneDPL artifacts are compiled once and run through each virtual
target on the Gemstone side. Oracle processes never receive the virtual target.

Checked-in generated Scala and resources live in each owning module's `src/generated/{main,test}` tree; handwritten
sources remain under `src/{main,test}`. Generators format Scala before comparing or writing it, and `scalafmtAll`
formats these generated roots as well, so regenerated diffs are canonical. CUDA's generated C artifacts and
language-neutral manifests live with their consumer in `runtime/cuda/generated`.

Every external Conformance case follows one invariant: compile once through the ordinary vendor toolchain, run that
exact artifact as the vendor oracle, then run it unchanged through Gemstone. There are no Gemstone compile defines,
alternate link lines, patched vendor runtimes, or private ROCr hooks. Each Gemstone child receives a fresh authenticated
server token; a zero exit is changed to a loud failure if the server observed no required CUDA or execution request.
For emulator-only iteration, `GEMSTONE_NO_ORACLE=1` skips the vendor execution while retaining the Gemstone run and
its assertions; this mode is not an acceptance result.

The public launch model is `gemstone run --api <api> [provider option] -- <ordinary command>`. CUDA replaces only the
Driver (`libcuda`), so applications may use the stock CUDA Runtime. HIP and direct HSA use drop-in public runtime
libraries, OpenCL uses an isolated ICD directory, Level Zero uses the loader's alternate-driver mechanism, and SYCL
selects exactly one of the OpenCL or Level Zero backends. See [docs/API_TRANSPORT.md](docs/API_TRANSPORT.md) and the
[Conformance handoff](CONFORMANCE_HANDOFF.md).

See [GEMSTONE_PLAN.md](GEMSTONE_PLAN.md) for the architecture, milestones, and permanent invariants.
