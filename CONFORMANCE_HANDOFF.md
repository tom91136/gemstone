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
3. all 348 enabled rocThrust tests plus five HIP/HSA samples;
4. all 57 compute-applicable Khronos OpenCL CTS quick entries;
5. all 902 enabled oneDPL tests plus three samples through both Level Zero and OpenCL;
6. no unsupported operation silently succeeds and no Gemstone run reaches a physical GPU.

Upstream-disabled tests remain disabled. Do not add a Gemstone skip/pass allowlist.

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

## Current state on 2026-08-29

Branch: `main`. The worktree is a large intentional staged/WIP change set; do not reset or discard unrelated files.

Implemented foundation:

- official-header validation for CUDA, HIP, HSA plus AMD extensions, OpenCL plus Intel extensions, and Level Zero;
- generated CUDA Driver facade and authenticated typed RPC, including reverse callbacks;
- shared execution RPC with provider attestation;
- `gemstone run` planning for all public boundaries above;
- per-process usage counters and fail-closed attestation;
- PTX, AMDGPU, and SPIR-V loaders/interpreters with shared checked memory, geometry, budget, and cancellation;
- Level Zero provider and focused native tests;
- HSA AQL, doorbell, code-object, and execution mechanics;
- compile-once paired Conformance runners for CUDA, Thrust, rocThrust/HIP/HSA samples, OpenCL CTS, and oneDPL;
- pristine rocThrust setup with separately pinned GoogleTest;
- pinned official OpenCL CTS quick inventory;
- oneDPL Level Zero and OpenCL backend lanes.

Important incompleteness:

- CUDA Driver/PTX supports only an early subset of the full test surface;
- a public Gemstone HIP Runtime is not yet built;
- a public Gemstone `libhsa-runtime64` is not yet built—the existing hsakmt-shaped code is implementation material,
  not a deployment interface;
- the Gemstone OpenCL ICD is not yet built;
- Level Zero and SPIR-V semantics are far from full oneDPL coverage;
- AMDGPU semantics are far from full rocThrust coverage;
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
server protocol -> runtime/cuda, runtime/hsa, runtime/levelzero, future runtime/hip and runtime/opencl
api -> codegen
```

Parallel streams should own disjoint trees:

| Lane | Primary ownership |
| --- | --- |
| PTX/CUDA | `isa/ptx`, CUDA server adapter, `runtime/cuda`, `conformance/cuda` |
| AMDGPU/HIP/HSA | `isa/amdgpu`, AMDGPU server adapter, `runtime/hsa`, future `runtime/hip`, rocThrust samples/suite |
| SPIR-V/OpenCL/Level Zero/SYCL | `isa/spirv`, SPIR-V server adapter, `runtime/levelzero`, future `runtime/opencl`, OpenCL CTS and oneDPL |
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
```

Focused examples:

```console
GEMSTONE_THRUST_FILTER='^transform$' GEMSTONE_THRUST_JOBS=1 sbt testCUDAConformance
GEMSTONE_ROCTHRUST_FILTER='^samples/' GEMSTONE_ROCTHRUST_JOBS=1 sbt testROCmConformance
GEMSTONE_OPENCL_FILTER='^(Basic|API)$' sbt testOpenCLConformance
GEMSTONE_ONEDPL_FILTER='^levelzero/samples/' GEMSTONE_ONEDPL_JOBS=1 sbt testSYCLConformance
```

All external filters are Scala regular expressions. `GEMSTONE_<SUITE>_SHARD=index/count` provides deterministic
zero-based sharding. Worker counts are bounded and independent cases execute concurrently.

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

1. Package and exercise `gemstone run` with the existing CUDA and Level Zero providers.
2. Generate/implement the public HSA facade over the existing queue, signal, memory, code-object, and doorbell
   mechanics; make the five direct-HSA samples the first acceptance gate.
3. Implement the public HIP facade and compiler registration path; start with HIP SAXPY, then the smallest rocThrust
   cases.
4. Implement the OpenCL ICD platform/device/context/memory/program/kernel/queue surface; start with CTS Basic and API,
   then the SYCL OpenCL samples.
5. Advance each ISA from the smallest paired reds, preserving shared execution contracts.
6. Add a stronger Linux physical-device guard if provider isolation plus attestation is insufficient to prove that a
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

Generated sources are checked in. Change the owning generator, regenerate, format, and review generator plus output;
never hand-edit generated API or instruction definitions.
