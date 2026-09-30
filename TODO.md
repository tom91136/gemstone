# CL/SYCL capability gaps

Prioritize the first three before images: they improve compute coverage and have existing interpreter machinery to build on. Confirm each capability with tests before advertising support.

| Priority | Gap | Current state / value |
|---|---|---|
| 1 | Non-uniform work-groups | OpenCL advertises unsupported. The non-uniform CTS cases fall back to uniform groups. Exercise partial groups to catch indexing and barrier bugs. |
| 2 | Work-group collectives | OpenCL advertises unsupported, although SPIR-V implements several reductions and scans. Complete integration and validation. |
| 3 | Generic address space | OpenCL advertises unsupported. Functions accepting pointers to different address spaces need this; some interpreter machinery exists. |
| 4 | Images and samplers | Missing across the relevant backends. Implement formats, addressing, filtering, transfers, and kernel operations. |
| 5 | Ordinary host-pointer access through OpenCL | Allocated SVM/USM exists, but fine-grained system SVM and Intel shared-system-memory capabilities are disabled. Complete these API paths. |

## Capability audit

- Correct the hard-coded `CL_DEVICE_LATEST_CONFORMANCE_VERSION_PASSED` claim: passing the declared CTS version has not been established.
- Check that advertised capabilities and capability-dependent skips match implemented behavior.
- Defer pipes and device-side enqueue until an application needs them.
