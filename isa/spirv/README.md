# SPIR-V 1.6

The public package is `net.kurobako.gemstone.isa.spirv.v1_6`. Generated instruction definitions,
operand metadata and grammar revision come from the [pinned Khronos grammar](../../codegen/ISA_REFERENCES.md).

`Binary` preserves raw modules, including unknown opcode numbers. Typed decoding accepts only
unambiguous grammar-shaped instructions. `Validator` applies the supported OpenCL compute/library
profile; `Interpreter.prepare` and execution enforce the actual executable subset. Generated
catalog size is not an execution-coverage claim.

The interpreter owns type/layout metadata, invocation values, checked pointers, function control
flow and collective suspension/resumption. Supported operations include selected integer,
floating-point, vector, memory, call, atomic and collective forms. Unsupported operations and
layouts fail explicitly; this is not a complete OpenCL memory-model implementation.

## Provider contracts

SYCL uses exactly one OpenCL or Level Zero backend. Module creation retains exact SPIR-V bytes;
commands preserve queue/event dependencies and publish dirty writes before reporting completion.
Native addresses require validated allocation provenance and supported relocation/mapping rules;
the executor never dereferences an application's foreign process address.

Preserve `ByVal` parameter layout even when its first word contains a relocated pointer in opaque
byte storage. An embedded relocation is not the address of the aggregate itself. Pointer access,
array strides, packed structures and collective rules remain SPIR-V semantics.

Level Zero allocations must satisfy at least their reported page alignment, including requests
with smaller or zero alignment. Requested sizes and bounds stay exact; pool clients can rely on
that alignment when placing suballocations. Host-visible dirty synchronization is explicit and
does not imply fault-driven UVM.

```console
sbt --server --batch 'spirv/generateSPIRVSources; spirv/generateSPIRVReferenceTests'
```

See [ISA execution](../../docs/ISA_EXECUTION.md), [runtime boundaries](../../docs/RUNTIME.md)
and [conformance](../../conformance/README.md). Missing pinned references may require cache
preparation; checked-in generated sources alone do not promise an entirely offline cold build.
