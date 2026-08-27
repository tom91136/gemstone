# SPIR-V 1.6

This module models the universal SPIR-V 1.6 binary format in package
`net.kurobako.gemstone.isa.spirv.v1_6`. Its checked-in `InstructionDef` and `Instruction` enums, `Instructions` vector,
and `Version` are generated from the pinned Khronos JSON grammar. Each singleton definition is the sole owner of its
opcode name, numeric opcode, decoder, and metadata. Each parameterized instruction points back to that definition and
exposes its useful fields, common operands, opcode, decoder, and metadata directly.

The binary and typed layers are deliberately separate. `Binary` losslessly parses and serializes `RawModule` values,
including unknown opcode numbers. Descriptor-driven decoding admits a raw instruction into the typed `Instruction`
enum only when exactly one definition accepts its opcode and grammar-shaped operands. Missing, malformed, or ambiguous
forms are decode errors and cannot inhabit the typed AST. `ComputeValidator` then enforces the initial OpenCL Compute
profile: Kernel capability and entry point, physical addressing, an OpenCL memory model, and a supported module version.

The handwritten executor lowers into an ISA-specific immutable program and consumes the shared execution substrate.
It initially implements 32-bit constants, copies, integer/bitwise/comparison operations, selection, structured label
jumps, variables, load/store, and return. Effects are staged and execution is bounded and cancellable. Floating-point,
vectors, access chains, atomics, barriers, calls, multiple invocations, and the full OpenCL memory model remain explicit
unsupported traps.

Regenerate checked-in sources explicitly:

```console
sbt 'isa_spirv / generateSPIRVSources; isa_spirv / generateSPIRVReferenceTests'
```
