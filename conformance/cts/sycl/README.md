# SYCL CTS bring-up profile

This builds unmodified upstream Khronos SYCL-CTS `item` and `range` categories at the commit and submodule revisions in `pin.json`. It is an initial representative profile, **not full SYCL conformance**: full-conformance mode and OpenCL interoperability tests are disabled. OpenCL and Level Zero execution backends are both supported; disabling interoperability tests does not disable the OpenCL backend.

`container-build.sh <empty-output-directory>` uses the existing `container-common.sh` and `fixture-run.py` workflow, the existing pinned oneAPI image, CMake/Ninja, and an offline build container. The container follows `CMAKE_BUILD_PARALLEL_LEVEL` with 2 GiB per compiler job and joins the common build slice when configured. Source retrieval is pinned, and a modified source checkout is rejected. `GEMSTONE_SYCL_CTS_SOURCE` may point to an already populated exact checkout; other container/resource settings use the common conformance variables.

The relocatable artifact contains:

- `bin/test_item`, `bin/test_range`, and the vendor user-space library closure in `lib/`;
- `manifest.json`, compiler version and upstream/Catch2/OpenCL-Headers/oneAPI licences;
- raw upstream Catch2 3.2.1 XML listings and independently discovered `cases.jsonl`.

Each inventory row retains its raw `test_name` and escaped Catch2 command selector. The two explicitly host-only tests are range deduction guides and range copy-by-value semantics on host. The other nine cases execute device kernels, including mixed host/device sections. Capture JUnit stdout separately for each role/case. Catch2's `testsuite tests` counts assertions; one selected upstream test can also produce multiple section `testcase` elements. Validate their selected name/section prefix, not a fixed element count.

For device cases, the catalog should constrain the backend/device using the normal provider environment and use the host profile’s selected native device and append `--device Gemstone` for emulation. Upstream `--device` matches platform/device names as a regular expression; it is not a device-type selector. `--info-dump <unique-report-directory>/device.info` records the actual device selection. Host-only cases do not require device selection or emulator attestation.

Use the existing full-run server instruction budget of 2,000,000,000 for this profile. Upstream `range_api` legitimately exceeds the compact 100M default; do not reduce its work to fit that default. The original 120-second per-case timeout is retained.

Official sources: [pinned SYCL-CTS](https://github.com/KhronosGroup/SYCL-CTS/tree/5ea625931cdbe608bab67046356722270449c883), [upstream build/profile documentation](https://github.com/KhronosGroup/SYCL-CTS/blob/5ea625931cdbe608bab67046356722270449c883/README.md).
