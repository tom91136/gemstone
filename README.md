# GEMSTONE

GEMSTONE (GPU Execution Model for State-Tracked, Offload-Neutral Emulation) is a deterministic, vendor-neutral GPU
execution sandbox. The command and native-library name are `gemstone`; Scala packages live under
`net.kurobako.gemstone`.

This repository currently contains the first vertical scaffold:

- sbt 2 with Scala 3.8 and JVM/Scala Native project-matrix rows;
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
- the same semantic tests compiled and run on the JVM and Scala Native.

The generated message boundary keeps native application pointers and callback entry out of managed server runtimes.
See [docs/API_TRANSPORT.md](docs/API_TRANSPORT.md). The hand-written semantics boundary and scheduler design are
described in [docs/ISA_EXECUTION.md](docs/ISA_EXECUTION.md).

The build has seven module boundaries: `api`, `codegen`, `core`, `isa_ptx`, `isa_amdgpu`, `isa_spirv`, and `server`.
Each bare `projectMatrix` uses its matching repository directory directly; there are no manual source mappings or
redundant `.in(file(...))` declarations.

## Build

```console
sbt scalafmtCheckAll
sbt 'codegen/run --check generated core/src/generated/main/scala'
sbt 'isa_ptx / generatePTXSources; isa_ptx / generatePTXReferenceTests'
sbt 'isa_amdgpu / generateAMDGPUSources; isa_amdgpu / generateAMDGPUGoldenTests'
sbt 'codegen / generateAPISources; isa_spirv / generateSPIRVSources; isa_spirv / generateSPIRVReferenceTests'
sbt test
sbt testFull
sbt serverNative/compile
```

`test` is incremental in sbt 2. Use `testFull` when every JVM and Native suite must be executed regardless of the
incremental test cache.

ISA generation is explicit and updates checked-in Scala sources. Ordinary compilation and tests do not download ISA
references or rewrite the worktree.

Checked-in generated Scala and resources live in each owning module's `src/generated/{main,test}` tree; handwritten
sources remain under `src/{main,test}`. Generators format Scala before comparing or writing it, and `scalafmtAll`
formats these generated roots as well, so regenerated diffs are canonical. The repository-level `generated/` tree is
reserved for generated C artifacts and language-neutral manifests.

The [CUDA conformance corpus](conformance/cuda/README.md) contains self-checking, one-file tests adapted from the
BSD-licensed GPUOcelot suite. `CUDAConformanceSuite` discovers them during `server` tests and runs their
`#pragma region` commands against NVIDIA CUDA and the enabled Gemstone implementation. The active Gemstone set now
includes Driver memory and PTX launch flows plus host-only Runtime allocation/copy cases.

See [GEMSTONE_PLAN.md](GEMSTONE_PLAN.md) for the architecture, milestones, and permanent invariants.
