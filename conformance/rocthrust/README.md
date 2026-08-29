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

Upstream CMake owns discovery and disabled-test policy. Gemstone does not keep
a second allowlist or silently redirect a virtual-device run to a physical
GPU. Each CTest instance is retained as an independent result so one missing
operation does not hide the rest of the failure surface.

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
GEMSTONE_ROCTHRUST_FILTER='^testing/(scan|sort)' sbt testROCmConformance
GEMSTONE_ROCTHRUST_FILTER='^samples/' sbt testROCmConformance
GEMSTONE_ROCTHRUST_SHARD=0/8 GEMSTONE_ROCTHRUST_JOBS=8 sbt testROCmConformance
```

The filter is a Scala regular expression over stable qualified case names.
Shards are zero-based `index/count`; filtering happens before sharding. Worker
count is 1-32 and defaults to eight. Target compilation is serialized through
one Ninja build tree while completed executables run concurrently.

The exhaustive suite is intentionally outside the fast `sbt check` lane. See
the repository README for the current conformance commands and filtering
controls. Archives are hash-verified in `~/.cache/gemstone/conformance`; all
build products remain under `target/conformance`.
