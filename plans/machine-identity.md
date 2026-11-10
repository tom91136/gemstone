# Machine identity

Status: proposed, not started. It follows `device-presets.md`, whose remaining items it takes over, and parts of `ecosystem-conformance.md` need it.

## Goal

Make the management libraries and tools that sit next to the compute runtimes work under gemstone: `nvidia-smi` and `nvtop` through NVML, `rocm-smi` and `amd-smi` through their AMD libraries, and `xpu-smi` through Level Zero Sysman. Every runtime and tool reports one consistent device: the same name, memory, UUID, and PCI bus address, taken from the selected preset.

Gemstone stays at its layer. It provides the libraries the vendor driver provides, and the vendor's tools and packages run unchanged on top.

## Current state

- Presets hold a name, a target, total memory, and memory data rate for six cards. `GEMSTONE_DEVICE` or `gemstone run --device` selects one, and an unknown name is an error.
- CUDA, HIP, HSA, and OpenCL report the preset's name and memory, and allocation limits scale with it. Level Zero and Vulkan do not read the preset.
- Multiprocessor count, clocks, bus width, and L2 size come from the target's reference card, not from the preset.
- CUDA reports a fixed UUID and PCI bus, device, and domain numbers.
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

### 1. Preset completion

- Add multiprocessor count, clocks, bus width, and L2 per card, each checked against its source row as the existing columns are.
- Launch capacity follows the reported multiprocessor count. Check the effect on `Pacing` residency and run time for large counts.
- Level Zero and Vulkan report the preset.
- Derive UUID and bus address from the device index in one generated place, used by every runtime.

### 2. Discovery tools

- Conformance cases for `rocminfo`, `clinfo`, `sycl-ls`, and `vulkaninfo`. These need no new library, only correct reporting.
- Expected values come from the preset table, not from text written into each case.

### 3. NVML

- Generate the declarations from the NVML header, as for the other APIs, and implement the queries above.
- Cases: `nvidia-smi`, `nvidia-smi -q`, `nvidia-smi --query-gpu=... --format=csv`, and `nvtop`, with the tools taken from a pinned driver utilities package.
- A case checks that NVML and CUDA agree on name, UUID, bus address, and memory for every device index.

### 4. ROCm SMI and AMD SMI

- The same for `librocm_smi64.so` and `libamd_smi.so`, with `rocm-smi` and `amd-smi` from the pinned ROCm release.

### 5. Level Zero Sysman

- Implement the `zes` queries for identity, memory, and engines, with `xpu-smi` and `sycl-ls --verbose` as cases.

## Checks

- Test first, seen failing, for behaviour changes.
- Every library reports the same identity for the same device index, checked across libraries.
- Failures stay red.

## Risks to confirm

- Whether `nvidia-smi` uses only NVML or also opens `/dev/nvidiactl` or reads `/proc/driver/nvidia`. If it does, the tool needs more than the library.
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
