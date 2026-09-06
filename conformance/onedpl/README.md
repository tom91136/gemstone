# oneDPL conformance

This lane exercises the pinned upstream oneDPL CTest inventory through both
provider paths:

```text
oneDPL -> DPC++ -> Unified Runtime -> Level Zero -> libze_gemstone.so
oneDPL -> DPC++ -> Unified Runtime -> OpenCL ICD -> Gemstone OpenCL ICD
```

Both providers execute the resulting SPIR-V through Gemstone. The suite uses
oneDPL commit `3e0e05b32bf53850816f6a6bb09bcfded4f7edb7` (archive SHA-256
`ce130630653c88a49844409a4ab307f54d2e5ace356499819556eb4d9c6106e3`), with
the upstream `TEST_LONG_RUN=0` configuration. That configuration discovers
902 enabled CTest instances and this repository adds three self-checking
samples.

The default run is the broad provider suite, with only cases that are not
meaningful or practical for this interpreter removed:

- `backend_inclusion.pass`, which is an external-backend check;
- fixed-size stress cases listed in `defaultStressExcluded` in
  `OneDPLConformanceSuite.scala` (large scans, merges, sorts, repeated-kernel
  stress, and similar cases); and
- all `std_ranges_*` cases, because this build deliberately has the upstream
  C++ standard-ranges lane disabled and those tests return the configure-time
  skip status.

Tests that explicitly invoke host policies or only check host-side types run
once through the host path. Fast provider correctness failures and crashes are
not hidden by the default exclusions. Set
`GEMSTONE_ONEDPL_INCLUDE_STRESS=1` to restore the duration-based exclusions;
the external-backend and disabled standard-ranges exclusions remain.

The suite sets Intel’s FP64 emulation variables before DPC++/IGC DSOs load:
`OverrideDefaultFP64Settings=1` and `IGC_EnableDPEmulation=1`. Persistent SYCL
caching is disabled for deterministic emulator runs; the ICD preserves explicit
pointer relocations without scanning every buffer byte. The OpenCL ICD is
created under `target/conformance/opencl-driver/icd`.

## Running

Use the memory-capped, serialized provider-only form when no physical GPU is
available:

```console
systemd-run --user --scope -p MemoryMax=32G -p MemorySwapMax=0 \
  env GEMSTONE_ONEDPL_ORACLE=0 GEMSTONE_ONEDPL_JOBS=1 \
  sbt --server -batch 'conformance / Test / testOnly net.kurobako.gemstone.conformance.OneDPLConformanceSuite'
```

Provider cases are qualified by the selected AMDGPU target and backend, for
example `gfx900/levelzero/...` and `gfx900/opencl/...`; host-only cases retain
the `host/...` prefix and run once. `GEMSTONE_AMDGPU_TARGETS` selects the target
matrix. The filter is a Scala regular expression over these fully qualified
names and is applied before sharding:

```console
systemd-run --user --scope -p MemoryMax=32G -p MemorySwapMax=0 \
  env GEMSTONE_ONEDPL_ORACLE=0 GEMSTONE_ONEDPL_JOBS=1 \
      GEMSTONE_AMDGPU_TARGETS=gfx900,gfx1036 \
      GEMSTONE_ONEDPL_FILTER='^gfx1036/opencl/.*' GEMSTONE_ONEDPL_SHARD=0/32 \
  sbt --server -batch 'conformance / Test / testOnly net.kurobako.gemstone.conformance.OneDPLConformanceSuite'
```

`GEMSTONE_ONEDPL_JOBS` is limited to 1–32 and should normally be `1` for the
interpreter. `GEMSTONE_ONEDPL_EXCLUDE` accepts a regular expression over the
unqualified upstream names. The suite retains the original CTest index when a
case is excluded, so CTest selection remains stable.

The suite is intentionally outside the fast `sbt check` lane. The three
repository samples are compiled and run as provider cases when selected; use
`GEMSTONE_ONEDPL_FILTER='samples/'` to select them.

For a vendor-only baseline, set `GEMSTONE_ORACLE_ONLY=1`. This still builds and
runs the selected oneDPL vendor/CTest cases (and samples), but does not build or
construct a Gemstone provider. It requires the oneDPL oracle to be enabled and
cannot be combined with `GEMSTONE_NO_ORACLE` or `GEMSTONE_ONEDPL_ORACLE=0`.

For a broad emulator run, set `GEMSTONE_ONEDPL_BATCH=1`. The suite builds
oneDPL once and runs exact-name CTest batches instead of creating one test
process per upstream case. `GEMSTONE_ONEDPL_BATCH_SIZE` defaults to 256,
`GEMSTONE_ONEDPL_BATCH_PARALLEL` defaults to 2 (use at most 4 on the serial
interpreter), and `GEMSTONE_ONEDPL_BATCH_TIMEOUT` controls each CTest case
(120 seconds by default). `GEMSTONE_CONFORMANCE_TIMEOUT_SECONDS` controls
the individual provider process timeout for the non-batch path and defaults to
the same 120 seconds.
With `GEMSTONE_ONEDPL_BATCH_ACCEPT_TIMEOUTS=1`, timeout-only batch failures are
reported as duration exclusions; correctness failures and crashes still fail
the suite. Use `GEMSTONE_ONEDPL_INCLUDE_STRESS=1` to restore the documented
duration exclusions, including the random-engine and large algorithm sweeps.

## Current provider-only status

The batch run currently passes the selected host, Level Zero, and OpenCL
inventories with the default duration exclusions; the run uses no physical
GPU or oracle comparison. `engines_methods.pass` remains excluded because its
upstream kernel writes one element past its declared allocation, which is an
invalid test for this strict emulator rather than a duration issue. The
large scan, sort, iterator, reverse, and random-engine sweeps remain opt-in
through `GEMSTONE_ONEDPL_INCLUDE_STRESS=1`.
