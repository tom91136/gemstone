# GEMSTONE

GEMSTONE (GPU Execution Model for State-Tracked, Offload-Neutral Emulation) is a deterministic, vendor-neutral GPU
execution sandbox. The command and native-library name are `gemsi`; Scala packages live under
`net.kurobako.gemstone`.

This repository currently contains the first vertical scaffold:

- sbt 2 with Scala 3.8 and JVM/Scala Native project-matrix rows;
- a typed Scala API registry and deterministic JVM generator;
- a Scala 3 macro-derived `CUDA` trait contract with name-derived IDs, schema negotiation, and generated client/server dispatch;
- canonical runtime objects and a CUDA-shaped logical API handler;
- bounds-checked guest memory with provenance and use-after-free detection;
- deterministic block/lane ordering;
- aligned, version-packaged PTX 9.3 and AMDGPU ISA models with checked-in generated catalogs and oracle corpora;
- a version-packaged SPIR-V 1.6 binary model with the complete grammar catalogue, exact endian round trips, OpenCL
  Compute validation, and an initial bounded integer/control/memory executor;
- generated exact-C-name OpenCL and Level Zero API surfaces;
- a small PTX single-block integer vector-add interpreter;
- deterministic JSON-lines instruction and output-write traces;
- a generated `libcuda.so.1`, pure-C dispatcher/recorder, and separated Scala Native core ABI;
- the same semantic tests compiled and run on the JVM and Scala Native.

The generated message boundary resolves Scala Native foreign-thread entry: eight C++ threads execute 16,000 calls
through the C queue without entering Scala. See [docs/API_TRANSPORT.md](docs/API_TRANSPORT.md) and
[docs/PHASE_0_FINDINGS.md](docs/PHASE_0_FINDINGS.md). The hand-written semantics boundary and scheduler design are
described in [docs/ISA_EXECUTION.md](docs/ISA_EXECUTION.md).

The build has eight module boundaries: `api`, `codegen`, `core`, `isa_ptx`, `isa_amdgpu`, `isa_spirv`, `server`, and `native`.
Each bare `projectMatrix` uses its matching repository directory directly; there are no manual source mappings or
redundant `.in(file(...))` declarations.

## Build

```console
sbt scalafmtCheckAll
sbt 'codegen/run --check generated'
sbt 'isa_ptx / generatePtxSources; isa_ptx / generatePtxReferenceTests'
sbt 'isa_amdgpu / generateAmdGpuSources; isa_amdgpu / generateAmdGpuGoldenTests'
sbt 'codegen / generateComputeApiSources; isa_spirv / generateSpirvSources; isa_spirv / generateSpirvReferenceTests'
sbt test
sbt testFull
sbt nativeNative/nativeLink
conformance/smoke/run.sh
```

`test` is incremental in sbt 2. Use `testFull` when every JVM and Native suite must be executed regardless of the
incremental test cache.

ISA generation is explicit and updates checked-in Scala sources. Ordinary compilation and tests do not download ISA
references or rewrite the worktree.

The [CUDA conformance corpus](conformance/cuda/README.md) contains self-checking, one-file tests adapted from the
BSD-licensed GPUOcelot suite. `CudaConformanceSuite` discovers them during `server` tests and runs their
`#pragma region` commands against NVIDIA CUDA and the enabled Gemstone implementation.

See [GEMSTONE_PLAN.md](GEMSTONE_PLAN.md) for the architecture, milestones, and permanent invariants.
