# Machine identity

Status: steps 1, 2, and 3 are done. Device presets hold multiprocessor count, boost clock, bus width, and L2 size per card, and CUDA, HIP, HSA, OpenCL, Level Zero, Vulkan, and NVML report the selected preset's identity and capacity and derive UUID and PCI domain, bus, device, and function from the device index in `gemstone_device_presets.hpp`. Launch residency scales with the reported multiprocessor count (`multiprocessors * 32`). Discovery tool cases verify rocminfo, clinfo, sycl-ls, and vulkaninfo against preset values. NVML provides `libnvidia-ml.so.1` with device identity, memory, PCI info, clocks, and empty process lists, verified against CUDA and by `nvidia-smi`, `nvidia-smi -q`, `nvidia-smi --query-gpu=... --format=csv`, and `nvtop`. Steps 4 and 5 are not started. It follows `device-presets.md`, whose remaining items it takes over, and parts of `ecosystem-conformance.md` need it.

## Goal

Make the management libraries and tools that sit next to the compute runtimes work under gemstone: `nvidia-smi` and `nvtop` through NVML, `rocm-smi` and `amd-smi` through their AMD libraries, and `xpu-smi` through Level Zero Sysman. Every runtime and tool reports one consistent device: the same name, memory, UUID, and PCI bus address, taken from the selected preset.

Gemstone stays at its layer. It provides the libraries the vendor driver provides, and the vendor's tools and packages run unchanged on top.

## Current state

- Presets hold a name, a target, multiprocessor count, boost clock, memory data rate, bus width, L2 size, and total memory for six cards. `GEMSTONE_DEVICE` or `gemstone run --device` selects one, and an unknown name is an error.
- CUDA, HIP, HSA, OpenCL, Level Zero, and Vulkan report the preset's name, memory, and capacity, and derive UUID and PCI domain, bus, device, and function from the device index in `gemstone_device_presets.hpp`.
- Launch residency scales with the reported multiprocessor count.
- There is no NVML, ROCm SMI, or AMD SMI library. Level Zero has generated Sysman (`zes`) declarations and no implementation.
- `rocminfo`, `clinfo`, `sycl-ls`, and `vulkaninfo` are not conformance cases, except for a Vulkan information workload.

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

- Added multiprocessor count, boost clock, bus width, and L2 per card in `DevicePreset`, each checked against its source row in `references/nvidia/gpus.md`, `references/amdgpu/gpus.md`, and the whitepapers.
- CUDA, HIP, HSA, OpenCL, Level Zero, and Vulkan report the preset's name, memory, multiprocessor count, clocks, bus width, and L2.
- Launch capacity follows the reported multiprocessor count (`multiprocessors * 32`). On `ptx-calls` with 2688 groups, scaling from 4 multiprocessors (residency 128) to 64, 80, and 84 multiprocessors (residency 2048, 2560, and 2688) changes median launch time from 47.0 ms to 104.6 ms, 115.8 ms, and 70.0 ms, and calling-thread `Pacing` slot allocation from 42.4 KB to 572.4 KB, 713.7 KB, and 749.0 KB per launch.
- Derived UUID and PCI domain, bus, device, and function from the device index in `gemstone_device_presets.hpp`, used by every runtime and checked across runtimes in `gemstone_preset_identity_test`.

### 2. Discovery tools (done)

- Conformance cases for `rocminfo`, `clinfo`, `sycl-ls`, and `vulkaninfo`. These need no new library, only correct reporting.
- Expected values come from the preset table, not from text written into each case.

### 3. NVML (done)

- Generated declarations from NVML header in `gemstone_nvml_api.h` and implemented `libnvidia-ml.so.1`.
- Implemented device handles by index, UUID, and PCI bus ID, memory info v1/v2, PCI info v2/v3/Ext, clocks, compute capability, empty process lists, event sets, and internal export table query.
- Conformance cases for `nvidia-smi`, `nvidia-smi -q`, `nvidia-smi --query-gpu=... --format=csv`, and `nvtop` against preset values.
- Cross-runtime check in `gemstone_preset_identity_test` verifying NVML and CUDA report identical name, UUID, bus address, and memory across all presets.

### 4. ROCm SMI and AMD SMI

- The same for `librocm_smi64.so` and `libamd_smi.so`, with `rocm-smi` and `amd-smi` from the pinned ROCm release.

### 5. Level Zero Sysman

- Implement the `zes` queries for identity, memory, and engines, with `xpu-smi` and `sycl-ls --verbose` as cases.

## Checks

- Test first, seen failing, for behaviour changes.
- Every library reports the same identity for the same device index, checked across libraries.
- Failures stay red.

## Risks to confirm

- Whether `nvidia-smi` uses only NVML or also opens `/dev/nvidiactl` or reads `/proc/driver/nvidia`. Confirmed by strace: `nvidia-smi` runs purely against `libnvidia-ml.so.1` without opening `/dev/nvidiactl` or `/proc/driver/nvidia`. It queries internal export table symbol `nvmlInternalGetExportTable` and MIG device count; returning success allows the tool to run unmodified.
- Whether `rocm-smi` and `amd-smi` load their libraries through a path the conformance package can redirect, or need sysfs as well.
- Whether `xpu-smi` needs Intel components beyond Level Zero.
- Licence terms for taking `nvidia-smi` from the driver utilities package into a conformance image.

## Non-goals

- Device control: clock, power, and fan settings, resets, and MIG partitions. These return not-supported or not-permitted.
- DCGM and other daemon-based monitoring.
- Sensor models.

## Open decisions

- How a tool in one process sees memory used by a gemstone session in another. Recommendation: each session publishes its use in a small file under `$XDG_RUNTIME_DIR`, and the management libraries read it. The first version reports only identity and capacity, with zero use and no processes.
- Whether the AMD libraries are replaced or their sysfs inputs emulated. Recommendation: replace the libraries, matching how the HSA runtime is handled, and leave sysfs emulation out unless a tool reads sysfs directly.
