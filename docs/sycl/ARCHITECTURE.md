# SYCL, Level Zero, and OpenCL boundaries

Gemstone keeps the stock SYCL host stack and implements both standard device boundaries:

```text
oneDPL -> DPC++ -> Unified Runtime -> Level Zero loader
       -> libze_gemstone.so -> typed execution RPC
       -> SPIR-V decoder and interpreter

oneDPL -> DPC++ -> Unified Runtime -> OpenCL ICD loader
       -> Gemstone ICD -> typed execution RPC
       -> SPIR-V decoder and interpreter
```

Each `gemstone run --api sycl` invocation must select exactly one backend. Level Zero uses the loader's
alternate-driver mechanism; OpenCL uses an isolated ICD directory. `ONEAPI_DEVICE_SELECTOR` selects only that
Gemstone GPU. Both providers advertise the literal device name `Gemstone`. Missing execution
features return `ZE_RESULT_ERROR_UNSUPPORTED_FEATURE`; they do not silently
complete a launch or fall back to hardware.

Native allocations are mirrored into checked guest memory. Kernel arguments
carry scalar bytes or allocation identity plus offset. Aggregates containing
pointers require explicit pointer patches derived from the SPIR-V parameter
type; native addresses are classification input only and are never trusted or
dereferenced by the managed executor.

## Invariants

- Module creation sends the exact SPIR-V bytes.
- Commands retain queue and event dependency order.
- Dirty device writes are copied back before completion is signalled.
- Test processes have disjoint allocation namespaces.
- Unsupported opcodes, argument layouts, and pointer provenance fail closed.

Focused Level Zero CTest cases cover loader discovery, transfers, lifecycle, and execution RPC. The identical 902
oneDPL binaries plus three samples are each run through both backend paths; OpenCL cases are paired with the ICD
facade where the selected oneDPL workload supports it. The exhaustive upstream inventory is described in
`conformance/onedpl/README.md`.
