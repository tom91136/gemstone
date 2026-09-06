# Mini-app conformance handoff

Updated: 2026-09-05

This is a focused handoff for continuing the Polyregion mini-app integration. The immediate objective is to make the
available matrix fully green against its vendor oracles. The repository is a live worktree with other agent changes;
preserve unrelated modifications and do not reset, clean, or overwrite the worktree.

For the wider Conformance status and shared invariants, see [CONFORMANCE_HANDOFF.md](CONFORMANCE_HANDOFF.md). The
mini-app overview, pinned revisions, input sizes, relocation rules, and environment controls are in
[conformance/miniapps/README.md](conformance/miniapps/README.md).

## Current implementation

The suite is opt-in and lives in `conformance/src/test/scala/net/kurobako/gemstone/conformance/MiniappsConformanceSuite.scala`.
It currently defines these vendor-port models:

| Workload | Models currently defined |
| --- | --- |
| BabelStream | CUDA, HIP, OpenCL, SYCL, OpenMP target, Kokkos, OpenACC, StdPar/std-indices |
| miniBUDE | CUDA, HIP, OpenCL, SYCL, OpenMP target, Kokkos, OpenACC, StdPar/std-indices |
| CloverLeaf | CUDA, HIP, SYCL accessors, SYCL USM, OpenMP target, Kokkos, OpenACC, StdPar/std-indices |
| TeaLeaf | CUDA, HIP, SYCL accessors, SYCL USM, OpenMP target, Kokkos, StdPar/std-indices |

OpenACC is only planned when `GEMSTONE_MINIAPP_OPENACC_IMAGE` is provided. The available host matrix currently
produces 38 workload cases plus the mini-app scheduling regression; OpenACC was not present in that run.

The suite maintains the compile-once/paired-run invariant:

```text
one ordinary vendor-built executable
             /                    \
      vendor oracle          Gemstone provider
```

The same executable, arguments, and working directory are used for both runs. Gemstone runs must authenticate with the
selected provider; a zero exit without provider evidence is a failure, not a pass.

## Build and execution policy

The important distinction is intentional:

- app/model invocations are serial and isolated;
- the build of the current app/model is parallel internally.

`conformance/miniapps/tools/container-build.sh` receives a backend and several model specifications. It calls
`build_one` in a foreground loop, so it must not be changed to launch `build_one ... &`. Each `build_one` configures
with Ninja and runs:

```console
cmake --build "$build" --parallel "$build_jobs"
```

`GEMSTONE_MINIAPP_BUILD_JOBS` controls the inner build parallelism. Its default is `nproc`, capped at 8 to avoid
turning the full matrix into a memory-pressure event; set it to `1` for diagnosis or raise the cap deliberately on a
larger builder. `GEMSTONE_MINIAPP_JOBS` remains restricted to `1` because concurrent provider/emulator executions
would multiply the expensive emulation and compromise isolation.

The regression test `miniapps/container-build-keeps-model-compilers-isolated` checks all of the following:

- model-specific compiler selection is not leaked between builds;
- no background app-level build fan-out exists;
- the script uses Ninja;
- the script derives the bounded default from `nproc`;
- the individual build uses CMake inner parallelism;
- the build-job override remains present.

Do not optimize this by running all mini-apps as one-thread builds in parallel. If the run is slow, tune
`GEMSTONE_MINIAPP_BUILD_JOBS` or improve artifact caching first; the current full-matrix wall time is dominated by
Gemstone execution, especially SYCL/Level Zero, after artifacts are already built.

## Verified state

Commands and results from the latest run:

```console
bash -n conformance/miniapps/tools/container-build.sh

GEMSTONE_MINIAPPS=0 sbt --server -batch \
  'conformance / Test / testOnly net.kurobako.gemstone.conformance.MiniappsConformanceSuite'
# 5 total, 0 failed

sbt --server -batch \
  'amdgpu / Test / testOnly net.kurobako.gemstone.isa.amdgpu.CodeObjectSuite'
# 104 total, 0 failed
```

The expanded matrix was run with one case at a time and eight inner build jobs:

```console
GEMSTONE_MINIAPPS=1 \
GEMSTONE_MINIAPP_JOBS=1 \
GEMSTONE_MINIAPP_BUILD_JOBS=8 \
GEMSTONE_AMDGPU_TARGETS=gfx1036 \
GEMSTONE_AMDGPU_ORACLE_TARGET=gfx1036 \
GEMSTONE_CUDA_DRIVER="$PWD/target/conformance/cuda-gemstone/lib/libcuda.so.1" \
GEMSTONE_LEVELZERO_DRIVER="$PWD/target/conformance/levelzero-driver/libze_gemstone.so.1" \
GEMSTONE_HIP_RUNTIME="$PWD/target/out/jvm/scala-3.9.0/gemstone-runtime-hip/cmake/libamdhip64.so.7" \
GEMSTONE_HSA_RUNTIME="$PWD/target/out/jvm/scala-3.9.0/gemstone-runtime-hsa/cmake/libhsa-runtime64.so.1" \
GEMSTONE_OPENCL_ICD="$PWD/target/conformance/opencl-driver/icd" \
sbt --server -batch \
  'conformance / Test / testOnly net.kurobako.gemstone.conformance.MiniappsConformanceSuite'
```

Result after the bounded HIP interpreter-profile and lazy HIP module guards:
**39 passed, 0 failed, 0 ignored, 39 total, 589.12 seconds**. The 38 workload cases remain unchanged;
the additional passing test asserts that HIP Kokkos and StdPar models retain
serial interpreter dispatch until their cross-kernel ordering is validated.

This is the complete currently available matrix. The prior 32/6 run was
resolved by selecting the current disposable CUDA facade explicitly and the
current Level Zero driver explicitly; the default provider search paths had
been able to select stale DSOs. The four CUDA cases and the two Level Zero
cases now pass their paired oracle checks and provider-authentication guards.
OpenACC remains out of scope until `GEMSTONE_MINIAPP_OPENACC_IMAGE` is supplied.

The full output was captured in `/tmp/gemstone-miniapps-full.log` during the
latest run. Generated reports and artifacts are
under `target/conformance/miniapps/runs` and `target/conformance/miniapps/artifacts`; they are disposable build output,
not source changes.

## Important fixes already landed in this worktree

### AMDGPU VOP3 source-3 absolute modifier

`isa/amdgpu/src/main/scala/net/kurobako/gemstone/isa/amdgpu/CodeObject.scala` now decodes VOP3 source-3 absolute
from DWORD0 bit 10 (`0x400`). DWORD1 bit 26 belongs to the source-3 register encoding and must not be interpreted as
the absolute modifier. Source-3 negate remains DWORD1 bit 31.

The old decode changed ordinary `-v4` operations into `-|v4|`, which corrupted miniBUDE's force calculation. The
focused regression is:

```text
isa/amdgpu/src/test/scala/net/kurobako/gemstone/isa/amdgpu/CodeObjectSuite.scala
  "VOP3 f32 FMA does not confuse source 3 encoding with absolute"
```

The existing fused/non-fused FMA/MAD and FFBH boundary tests also pass. Do not remove these tests when modifying the
decoder or VOP3 execution.

Focused miniBUDE reproduction, useful after an AMDGPU change:

```console
GEMSTONE_MINIAPPS=1 \
GEMSTONE_MINIAPP_APPS=minibude \
GEMSTONE_MINIAPP_PORTS=omp-target \
GEMSTONE_MINIAPP_JOBS=1 \
GEMSTONE_MINIAPP_BUILD_JOBS=8 \
GEMSTONE_AMDGPU_TARGETS=gfx1036 \
GEMSTONE_AMDGPU_ORACLE_TARGET=gfx1036 \
GEMSTONE_HSA_RUNTIME="$PWD/target/out/jvm/scala-3.9.0/gemstone-runtime-hsa/cmake/libhsa-runtime64.so.1" \
sbt --server -batch \
  'conformance / Test / testOnly net.kurobako.gemstone.conformance.MiniappsConformanceSuite'
```

The host serial and OpenMP miniBUDE runs both produce approximately `865.52`; the corrected Gemstone OpenMP-target
case matches that oracle. Keep using host serial/OpenMP as an oracle when a miniBUDE result looks suspicious.

## Resolved blockers and remaining boundary

The available matrix is green. The fixes and their focused coverage are:

- CUDA provider selection: the current CUDA facade is passed explicitly via
  `GEMSTONE_CUDA_DRIVER`, and the CUDA mini-app cases authenticate and pass.
- Level Zero provider selection: the current driver is passed explicitly via
  `GEMSTONE_LEVELZERO_DRIVER`; CloverLeaf SYCL-USM and TeaLeaf SYCL-USM now
  complete teardown successfully.
- The complete available matrix was rerun serially after both fixes: 39/39,
  including the HIP model-profile regression guard.
- OpenACC is optional and remains pending an approved
  `GEMSTONE_MINIAPP_OPENACC_IMAGE`.

For future provider changes, start with one small case rather than the full
matrix:

```console
GEMSTONE_MINIAPPS=1 \
GEMSTONE_MINIAPP_APPS=babelstream \
GEMSTONE_MINIAPP_PORTS=cuda \
GEMSTONE_MINIAPP_JOBS=1 \
GEMSTONE_MINIAPP_BUILD_JOBS=8 \
GEMSTONE_AMDGPU_TARGETS=gfx1036 \
GEMSTONE_HSA_RUNTIME="$PWD/target/out/jvm/scala-3.9.0/gemstone-runtime-hsa/cmake/libhsa-runtime64.so.1" \
sbt --server -batch \
  'conformance / Test / testOnly net.kurobako.gemstone.conformance.MiniappsConformanceSuite'
```

Check the resolved provider path, artifact closure, `LD_LIBRARY_PATH`, and
the launcher's provider-first ordering. Confirm that the process loads the
Gemstone facade rather than a host stub/vendor library and that the provider
attestation is recorded.

Do not solve this by allowing a physical/vendor CUDA GPU: the mini-app must execute through Gemstone and carry the
provider attestation.

### Complete available matrix

After each focused fix, run the affected app/port/provider filter before the
full matrix. The matrix is intentionally serialized and can take roughly ten
minutes even with cached artifacts. Use the log and per-case reports to
distinguish build failures, provider startup failures, process timeouts, and
numerical mismatches.

### 5. OpenACC and host-dependent variants

Once an approved OpenACC image is available, set `GEMSTONE_MINIAPP_OPENACC_IMAGE` and run the OpenACC identities. Keep
the same compile-once/paired-oracle invariant and add tests for any toolchain-specific source staging. Kokkos and
StdPar already have compatibility staging in the private container workspace; never edit the pinned checkout.

## Test discipline

Add a regression test whenever a failure reveals a reusable contract:

- decoder bit-field or instruction semantics: `isa/amdgpu/.../CodeObjectSuite.scala`;
- provider discovery, authentication, queue/event, or teardown: the owning runtime/provider native or Scala suite;
- relocation, artifact closure, model selection, or build scheduling: `MiniappsConformanceSuite` or the mini-app
  tooling tests;
- output validation: keep the paired oracle and add a minimal deterministic report assertion.

Do not convert an unreliable upstream self-check into a pass condition. CloverLeaf pressure is the one explicitly
documented cosmetic exception; TeaLeaf must still match its fixed reference and its paired oracle. Do not add an
allowlist or skip to make the dashboard green.

Before handoff, run:

```console
git diff --check
bash -n conformance/miniapps/tools/container-build.sh
sbt check
sbt testAPIConformance
```

Then report the mini-app matrix with separate counts for passed, numerical mismatch, provider startup/authentication,
timeout, and teardown failures.
