# rocThrust conformance

This lane exercises the complete upstream-enabled rocThrust test inventory through public HIP and HSA boundaries.
The vendor toolchain builds every executable once; the same artifact runs against installed ROCm as the oracle and
then through Gemstone's public provider.

## Pin

- upstream: `ROCm/rocThrust`
- tag: `rocm-7.2.4`
- commit: `ed551d53d9bac8fd55b2a7fc62683946e33060a0`
- codeload SHA-256: `e9b656bc5ab7568b07347825c4fc00d2940c1ca31ee4926e5862b0f6b9fb31e4`
- licence: Apache-2.0

Upstream CMake owns discovery and disabled-test policy. Gemstone does not
silently redirect a virtual-device run to a physical GPU. Each CTest instance
is retained as an independent result so one missing operation does not hide
the rest of the failure surface.

The upstream `*WithBigIndexes` cases are explicitly blacklisted at invocation
time. They submit ranges of 2^30 through 2^33 elements to validate very large
indexing on a real GPU, which cannot finish in the bounded CPU emulator. The
same applies to the 4.4-billion-element unique-by-key cases, the 2^31/2^32
`parallel_for` cases, the 10-billion-element constant iterator case, and the
deliberately enormous out-of-memory recovery case. The legacy harness filters
individual named cases and the newer GoogleTest binaries use the equivalent
GoogleTest filter; all other tests in those executables still run.

The two `address_stability` entries are host-only static/host assertions. They
run with Gemstone's HIP provider injected, but are intentionally not required
to produce an execution RPC.

The strict CPU emulator supports the typed `testing/unique_by_key` units,
including the custom-predicate device paths. To extend the emulator-only
exception list for a demonstrably impossible workload, set
`GEMSTONE_ROCTHRUST_EMULATION_BLACKLIST` to comma-separated `case:unit`
entries; this setting must not be used for ordinary correctness failures.

rocThrust is configured from the pristine pinned archive. GoogleTest 1.11 is downloaded, hash-checked, and installed
as a separate build dependency, so no upstream source patch is required. Gemstone neither builds a private ROCr nor
uses DTIF, `HSA_TOOLS_LIB`, or hsakmt substitution.

The configured inventory contains 348 enabled upstream CTest instances. The
legacy Thrust harness cases run at upstream's `tiny` size; the newer typed
rocThrust tests use their upstream bounded defaults. Five discovered sample
translation units extend the dashboard to 353 independently reported cases:

- the MIT-licensed SAXPY adaptation is pinned to `ROCm/rocm-examples`
  commit `143ead779346e6e7179100cf7c56d9f3562554ae` (archive SHA-256
  `566b19aedd133dc913cb25d4b97c60f01ef24f03668e86c4d45ca855e97f14cd`);
- the public-HSA cases cover agent/queue discovery, AMD memory pools and async
  copy, code-object loading, AQL dispatch, an ordinary atomic doorbell store,
  and completion. The dispatched kernel is the pinned ROCr NCSA fixture.

Every sample is a self-checking translation unit discovered from
`samples/**/check_*` through `#pragma region` compile/run metadata.
Its normal `hipcc` or C++/`libhsa-runtime64` compile command runs once; only process launch differs. Set
`GEMSTONE_HIP_RUNTIME` and `GEMSTONE_HSA_RUNTIME` to the built public Gemstone provider libraries. Missing providers
produce named Gemstone failures rather than falling through to hardware.

```console
sbt testROCmConformance
GEMSTONE_ROCTHRUST_FILTER='^gfx900/testing/(scan|sort)' sbt testROCmConformance
GEMSTONE_ROCTHRUST_FILTER='^gfx900/samples/' sbt testROCmConformance
GEMSTONE_ROCTHRUST_SHARD=0/8 GEMSTONE_ROCTHRUST_JOBS=8 sbt testROCmConformance
```

The filter is a Scala regular expression over stable qualified case names.
Shards are zero-based `index/count`; filtering happens before sharding. Worker
count is 1-32 and defaults to eight. Target compilation is serialized through
one Ninja build tree while completed executables run concurrently. Each MUnit
case is qualified by its target (`gfx900/testing/...`), so a comma-separated
`GEMSTONE_AMDGPU_TARGETS` value builds one fatbinary and runs the cases
against each selected GFX generation. The default is `gfx900`; the full
representative GFX-family matrix is:

```console
GEMSTONE_AMDGPU_TARGETS='gfx600,gfx700,gfx803,gfx900,gfx1010,gfx1100,gfx1200' sbt testROCmConformance
```

HIP samples carry all selected images in their fatbinary, while HSA sample code
objects are compiled separately for each selected target. The virtual provider target is passed only to the Gemstone process,
so the installed ROCm process remains the oracle environment. HSA samples use
`GEMSTONE_AMDGPU_ORACLE_TARGET` (default `gfx900`) for their physical ROCr
code object; that target must be included in `GEMSTONE_AMDGPU_TARGETS`
and match the installed GPU when running the full matrix.

`GEMSTONE_AMDGPU_TARGETS` and `GEMSTONE_AMDGPU_ORACLE_TARGET` are shared by
the executable AMDGPU-facing conformance suites. OpenCL CTS and oneDPL also
qualify each Gemstone result by target; their SPIR-V artifacts are compiled
once because they do not contain target-specific AMDGPU code objects.

The exhaustive suite is intentionally outside the fast `sbt check` lane. See
the repository README for the current conformance commands and filtering
controls. Archives are hash-verified in `~/.cache/gemstone/conformance`; all
build products remain under `target/conformance`.
