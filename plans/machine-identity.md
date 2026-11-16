# Machine identity

Status: steps 1 to 6 are done. Device presets hold multiprocessor count, boost clock, bus width, and L2 size per card, and CUDA, HIP, HSA, OpenCL, Level Zero, NVML, ROCm SMI, and AMD SMI report the selected preset's identity and capacity, Vulkan reports its name and memory, and all of them derive UUID, the 64-bit unique ID that HSA, ROCm SMI, and AMD SMI share, and PCI domain, bus, device, and function from the device index in `gemstone_device_presets.hpp`. Launch residency scales with the reported multiprocessor count (`multiprocessors * 32`). Native ctest cases in `native/test/tools.cpp` verify rocminfo, clinfo, sycl-ls, and vulkaninfo against preset values; `sycl-ls` comes from the pinned oneAPI toolkit in the native toolchain image. NVML provides `libnvidia-ml.so.1` with device identity, memory, PCI info, clocks, and empty process lists, verified against CUDA for every NVIDIA preset and by `nvidia-smi`, `nvidia-smi -q`, `nvidia-smi --query-gpu=... --format=csv`, and `nvtop`, which runs headless and is checked for the device name, clocks, and memory. NVML reports CUDA cores as multiprocessors times the cores per multiprocessor of the preset's compute capability. ROCm SMI and AMD SMI provide `librocm_smi64.so.1` and `libamd_smi.so` with the same identity and capacity and empty process lists, verified against HIP for every AMD preset and by `rocm-smi` and `amd-smi`. Level Zero Sysman reports the preset device properties, PCI address, one memory module, compute and copy engine groups, and empty process lists, verified by `xpu-smi` and `sycl-ls --verbose`. Device control returns not-supported. It follows `device-presets.md`, whose remaining items it takes over, and parts of `ecosystem-conformance.md` need it.

## Goal

Make the management libraries and tools that sit next to the compute runtimes work under gemstone: `nvidia-smi` and `nvtop` through NVML, `rocm-smi` and `amd-smi` through their AMD libraries, and `xpu-smi` through Level Zero Sysman. Every runtime and tool reports one consistent device: the same name, memory, UUID, and PCI bus address, taken from the selected preset.

Gemstone stays at its layer. It provides the libraries the vendor driver provides, and the vendor's tools and packages run unchanged on top.

## Current state

- Presets hold a name, a target, multiprocessor count, boost clock, memory data rate, bus width, L2 size, and total memory for six cards. `GEMSTONE_DEVICE` or `gemstone run --device` selects one, and an unknown name is an error.
- CUDA, HIP, HSA, OpenCL, Level Zero, NVML, ROCm SMI, and AMD SMI report the preset's name, memory, and capacity. Vulkan reports its name and memory only. Every runtime derives UUID and PCI domain, bus, device, and function from the device index in `gemstone_device_presets.hpp`.
- Launch residency scales with the reported multiprocessor count.
- NVML, ROCm SMI, and AMD SMI are implemented. Level Zero implements its Sysman (`zes`) identity, memory, engine, PCI, and process queries; sensor and control families return not-supported.
- `rocminfo`, `clinfo`, `sycl-ls`, `vulkaninfo`, `nvidia-smi`, `nvtop`, `rocm-smi`, and `amd-smi` have native ctest cases in `native/test/tools.cpp`, not conformance cases.

## Design

### Values

- Identity: name, UUID, PCI domain, bus, device, and function, and driver and runtime version strings. The UUID and bus address derive from the device index, so every library computes the same value without sharing state.
- Capacity: total memory, multiprocessor count, clocks, bus width, and L2, from the preset.
- Use: memory used and utilisation, from what the emulator is doing.
- Sensors: temperature, fan, and power return the API's not-supported result, which the tools print as `N/A`. Inventing sensor values would mislead.

### Libraries

- **NVML:** `libnvidia-ml.so.1`, built like `libcuda.so.1`. It covers initialisation, device handles by index, UUID, and bus address, identity and capacity queries, memory and utilisation, compute mode, and process lists.
- **AMD:** `rocm-smi` and `amd-smi` are Python front ends over `librocm_smi64.so` and `libamd_smi.so`, which read sysfs and the DRM and KFD devices. Gemstone already replaces the HSA runtime rather than emulating `/dev/kfd`, so the same choice applies: provide the two libraries.
- **Intel:** Sysman in gemstone's Level Zero driver, which `xpu-smi` and `sycl-ls --verbose` reach through the loader.

## Steps

### 1. Preset completion (done)

- Added multiprocessor count, boost clock, bus width, and L2 per card in `DevicePreset`, checked in `DevicePresetSuite` against the card in `Architecture` for the preset's target. That table is not checked against a source by a test. The values match the Wikipedia GeForce 900, 10, and 20 series and Radeon RX 9000 series tables. The RTX 5080 and Quadro GV100 values rest on secondary sources only. The 9070 XT data rate of 20100 MT/s is not a published figure (the specification is 20000).
- CUDA, HIP, HSA, OpenCL, and Level Zero report the preset's name, memory, multiprocessor count, clocks, bus width, and L2, as each API exposes them. Vulkan reports the name and memory only.
- Launch capacity follows the reported multiprocessor count (`multiprocessors * 32`), and calling-thread `Pacing` slot allocation grows with it. `WorkerPoolSuite` checks residency of 128, 512, and 2688 for 4, 16, and 84 multiprocessors. No launch-time measurement is recorded.
- Derived UUID and PCI domain, bus, device, and function from the device index in `gemstone_device_presets.hpp`, used by every runtime and checked across runtimes in `gemstone_preset_identity_test`.

### 2. Discovery tools (done)

- Native ctest cases for `rocminfo`, `clinfo`, `sycl-ls`, and `vulkaninfo`. These need no new library, only correct reporting.
- Expected values come from the preset table. Each case fixes one preset (`Radeon RX 9070 XT`, or `GeForce GTX 1080` for the NVIDIA tools) and writes the tool's output format into the case.

### 3. NVML (done)

- Generated declarations from NVML header in `gemstone_nvml_api.h` and implemented `libnvidia-ml.so.1`.
- Implemented device handles by index, UUID, and PCI bus ID, memory info v1/v2, PCI info v2/v3/Ext, clocks, compute capability, empty process lists, event sets, and internal export table query.
- Native ctest cases for `nvidia-smi`, `nvidia-smi -q`, `nvidia-smi --query-gpu=... --format=csv`, and `nvtop` against preset values. `nvtop` 3.0.2 has no non-interactive mode, so its case runs it for 3 seconds with output to a pipe and checks the drawn device name, clocks, and memory.
- `nvmlDeviceGetNumGpuCores` returns multiprocessors times the cores per multiprocessor of the preset's compute capability. The identity test compares it with the published core count of each NVIDIA card. A real RTX 4060 Ti (sm_89, 34 multiprocessors) reports 4352 through the NVIDIA driver's NVML.
- Cross-runtime check in `gemstone_preset_identity_test` verifying NVML and CUDA report identical name, UUID, bus address, and memory for every NVIDIA preset.

### 4. ROCm SMI and AMD SMI (done)

- Generated declarations from the pinned ROCm headers in `gemstone_rocm_smi_api.h` and `gemstone_amd_smi_api.h` and implemented `librocm_smi64.so.1` and `libamd_smi.so`.
- Implemented device enumeration, name and market name, the shared 64-bit unique ID (the HSA `GPU-<hex>` UUID, the ROCm SMI unique ID, and the AMD SMI ASIC serial), the full UUID, PCI bus ID, VRAM total and usage, clocks, and empty process lists.
- Native ctest cases for `rocm-smi` and `amd-smi` against preset values.
- Cross-runtime checks in `gemstone_preset_identity_test` verifying HSA, ROCm SMI, AMD SMI, and HIP report identical identity and capacity for every AMD preset. CUDA rejects AMD presets and HIP rejects NVIDIA presets, so the reference runtime is CUDA for NVIDIA presets and HIP for AMD presets.
- Clock, power, fan, and reset calls return not-supported.

### 5. Level Zero Sysman (done)

- Implemented `zes` driver and device enumeration, device properties, state and process lists, PCI properties and state, one memory module with capacity, usage, and bandwidth, compute and copy engine groups with zero activity, and empty fan, power, temperature, frequency, firmware, RAS, scheduler, standby, fabric, and ECC families.
- Native ctest case for `xpu-smi discovery` against preset values; `sycl-ls --verbose` reports the same name and UUID through the loader.

### 6. AMD driver state (done)

Both tools check the amdgpu module before they call a library. With no amdgpu module on the host (a CI runner, a machine without an AMD GPU) they stop at that check and list nothing: `rocm-smi` prints `Driver not initialized (amdgpu not found in modules)` and exits 0, and `amd-smi` enumerates no device.

`libhsa-runtime64.so.1`, which `gemstone run` preloads by default, exports a path-limited hook of the libc calls the tools' Python makes: `open`, `open64`, `openat`, `openat64`, `stat`, `stat64`, `lstat`, `lstat64`, `fstatat`, `fstatat64`, `access`, and `faccessat`. It answers two paths and passes every other call to the next definition (`dlsym(RTLD_NEXT)`):

- `/sys/module/amdgpu`: a directory (`stat`, `access`).
- `/sys/module/amdgpu/initstate`: a read-only file whose content is `live` (`stat`, `access`, and `open`, which returns an anonymous `memfd` holding the text). Write access fails with `EACCES`.

The hook answers only when `GEMSTONE_DEVICE` selects a preset whose target starts with `gfx`, checked on each call that matches one of the two paths, so unrelated calls cost two string comparisons. It keeps no state, so it is safe in threads, and the sanitizer runtimes sit in front of it in `LD_PRELOAD` and reach it through their own `RTLD_NEXT` chain. It answers whether or not the host has the module, so results do not depend on the host. A tool must have the library in its `LD_PRELOAD`, as `gemstone run` arranges; the tool cases in `native/test/tools.cpp` add it to `ToolEnvironment`. A process that loads the library with `dlopen` does not interpose, which only matters for tools that are not preloaded.

Not hooked, because the traces show no need: `/sys/module/amd_hsmp` (`amd-smi` treats it as not live and carries on), `/sys/class/kfd`, `/sys/class/drm` (`rocm-smi` lists the directory only for its low-power warning), `/dev/kfd`, and `/dev/dri`. `fopen` and `statx`, which the tools' Python does not call, and the legacy `__xstat` family, which programs built against glibc older than 2.33 call instead of `stat`, are not hooked either.

Evidence is from a masked container, since the development host has amdgpu loaded. `podman run --security-opt mask=/sys/module` leaves `/sys/module` empty. Masking only `/sys/module/amdgpu` is not enough: the empty directory makes `rocm-smi` treat the module as built into the kernel (`rocm_smi.py` `driverInitialized()`). Traces with `strace -f -e trace=%file,%stat`:

```
rocm-smi, /sys/module masked:
  newfstatat(AT_FDCWD, "/sys/module/amdgpu", ...) = -1 ENOENT
amd-smi list, /sys/module masked:
  newfstatat(AT_FDCWD, "/sys/module/amdgpu/initstate", ...) = -1 ENOENT
  newfstatat(AT_FDCWD, "/sys/module/amd_hsmp/initstate", ...) = -1 ENOENT
amd-smi list, module state present, /sys/class/kfd and /sys/class/drm masked, no /dev/kfd:
  openat(AT_FDCWD, "/sys/module/amdgpu/initstate", O_RDONLY|O_CLOEXEC) = 3
  openat(AT_FDCWD, "/dev/dri", ...) = -1 ENOENT
  newfstatat(AT_FDCWD, "/dev/kfd", ...) = -1 ENOENT
```

With the hook the five AMD tool cases pass in that container, and `native/hsa/test/amdgpu_state_test.cpp` covers the hook directly.

## Checks

- Test first, seen failing, for behaviour changes.
- Every library reports the same identity for the same device index, checked across libraries.
- Failures stay red.

## Risks to confirm

- Whether `nvidia-smi` uses only NVML or also opens `/dev/nvidiactl` or reads `/proc/driver/nvidia`. Confirmed by strace: `nvidia-smi` runs purely against `libnvidia-ml.so.1` without opening `/dev/nvidiactl` or `/proc/driver/nvidia`. It queries internal export table symbol `nvmlInternalGetExportTable` and MIG device count; returning success allows the tool to run unmodified.
- Whether `rocm-smi` and `amd-smi` load their libraries through a path the conformance package can redirect, or need sysfs as well. Confirmed by strace: `rocm-smi` loads `librocm_smi64.so.1` through `ROCM_SMI_LIB_PATH` and reads `/sys/class/drm/*/power/runtime_status` only for its low-power warning; identity and capacity come from the library. `amd-smi` loads `libamd_smi.so` from its package directory and probes `/dev/dri`, `/dev/kfd`, and `/sys/module/amdgpu/initstate` while enumerating, then falls back to the library's processor handles. Both tools report the preset with no DRM or KFD nodes present, so neither needs sysfs emulation beyond the driver state described below.
- Whether `xpu-smi` needs Intel components beyond Level Zero. Confirmed it does not: the pinned `xpu-smi` package needs the Level Zero loader, `libhwloc`, `libpciaccess`, and `libudev`, all of which the build provides. It runs against the gemstone driver without the Intel kernel driver, the `xpumd` daemon, or the optional `/usr/share/xpum` resources for `discovery`; it prints a DMI permission warning and reports sensors as N/A.
- Licence terms for taking `nvidia-smi` from the driver utilities package into a conformance image. Resolved by not redistributing it: the build downloads the pinned, hashed package from NVIDIA's repository into the build tree, and the conformance package does not contain the tool.

## Non-goals

- Device control: clock, power, and fan settings, resets, and MIG partitions. These return not-supported or not-permitted.
- DCGM and other daemon-based monitoring.
- Sensor models.

## Open decisions

- How a tool in one process sees memory used by a gemstone session in another. Recommendation: each session publishes its use in a small file under `$XDG_RUNTIME_DIR`, and the management libraries read it. The first version reports only identity and capacity, with zero use and no processes.
- Whether the AMD libraries are replaced or their sysfs inputs emulated. Decided: replace the libraries, matching how the HSA runtime is handled, and answer only the two driver-state paths the tools read themselves (step 6).
