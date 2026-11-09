# Device presets

Status: proposed, not started. It follows the per-target memory values in `Architecture`, and it stays separate from the cost model.

## Goal

Let a run report a named card, for example `--device "RTX 4060 Ti"`, so software that sizes its work or picks code paths from device properties sees that card. Today each runtime reports one fixed device per target: `native/cuda/src/runtime.cpp` hard-codes `device_limits::MultiprocessorCount` and `ClockRateKhz`, and HIP takes them from `deviceModel()`.

A preset changes what the device reports. It does not change costs, latency, or rates, which stay per target and per multiprocessor.

## What a preset sets

- Card name and the target it selects.
- Multiprocessor or compute unit count.
- Total memory and allocation limits.
- Core and memory clocks.
- Bus width and L2 size, overriding the target's reference card values.

## Steps

### 1. Table

- A small curated table in `core`, next to `Architecture`, keyed by card name, with one source per card.
- A card names its target, and a test requires that target to exist.
- Every row is checked against its source row, including the columns that a neighbouring row shares through `rowspan`. Spec lists are easy to misread: the first pass at the reference card table gave the Quadro GV100 the Titan V's L2 size.

### 2. Reporting

- Generate the table into native code as `gemstone_target_memory.hpp` does, so that one source feeds Scala and C++.
- CUDA, HIP, and OpenCL report the selected card's values, and an unknown card name is an error at startup.
- Launch capacity must follow the reported multiprocessor count, because libraries size grids from it. Check the effect on `Pacing` residency and on run time for large counts.

### 3. Selection

- Add `--device` to `gemstone run`, passed as an environment key alongside `GEMSTONE_TARGET_ARCH`. Choosing a preset also chooses its target, and naming a conflicting target is an error.

## Non-goals

- Modelling every shipping card. The table holds the cards someone needs.
- Per-card performance, including DRAM bandwidth limits and FP64 product-line limits.

## Open decisions

- Whether `--device` also reports PCI identifiers and driver strings. Recommendation: name and capacity first, and identifiers only if a real program needs them.
