# OpenCL CTS conformance

This lane uses the official Khronos OpenCL CTS tag `v2026-07-09-00`, pinned by archive SHA-256 in `build.sbt`. The
source, OpenCL Headers, and SPIR-V Headers are downloaded into `~/.cache/gemstone/conformance`; no upstream source is
checked in or patched.

The CTS quick inventory is the upstream bounded suite. Gemstone registers its 57 compute-applicable entries as
independent MUnit tests: CPU-qualified and disabled GL-interop entries are omitted because this lane explicitly
selects a GPU and configures the CTS without graphics interop. The entire suite is compiled once against the stock
OpenCL loader. Each command then runs unchanged against the installed vendor ICD as the oracle and against Gemstone's
isolated ICD directory.

```console
sbt testOpenCLConformance
GEMSTONE_OPENCL_FILTER='^(Basic|API)$' sbt testOpenCLConformance
GEMSTONE_OPENCL_SHARD=0/8 GEMSTONE_OPENCL_JOBS=8 sbt testOpenCLConformance
```

Set `GEMSTONE_OPENCL_ICD` to a directory containing only Gemstone's `.icd` file. The provider must authenticate with
the execution server; missing provider discovery, silent vendor fallback, or a process that never uses Gemstone fails
loudly. `GEMSTONE_OPENCL_LOADER` may override the stock loader used at compile/link time.
