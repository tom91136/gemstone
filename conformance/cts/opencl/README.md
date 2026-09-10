# OpenCL CTS artifact

This recipe builds unmodified [Khronos OpenCL CTS](https://github.com/KhronosGroup/OpenCL-CTS/tree/v2026-07-09-00) tag `v2026-07-09-00`, commit `726a6d94da0876cf69bf6198d20d42197b0392de`. `pins.json` records immutable source archive hashes and the matching OpenCL/SPIR-V header snapshots. Original archives are verified before extraction into a fresh container workspace. Any future upstream changes must be supplied as checked-in `patches/*.patch`; the artifact records their hashes. There are currently no patches.

Build an absent or empty output directory with:

```sh
conformance/cts/opencl/container-build.sh target/cts/opencl/artifact
```

The recipe uses the existing fixture lock/measurement wrapper, container-common image lifecycle, and runtime-bundle policy. The existing Docker/Podman selection and image overrides apply. Its small derived container layer adds SPIR-V Tools because upstream `test_compiler` assembles input binaries during the build. Tool versions, base and builder image identities, recipe hashes, licenses, and source provenance accompany the artifact.

The initial profile builds the complete `api`, `basic`, `compiler`, `computeinfo`, and `events` executables: 379 cases at this pin. It is a partial CTS inventory, not an OpenCL conformance claim. Exactly ten reviewed cases are default smoke tests; every other compiled case remains available through explicit filtering. `--list` obtains actual executable registrations without a device; source registrations supply exact minimum OpenCL versions. Upstream feature/version checks and result statuses remain authoritative. An empty `capabilities` array means that no additional prefilter is imposed, not that all optional features are supported.

`cases.jsonl` contains one exact upstream test selector per entry. Relative executable paths are rooted at the artifact; `{artifact}` in compiler resource arguments must be expanded by the catalog. The runner sets `CL_CONFORMANCE_RESULTS_FILENAME={report_directory}/opencl-cts.json`, with a unique directory for each case and role. A valid verdict requires exactly the requested `test_name` in upstream JSON `results`, with value `pass`, `fail`, or `skip`. Initialization-wide or multi-test results are failures. Explicit upstream skips map to exit 77; a successful process alone is not a pass. Native stdout/stderr are retained.

Reviewed header/property-only cases carry `execution: host` and source evidence; this is not a category-wide exemption. These tests can still enumerate an OpenCL device during harness startup. Kernel, compiler, memory, and event cases retain device-provider attestation. The smoke covers real source compilation and integer execution, constant-source handling, binary retrieval/reload, separate compile/link, and events, plus host-only checks and explicit optional-feature/version skips.

The CTS executables require an OpenCL ICD and physical driver for oracle execution. The bundled loader is Ubuntu's ocl-icd: use an isolated `OCL_ICD_VENDORS` directory containing the chosen provider's `.icd` file. `OCL_ICD_FILENAMES` alone is not sufficient for this loader. Compiler include directories and SPIR-V fixtures are bundled next to `test_compiler`.

Gemstone's online source path additionally requires the existing matching `icpx`, `llvm-spirv`, and `llvm-link` SDK tools. Current defaults point to Intel oneAPI 2026.1; the existing `GEMSTONE_OPENCL_COMPILER`, `GEMSTONE_OPENCL_LLVM_SPIRV`, and `GEMSTONE_OPENCL_LLVM_LINK` overrides apply. The CTS artifact does not bundle this SDK or replace compilation with prerecorded output. Source, binary reload, and compile/link smoke tests have been run through this real path.

Not built in this initial profile: the remaining upstream categories, including image/interop, atomics, subgroups, SPIR-V-specific, conversion, and exhaustive math executables. The exported non-default cases have not all been qualified. Missing device UUID support and insufficient OpenCL version are explicit upstream skips in the initial smoke, not fabricated passes.
