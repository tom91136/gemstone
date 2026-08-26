# SPIR-V 1.6

This module models the universal SPIR-V 1.6 binary format in package
`net.kurobako.gemstone.isa.spirv.v1_6`. Its checked-in catalogue is generated from the pinned Khronos JSON grammar.

`Binary` parses little- and big-endian modules and serializes them byte-for-byte, including unknown opcodes.
`ComputeValidator` separately enforces the initial OpenCL Compute profile: Kernel capability and entry point, physical
addressing, an OpenCL memory model, structural operand minima, and a supported module version. Unknown instructions
are `Unsupported`, never silently accepted.

The handwritten executor lowers into an ISA-specific immutable program and consumes the shared execution substrate.
It initially implements 32-bit constants, copies, integer/bitwise/comparison operations, selection, structured label
jumps, variables, load/store, and return. Effects are staged and execution is bounded and cancellable. Floating-point,
vectors, access chains, atomics, barriers, calls, multiple invocations, and the full OpenCL memory model remain explicit
unsupported traps.

Regenerate checked-in sources explicitly:

```console
sbt 'isa_spirv / generateSpirvSources; isa_spirv / generateSpirvReferenceTests'
```
