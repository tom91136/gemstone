# Native toolchain

Status: in progress. Steps 1 and 2 are done locally.

## Goal

Ship native runtime DSOs that need nothing beyond a glibc at or above a stated floor, and lint them with the newest LLVM. Local and CI results must agree whatever distribution the host runs. The conformance package uses the same sysroot where vendor libraries allow it.

## Current state

- A local build of `libcuda.so.1`, `libamdhip64.so.7`, and the other DSOs needs `libstdc++.so.6` and `libgcc_s.so.1`, and reaches `GLIBC_2.34`. `native/CMakeLists.txt` sets hidden visibility but no static runtime.
- CI's `build`, `test`, `ctest`, and `lint` jobs run on the `ubuntu-24.04` host with LLVM tools unpacked from the release tarball. Only conformance builds and per-backend conformance lint run in containers.
- `conformance_clang_release` (22.1.8) sets the conformance compiler, `clang-tidy`, and the artifact cache keys. `conformance_clang_format_release` (23.1.5) sets `clang-format` for CI and the pre-commit hook.
- The Fedora 45 host has LLVM 23.1.5. Its `clang-tidy` reports findings CI never sees, and 32 OpenCL `ctest` cases fail because the DPC++ reader rejects LLVM 23 modules.
- The conformance package is already relocatable: `Assemble.cmake` sets `$ORIGIN` run paths, and `Verify.cmake` runs a moved copy. It has no glibc floor.

## Design

The compiler version does not set the glibc floor. The sysroot does. The C++ runtime is what ties a DSO to a recent system, so the design separates three things.

### Pins

- **Sysroot image digest:** the glibc floor of what ships. It changes rarely and deliberately.
- **Tools LLVM:** native compile, static libc++, `clang-format`, `clang-tidy`, and `clangd`. It changes freely, to get newer diagnostics. This widens `conformance_clang_format_release` into `conformance_clang_tools_release`.
- **Conformance compilers:** `conformance_clang_release`, ROCm, and oneAPI, which are what the suite tests. They change separately, because a change rebuilds artifacts.

### Sysroot

- AlmaLinux 8, glibc 2.28, from the recipe in `~/polyregion/sysroot/Dockerfile` (`just build-sysroot`).
- The recipe is copied into gemstone, because CI has no `~/polyregion`, and its base image is pinned by digest. Polyregion pulls an unpinned `almalinux:8`.
- `gcc-toolset-15` replaces `gcc-toolset-12`, because C++23 sources need it and AlmaLinux 8 provides it. Native DSOs do not use it. Conformance programs do.
- The sysroot is a directory inside the toolchain image, not the image's own system. LLVM's release binaries need `GLIBC_2.34` and cannot run on it, so the image is based on a current distribution and compiles with `--sysroot`.

### Native DSOs

- Compile with tools LLVM and `--sysroot`.
- Link libc++, libc++abi, and libunwind statically, built as PIC with `LIBCXX_HERMETIC_STATIC_LIBRARY=ON`, plus `-nostdinc++`, `-nostdlib++`, `-static-libgcc`, `-Bsymbolic`, and `--exclude-libs,ALL`. `-nostdinc++` is required: without it the driver also searches the libc++ headers in the LLVM tarball. A version script exports only the API.
- A probe built this way needs only glibc libraries, reaches `GLIBC_2.16`, exports only its API symbol, and loaded and threw a C++ exception on plain AlmaLinux 8.
- Polyregion links `-static-libstdc++` instead, and records in `native/CMakeLists.txt` that a static libstdc++ next to AMD's libraries splits the locale registry and crashes AMD. libc++ has its own namespace and state, so it cannot share that registry. The OpenCL ICD can load a gemstone DSO next to a vendor one, so this applies here.

### Lint and editor

- `clang-tidy` and `clangd` read the compile database of the native build, so findings reflect the headers that ship.
- The repository mounts at the same absolute path in the image, so `compile_commands.json` also works from a host editor.

### Conformance package

- Programs link libstdc++ dynamically, because a process must hold one copy once vendor libraries are present. The toolset's `libstdc++_nonshared` is expected to keep the requirement at the sysroot's base `libstdc++.so.6`.
- Each route records its highest `GLIBC_` and `GLIBCXX_` versions in `artifact.toml`, computed from its ELF files. Objects the suite compiles must stay within the sysroot. Vendor libraries set the rest, and the record reports it.

## Steps

### 1. Triage

- Lint: run `clang-tidy` 23 on the native sources, dedupe across translation units, and count by check. Decide fix or disable for each check.
- OpenCL: find the cause of `sycl/vendor_sycl_sample_fcorr_1d_usm/fourier-correlation-1d-usm`, red on main with OpenCL error -11 while building MKL's DFT kernel, and fix it on its own. Confirm that the 32 local OpenCL `ctest` failures all share the LLVM 23 and DPC++ reader mismatch.
- This step changes no toolchain.

### 2. Toolchain image

- Add the sysroot recipe and a toolchain image holding tools LLVM, the sysroot, and hermetic libc++.
- Add the tools pin. CI's `clang-format` and `clang-tidy` move to it, and the same commit disables `bugprone-signed-bitwise` and `readability-trailing-comma` and applies the 185 fixes for the other checks from step 1.

### 3. Native build in the image

- Build the native DSOs in the image, locally and in CI, through the existing container mechanism.
- `lint`, `build`, `test`, and `ctest` use it.
- OpenCL native tests take their compiler from the pinned conformance toolchain, which clears the 32 local failures.

### 4. Memory, integer, and shift coverage

- Run native `ctest` in one ASan and UBSan build (`-fsanitize=address,shift,signed-integer-overflow,bounds -fno-sanitize-recover=all`), in the image, as a CI job, so a report fails the test. Leak detection starts off, because the runtimes keep process-lifetime state on purpose. `~/polyregion/native/toolchains/linux-clang-amd64-asan.cmake` is the existing pattern.
- Count `bugprone-signed-char-misuse` findings and find why `.clang-tidy` disables it, then enable it and fix them unless the count shows it is noise.
- Add `-Wshift-sign-overflow` to the native flags. `clang-tidy` runs with `-w`, so compiler warnings come from the build.

### 5. Prove the floor

- Package check on every shipped DSO: each `DT_NEEDED` is a library the sysroot's glibc provides, and no symbol version is newer than that glibc. Both limits are read from the sysroot, not kept in a list.
- Run the native smoke tests in a plain AlmaLinux 8 image, so the floor is tested and not only inferred.

### 6. Conformance package

- Move one compiler family at a time: plain Clang, then `icpx` and `hipcc`, then `nvcc`.
- Add the per-route floor record, and keep `Verify.cmake`'s relocation test.
- A route that cannot use the sysroot stays as it is, and the record shows its floor.

### 7. Compiler bump

- Move `conformance_clang_release` separately, once the above is in place. It changes the compiler under test and rebuilds the Clang-route artifacts.

## Checks

- Test first, seen failing, for behaviour changes.
- A full native `ctest` and `native/lint`, in the image, must pass at each step.
- `conformance/emulate --prebuilt --workload xlong` on the affected routes at step 6.
- Failures stay red.

## Risks to confirm

- Whether C++ objects or exceptions cross between DSOs. `DT_NEEDED` shows no links between them, but a `dlopen` path has not been checked. A shared object would need one runtime, or the boundary changed.
- Whether the sources rely on libstdc++-specific behaviour. They target GCC 13 and Clang 19 or newer today, so glaze and the C++23 uses need a libc++ build to confirm.
- Whether `nvcc` works against the sysroot. It drives a host GCC through `-ccbin`.
- The cost of building natively in the image, including rootless podman, which has failed to write `/output` with CI's Docker settings.
- Whether ASan works with the hermetic static libc++. An uninstrumented libc++ can give false container-overflow reports, so the sanitiser job may need its own instrumented libc++, or the system's dynamic C++ runtime.

## Non-goals

- Architectures other than x86_64, although the polyregion recipe covers several.
- Raising the floor of vendor libraries.
- A static glibc.

## Decisions

- `clang-tidy` 23 reported 981 unique findings across nine checks. `bugprone-signed-bitwise` (417) and `readability-trailing-comma` (379) are disabled: the first flags shift amounts such as `size_t{8} << 20` and vendor API flags, and `clang-analyzer-core.BitwiseShift` already catches real shift errors. The other 185 are fixed.
- The pre-commit hook calls `clang-format` from the toolchain image, so a distribution update stops blocking commits.
- The floor check reports vendor routes and fails only for objects the suite compiles.
