# Gemstone

Gemstone runs GPU programs on a checked CPU emulator for GPUless testing and debugging.
It supports implemented subsets of PTX, AMDGPU and SPIR-V through native CUDA Driver,
HIP, HSA, OpenCL and Level Zero facades. SYCL uses OpenCL or Level Zero.
Unsupported operations return errors. Emulation never silently uses a physical GPU.

## Build and test

Requires JDK 25, sbt 2, CMake/Ninja, a C/C++ compiler and Python 3. Vendor workload builds
use pinned SDK containers; native boundary tests do not require a GPU.

```console
sbt --server --batch compile
sbt --server --batch testFull
sbt --server --batch nativeIntegration/testFull
sbt --server --batch integration/testFull
sbt --server --batch serverNative/nativeLink
```

Ordinary compile/test tasks do not build the native facades. Integration builds them
explicitly. `test` is incremental; `testFull` reruns the selected suites.
`sbt clean` removes repository build outputs, not external download/container caches.

## Guides

- [Build lifecycle and CI](docs/BUILD.md)
- [Runtime boundaries and protocol](docs/RUNTIME.md)
- [Capture and replay](docs/REPLAY.md)
- [ISA execution](docs/ISA_EXECUTION.md)
- [Conformance and host profiles](conformance/README.md)
- [Pinned generator inputs](codegen/ISA_REFERENCES.md)
- [Plan and deferred features](GEMSTONE_PLAN.md)
- [LUNA action ledger](LUNA_REVIEW_1.md)

The local endpoint supports JVM and Scala Native. Remote execution, physical-GPU
forwarding and fault-driven UVM remain deferred. Native Linux qualification does not
establish Windows/macOS runtime support.
