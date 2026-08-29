# GEMSTONE plan

## Objective

GEMSTONE is a deterministic, vendor-neutral virtual GPU for GPUless CI and
kernel debugging. It executes code produced by normal CUDA, HIP, and SYCL
toolchains without silently using physical hardware. Functional correctness,
checked state, reproducibility, and useful diagnostics take priority over
timing accuracy.

The supported host boundaries are CUDA Driver (with stock CUDA Runtime calls intercepted downstream), HIP, HSA plus AMD
extensions, OpenCL plus Intel extensions, and Level Zero. The execution
profiles are PTX 9.3, AMDGPU generations represented by the pinned Mesa/AMD
definitions, and SPIR-V 1.6.

## Permanent decisions

- Shared implementation code is Scala 3 and cross-compiles to JVM and Scala
  Native where its dependencies permit.
- The JVM service is the rapid development and end-to-end execution target.
  Scala Native remains a supported server build, not a separate architecture.
- Vendor-compatible native facades contain ABI mechanics only. Parsing,
  scheduling, memory policy, and instruction semantics live in shared code.
- Guest pointers are checked addresses with allocation identity and
  provenance. Managed code never dereferences an application pointer.
- Unsupported behavior returns a typed failure; it is never a successful
  no-op unless the vendor contract explicitly permits that treatment.
- The implementation is clean-room. Adapted tests retain licence and
  provenance, but no donor implementation tree or compatibility layer is
  carried forward.

## Module graph

```text
core
├── api
├── isa/ptx
├── isa/amdgpu
└── isa/spirv

api + core + all ISA modules -> server
server protocol -> runtime/cuda, runtime/hsa, runtime/levelzero, runtime/hip, runtime/opencl
api -> codegen
```

`core` owns checked memory, execution protocols, scheduling, tracing, binary
utilities, and `Col[A]`. `api` owns exact C-shaped Scala contracts and ABI
types. Each ISA module owns its versioned instruction model, parser/decoder,
validation, formatting, and execution semantics. `server` binds vendor
operations to those engines. Runtime modules own native libraries and their
focused CTest cases.

## Generated and handwritten boundaries

Pinned reference artifacts are downloaded explicitly into the external cache,
hash checked, and prepared below the codegen target. Ordinary compilation does
not access the network or rewrite the worktree.

Checked-in generated sources are formatted and reviewed like handwritten
sources, but live under `src/generated`. Their inputs are:

- NVIDIA PTX documentation for PTX instruction definitions and examples;
- Mesa ACO and AMD XML for normalized AMDGPU definitions;
- bounded LLVM MC fixtures as an independent AMDGPU assembly/encoding oracle;
- Khronos grammar JSON and SPIRV-Tools tests for SPIR-V;
- pinned official CUDA, HIP, HSA, OpenCL, and Level Zero headers for API
  validation.

Instruction values are typed enums carrying their operands, modifiers,
decoder, and metadata together. Generated data must not introduce parallel
catalogues or stringly typed metadata. Handwritten semantics use explicit
pattern matches over those instruction values and shared core utilities for
bits, byte order, memory, and control flow.

## Native boundary

Native facades preserve exact names, signatures, callbacks, loader lifecycle,
and error values. Requests cross an authenticated, versioned typed transport.
Large or aggregate values are copied or described by validated allocation
identity plus offset; raw addresses never cross as trusted guest pointers.

Callbacks use typed reverse RPC and an owning native registry. They retain
threading, ordering, failure, and lifetime rules, including calls made while a
foreign thread is blocked in a synchronization API.

HSA queues are CPU-visible AQL rings. An ordinary release atomic doorbell store
must wake the monitor and submit the packet; this cannot be replaced by an API
shim. Direct HSA uses a public `libhsa-runtime64`, never ROCr DTIF or hsakmt substitution. Level Zero is selected as an
alternate driver and OpenCL through an isolated ICD; SYCL selects exactly one of those providers.

Conformance compiles each application once through its normal vendor flow, runs the exact artifact as the vendor
oracle, then runs it unchanged through `gemstone run`. Each child must authenticate and attest actual Gemstone use.

## Execution model

- PTX uses lane registers and predicates, deterministic cohorts, explicit
  divergence, and block/warp rendezvous state.
- AMDGPU uses wave state, SGPR/VGPR files, SCC/VCC/EXEC, LDS, and generation-
  checked decode rules.
- SPIR-V uses typed SSA values, storage classes, structured control flow, and
  explicit workgroup/subgroup operations.

All engines share checked allocation access, endian/bit operations, launch
geometry, execution faults, tracing, and scheduler contracts. The simulation
hot loop may use controlled mutation and specialized storage after profiling;
control-plane and parsing code remains referentially transparent by default.

## Test lanes

The fast `sbt check` lane covers formatting, project Scalafix policy, generated
source freshness, official-header validation, JVM/Native semantic parity, and
focused native CTest cases.

`sbt testAPIConformance` registers every official C function as an independent ABI
case. Functional native-language tests remain independently discoverable:

- compact self-checking CUDA translation units use `#pragma region` metadata;
- the complete CUDA and upstream-enabled Thrust lane runs with `sbt testCUDAConformance`;
- rocThrust exercises public HIP/HSA, doorbells, and AMDGPU execution;
- oneDPL exercises SYCL through both Level Zero and OpenCL, then SPIR-V execution;
- the pinned OpenCL CTS quick inventory exercises the direct OpenCL boundary.

Upstream discovery and disabled-test policy are authoritative. Gemstone keeps
no silent skip list, reports each enabled case separately, and fails if a
virtual-device test touches a physical GPU device node.

## Remaining milestones

1. Implement the public HIP, HSA, and OpenCL provider libraries now represented by explicit Conformance reds.
2. Turn the current vendor-suite and official-sample failures green in dependency order: host ABI,
   allocation and queue semantics, code-object/module loading, kernel argument
   transport, then instruction and collective semantics.
3. Expand managed/shared allocation contracts only where a vendor suite needs
   them; avoid page-fault machinery until evidence justifies it.
4. Keep JVM and Native semantic results identical and add differential hardware
   oracles for every newly supported behavior.
5. Profile real library workloads before specializing dispatch, register
   storage, memory mirrors, or scheduler concurrency.
6. Stabilize packaging, trace/replay formats, debugger workflow, and explicit
   API/ISA support profiles.

## Quality invariants

- No Scala exception crosses a C ABI boundary.
- No managed Scala code runs in an asynchronous signal handler.
- Bounds, lifetime, direction, nullability, and pointer provenance are checked.
- Every supported instruction and API behavior has a focused semantic test.
- Generated definitions record their pinned version and content hash.
- Deterministic mode produces byte-identical results and traces for identical
  inputs and profiles.
- Test tooling never converts missing compilers, runtimes, implementations, or
  empty discovery into a green run.
