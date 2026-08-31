# oneDPL conformance

This lane exercises the complete upstream-enabled oneDPL inventory through both stock SYCL backend paths:

```text
oneDPL -> DPC++ -> Unified Runtime -> Level Zero loader
       -> libze_gemstone.so -> Gemstone SPIR-V execution

oneDPL -> DPC++ -> Unified Runtime -> OpenCL ICD loader
       -> Gemstone OpenCL ICD -> Gemstone SPIR-V execution
```

Upstream CMake owns recursive `*.pass.cpp` discovery, target variants,
configure-time exclusions, and runtime skip code 77. Gemstone retains every
discovered CTest instance as an independent result and has no local skip list.
A Gemstone run selects exactly one provider and fails closed rather than falling back to a physical device. Each
executable is compiled once. Its identical executable runs as the selected vendor-backend oracle and then through the
corresponding Gemstone backend, yielding separate `levelzero/...` and `opencl/...` MUnit results.

The pinned oneDPL commit is `3e0e05b32bf53850816f6a6bb09bcfded4f7edb7`
(archive SHA-256 `ce130630653c88a49844409a4ab307f54d2e5ace356499819556eb4d9c6106e3`).
Its bounded `TEST_LONG_RUN=0` configuration discovers 902 enabled CTest
instances. oneDPL has no smaller named size tier. The two backend lanes register 1,804 upstream results.

Three self-checking sample cases are discovered from
`samples/**/check_*.cpp` via `#pragma region` metadata, adding six backend/sample results. They cover raw SYCL USM
transfer/fill, device-policy dot product,
and USM `max_element`; the latter two are adaptations of official oneDPL
examples. The source headers retain oneDPL's Apache-2.0 with LLVM exception or
MIT licensing.

```console
sbt testSYCLConformance
GEMSTONE_ONEDPL_FILTER='fill\.pass' sbt testSYCLConformance
GEMSTONE_ONEDPL_FILTER='^gfx900/.*samples/' sbt testSYCLConformance
GEMSTONE_ONEDPL_SHARD=0/16 GEMSTONE_ONEDPL_JOBS=8 sbt testSYCLConformance
```

The filter is a Scala regular expression over stable CTest/sample names.
Shards are zero-based `index/count`; filtering happens before sharding. Worker
count is 1-32 and defaults to eight. Executable compilation is serialized through
one Ninja build tree while completed executables run concurrently. Every case
is qualified by its shared AMDGPU target (`gfx900/levelzero/...` or
`gfx900/opencl/...`), so the same SPIR-V-producing executable is checked
through each selected virtual target:

```console
GEMSTONE_AMDGPU_TARGETS='gfx900,gfx1036,gfx1100' GEMSTONE_ONEDPL_FILTER='^gfx1036/(levelzero|opencl)/' sbt testSYCLConformance
```

The oracle remains the installed physical backend; the virtual target is
passed only to Gemstone.

The exhaustive suite is intentionally outside the fast `sbt check` lane. See
the repository README for the current conformance commands and filtering
controls. The source archive is hash-verified in
`~/.cache/gemstone/conformance`; build products remain under
`target/conformance`.

The Level Zero provider is built by the suite. Set `GEMSTONE_OPENCL_ICD` to the isolated directory containing the
Gemstone `.icd` file for the OpenCL lane. Until that public ICD exists, each OpenCL-backed Gemstone case is an
intentional, individually visible red rather than a skipped test.
