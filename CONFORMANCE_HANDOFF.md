# Gemstone full-Conformance handoff

This is the cold-start handoff for taking Gemstone from its present WIP state to full vendor-suite Conformance. It
assumes no conversation history.

## Goal

Gemstone is a deterministic virtual GPU. Unmodified applications are compiled and linked through the ordinary vendor
flow, then launched against public Gemstone provider libraries. PTX, AMDGPU, and SPIR-V execute in the shared managed
server without opening a physical GPU.

Completion means all of these are green:

1. `sbt check` and `sbt testAPIConformance`;
2. compact CUDA plus all 237 enabled Thrust 1.15 tests at `tiny` size;
3. all 348 enabled rocThrust tests, five HIP/HSA samples, and the pinned rocBLAS smoke dataset;
4. all 57 compute-applicable Khronos OpenCL CTS quick entries;
5. all 902 enabled oneDPL tests plus three samples through both Level Zero and OpenCL;
6. the portable `conformance/unified` algorithm, BLAS, edge-case, and bounded-stress matrices;
7. no unsupported operation silently succeeds and no Gemstone run reaches a physical GPU.

Upstream-disabled tests remain disabled. Do not add a Gemstone skip/pass allowlist.

For the current handoff, the requested completion scope is the non-legacy
portable unified matrix plus the available mini-app matrix. The imported
legacy identities and the exhaustive upstream vendor-suite matrices remain
explicitly out of scope for this gate; their status is retained as future
work below.

## Non-negotiable Conformance invariant

Every external case has one build and two executions:

```text
ordinary vendor compiler/linker
             |
             v
       one executable
          /       \
 vendor oracle   gemstone run
```

The executable, arguments, and working directory are identical. Only runtime provider selection differs. Never add:

- a Gemstone compile define;
- an alternate Gemstone link command;
- `nvcc --cudart=none`;
- a patched CUDA, ROCm, oneDPL, rocThrust, or CTS source;
- ROCr DTIF, `HSA_TOOLS_LIB`, libdtif, or substituted hsakmt;
- a user requirement to alter a vendor installation.

Pinned upstream archives are hash checked and cached under `~/.cache/gemstone/conformance`. Build products remain in
`target/conformance`.

## Public launch model

The intended UX is:

```console
gemstone run --api <cuda|hip|hsa|opencl|levelzero|sycl> [provider option] -- command args...
```

The Scala entry point is currently
`net.kurobako.gemstone.launcher.GemstoneLauncher`; packaging a standalone distribution script remains product work.

Emulated dispatches have a finite safety budget of 100,000,000 instructions by default. A deliberately selected large
workload can opt into a larger budget by setting `GEMSTONE_EXECUTION_MAX_INSTRUCTIONS` in the environment of the
Gemstone launcher; `GEMSTONE_EXECUTION_QUANTUM` optionally changes checkpoint granularity. Both values are validated
before the child is launched and malformed values fail closed. The profile is server-side and is not accepted from the
RPC client.

| Application frontend | Interception boundary | Selection mechanism |
| --- | --- | --- |
| CUDA Runtime or Driver | CUDA Driver `libcuda.so.1` only | preload plus provider-first library path |
| HIP | public HIP Runtime `libamdhip64` | drop-in preload/path |
| direct HSA | public HSA Runtime `libhsa-runtime64` | drop-in preload/path |
| OpenCL | OpenCL ICD | isolated `OCL_ICD_VENDORS` directory |
| Level Zero | Level Zero driver | `ZE_ENABLE_ALT_DRIVERS` |
| SYCL | exactly one OpenCL or Level Zero backend | provider selection plus `ONEAPI_DEVICE_SELECTOR` |

CUDA deliberately does not replace `libcudart`; stock Runtime calls are intercepted downstream at the Driver. HIP is
retained for systems where direct HSA is unavailable, including Windows. Direct HSA is a first-class public boundary,
not an internal ROCr hook.

`GemstoneProcess` creates a new loopback server and random 256-bit token for every child. The CUDA facade records an
authenticated Driver request; execution providers send an authenticated execution-protocol attestation. A process
that exits zero without the required evidence becomes failure 86. This detects failed injection and vendor-only
execution. Provider isolation must continue to ensure a process cannot use Gemstone and hardware in the same run.

## Current state on 2026-09-06

Branch: `main`. CUDA/PTX, AMDGPU/HIP/HSA, and SPIR-V/OpenCL/Level Zero were integrated as separate commits so further
vendor work remains bisectable.

Implemented foundation:

- official-header validation for CUDA, HIP, HSA plus AMD extensions, OpenCL plus Intel extensions, and Level Zero;
- generated CUDA Driver facade and authenticated typed RPC, including reverse callbacks;
- shared execution RPC with provider attestation;
- `gemstone run` planning for all public boundaries above;
- per-process usage counters and fail-closed attestation;
- PTX, AMDGPU, and SPIR-V loaders/interpreters with shared checked memory, geometry, budget, and cancellation;
- Level Zero provider and focused native tests;
- public HIP and HSA runtime providers plus HSA AQL, doorbell, code-object, and execution mechanics;
- public OpenCL ICD and Level Zero providers with focused native tests;
- compile-once paired Conformance runners for CUDA, Thrust, rocThrust/HIP/HSA samples, OpenCL CTS, and oneDPL;
- pristine rocThrust setup with separately pinned GoogleTest;
- a pinned rocBLAS build of the vendor-neutral bounded BLAS harness shared with cuBLAS and oneMKL;
- pinned official OpenCL CTS quick inventory;
- oneDPL Level Zero and OpenCL backend lanes.
- a separate header-only unified C++ workload catalog under `conformance/unified`, with host, CUDA, HIP, and SYCL
  runners, async dependency cases, expanded BLAS layouts, ROCm-derived iterator/set probes, a compact/full CTest
  profile, and parallel sharding.
- the imported legacy-red regression inventory is green for CUDA Thrust (56/56) but not yet fully green for the exact
  rocThrust/HSA binary lane: the latest authenticated run is 49/51 pass, with `random` and `unittest_static_assert`
  still failing. The expanded unified HIP semantic probes remain strict-green at 39/39. Focused HIP algorithms
  (including i8), BLAS, edge, and async profiles are currently strict-green; current tallies and reproduction
  commands are tracked in `UNIFIED_EMULATION_WORK.md`.

Important incompleteness:

- CUDA Driver/PTX supports only an early subset of the full test surface;
- the public HIP, HSA, OpenCL, and Level Zero surfaces are intentionally incomplete and will expose named vendor-suite
  reds until their API and execution behavior is filled out;
- Level Zero and SPIR-V semantics are far from full oneDPL coverage;
- the complete 348-test rocThrust inventory and full oneDPL/OpenCL CTS matrices remain future work; the imported
  50-case rocThrust legacy-red boundary has two remaining emulation failures (`random` and
  `unittest_static_assert`);
- the expanded unified profile is host-validated (1,812 portable cases; HIP lists 72 HIP-async and 39 additional ROCm
  semantic probes, for 1,923 HIP identities), and the non-legacy relocated CUDA/HIP/SYCL emulator matrix is now
  green at its documented workload bounds. The complete counts and the SYCL n1024/n257 policy are tracked in
  `UNIFIED_EMULATION_WORK.md`; the available mini-app matrix is also green at 39/39, with OpenACC pending an image.
- interpreter work-group parallelism is bounded by one shared, self-tuned JVM pool per server. General CUDA/SYCL
  runs default to host logical processors minus two; HIP/HSA defaults to two workers with serial matrix fan-out, and
  HIP Kokkos/StdPar mini-app models automatically retain serial dispatch until their cross-kernel ordering is
  validated. HIP fatbinaries now install lazily and share bounded immutable AMDGPU indexes across sessions. The
  measured non-legacy campaign timing is recorded in `UNIFIED_EMULATION_WORK.md`.
- the launcher is not yet packaged as a standalone installed command;
- Windows transport/provider selection still needs native implementation.

The missing HIP/HSA/OpenCL provider paths intentionally yield named reds. Do not “fix” them by pointing Gemstone
provider variables at vendor libraries; attestation exists to reject that mistake.

## Module ownership

```text
core
├── api
├── isa/ptx
├── isa/amdgpu
└── isa/spirv

api + core + all ISA modules -> server
server protocol -> runtime/cuda, runtime/hip, runtime/hsa, runtime/opencl, runtime/levelzero
api -> codegen
```

Parallel streams should own disjoint trees:

| Lane | Primary ownership |
| --- | --- |
| PTX/CUDA | `isa/ptx`, CUDA server adapter, `runtime/cuda`, `conformance/cuda` |
| AMDGPU/HIP/HSA | `isa/amdgpu`, AMDGPU server adapter, `runtime/hsa`, `runtime/hip`, rocThrust samples/suite |
| SPIR-V/OpenCL/Level Zero/SYCL | `isa/spirv`, SPIR-V server adapter, `runtime/levelzero`, `runtime/opencl`, OpenCL CTS and oneDPL |
| Integration | `core`, `api`, `native/execution`, launcher, Conformance support, `build.sbt` |

Land shared protocol changes through the integration lane before multiple ISA lanes depend on them. Do not force the
three ISAs into one generic execution loop: share memory, bits, geometry, faults, budget, cancellation, and tracing;
keep PTX reconvergence, AMD wavefront state, and SPIR-V structured execution in their owning modules.

## Commands and controls

Fast invariant gate:

```console
sbt check
sbt testAPIConformance
```

Vendor dashboards:

```console
sbt testCUDAConformance
sbt testROCmConformance
sbt testOpenCLConformance
sbt testSYCLConformance
sbt testConformance

GEMSTONE_UNIFIED_CONTAINER_RUN_CTEST=1 \
  conformance/unified/tools/container-build.sh host target/unified-artifacts/host
conformance/unified/tools/container-build.sh cuda
conformance/unified/tools/container-build.sh hip
conformance/unified/tools/container-build.sh sycl
conformance/unified/tools/run-relocated.sh target/unified-artifacts/hip hip --list

# Release-sized host matrix (the default CTest profile remains compact).
cmake -S conformance/unified -B target/unified-full -DUNIFIED_SUITE_CTEST_PROFILE=full
cmake --build target/unified-full --parallel
ctest --test-dir target/unified-full --output-on-failure --parallel
```

HIP relocation must include the complete rocBLAS Tensile closure under
`lib/rocblas/library`. `run-relocated.sh` exports `ROCBLAS_TENSILE_LIBPATH`
to that bundled directory automatically; an artifact with only a partial
Tensile set can abort inside rocBLAS before Gemstone receives a dispatch.

The unified runner is the one exception to the host-build convention: its compiler and SDK are selected by the
digest-pinned container wrapper in `conformance/unified/tools/container-build.sh`. The installed artifact is copied to
the host with relative RPATHs and bundled vendor user-space libraries, so Gemstone runs do not require the build image;
only the host ELF loader/libc and the physical vendor kernel driver (for an oracle) remain external requirements.

Focused examples:

```console
GEMSTONE_THRUST_FILTER='^transform$' GEMSTONE_THRUST_JOBS=1 sbt testCUDAConformance
GEMSTONE_ROCTHRUST_FILTER='^samples/' GEMSTONE_ROCTHRUST_JOBS=1 sbt testROCmConformance
GEMSTONE_OPENCL_FILTER='^gfx900/(Basic|API)$' sbt testOpenCLConformance
GEMSTONE_ONEDPL_FILTER='^gfx900/levelzero/samples/' GEMSTONE_ONEDPL_JOBS=1 sbt testSYCLConformance
```

All external filters are Scala regular expressions. `GEMSTONE_<SUITE>_SHARD=index/count` provides deterministic
zero-based sharding. Worker counts are bounded and independent cases execute concurrently.

For a reference-only legacy baseline, set `GEMSTONE_ORACLE_ONLY=1`. The paired
CUDA, Thrust, rocThrust, rocBLAS, OpenCL CTS, and oneDPL suites compile and run
the vendor artifacts without constructing Gemstone providers. The flag requires
the corresponding oracle and must not be combined with `GEMSTONE_NO_ORACLE` or
suite-specific oracle suppression. The native `hsa`, `hip`, `opencl`, and
`levelzero` smoke projects are provider tests and are not oracle-paired.

Provider variables:

```text
GEMSTONE_CUDA_DRIVER
GEMSTONE_HIP_RUNTIME
GEMSTONE_HSA_RUNTIME
GEMSTONE_OPENCL_ICD
GEMSTONE_LEVELZERO_DRIVER
```

Toolchain overrides include `NVCC`, `CUDA_HOME`, `GEMSTONE_HIPCC`, `GEMSTONE_AMDGPU_CLANG`, `GEMSTONE_DPCPP`,
`GEMSTONE_HSA_INCLUDE`, `GEMSTONE_ROCR_LIB`, and `GEMSTONE_OPENCL_LOADER`.

## Red-first iteration loop

For each named vendor red:

1. Keep the unchanged vendor-oracle run green. Record the command, environment, and first failure.
2. Add the smallest focused Scala or native test reproducing the missing contract and observe it red.
3. Classify ownership: facade/ABI, transport, loader/metadata, argument/pointer marshalling, queue/completion, or ISA
   semantics.
4. Implement at that owning layer. Do not hide ISA gaps in a host API special case.
5. Run the focused test, the selected paired vendor case, the owning module, then its shard.
6. Periodically run the whole lane and finish with `sbt check`.

Useful first triage:

| Failure | First owner |
| --- | --- |
| Compile/link or missing symbol | generated API/native facade |
| No virtual device/agent | public provider discovery and properties |
| No attestation/authentication | native execution/CUDA RPC transport |
| Module or code-object rejection | format loader and metadata decoder |
| Wrong pointer/value | native marshalling and checked allocation identity |
| Hung completion | queue/doorbell/command-list and cancellation |
| Unsupported opcode or wrong output | owning ISA decoder/semantics |

## Recommended next order

1. Package and exercise `gemstone run` across all five provider libraries.
2. Advance each public runtime from the smallest named vendor-suite red, preserving shared execution contracts.
3. Advance each ISA from the smallest paired reds; use `conformance/unified` for compact cross-vendor semantic
   comparisons before returning to the exhaustive upstream lane.
4. Add a stronger Linux physical-device guard if provider isolation plus attestation is insufficient to prove that a
   child cannot access `/dev/nvidia*`, `/dev/kfd`, or DRM render nodes after it has also contacted Gemstone.

## Files to read first

- `GEMSTONE_PLAN.md`
- `README.md`
- `docs/API_TRANSPORT.md`
- `docs/EXECUTION_PROTOCOL.md`
- `docs/ISA_EXECUTION.md`
- `docs/hsa/ROCR_BOUNDARY.md`
- `docs/sycl/ARCHITECTURE.md`
- `conformance/cuda/README.md`
- `conformance/rocthrust/README.md`
- `conformance/opencl/README.md`
- `conformance/onedpl/README.md`
- `conformance/unified/README.md`

Generated sources are checked in. Change the owning generator, regenerate, format, and review generator plus output;
never hand-edit generated API or instruction definitions.
