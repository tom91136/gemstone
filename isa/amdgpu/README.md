# AMDGPU ISA

Public JVM/Native APIs use `net.kurobako.gemstone.isa.amdgpu.gfx6` through `gfx12`
and `cdna1` through `cdna5`. The root package owns syntax, parsing, printing and generated
instructions; family packages select metadata and target-checking ISA profiles.

Family and exact processor are different constraints. Preserve exact XML subfamilies and reject
conflicting encodings rather than choosing one silently. A family package is not evidence that
all instructions execute on every processor in that family.

## Generated and executable boundaries

[Pinned Mesa ACO and AMD XML inputs](../../codegen/ISA_REFERENCES.md) supply instruction names,
forms, encodings, operand roles and availability metadata. Definitions are grouped by encoding
domain; large generated files split at deterministic instruction boundaries. Keep facts in generated
sources and normalization/execution in handwritten code.

The public `Binary` codec implements the supported SOPP subset and reports unsupported formats.
`DispatchInterpreter` separately decodes and executes its implemented code-object instruction
subset; public-codec coverage and runtime coverage must not be conflated. `CodeObject` owns ELF,
descriptors, target selection and linked data. See [execution semantics](../../docs/ISA_EXECUTION.md).

`generateAMDGPUOracleResources` selects deterministic positive/diagnostic cases from the pinned
LLVM MC tree. The manifest records source and selected counts. JVM oracle tests check parse/print,
supported byte encodings and explicit unsupported inventories; handwritten semantics tests also
run on Native. These checks do not claim full LLVM or vendor conformance.

## Runtime invariants

- Preserve simultaneous operand reads, SGPR/VGPR aliasing, EXEC/VCC masks and target wave width.
- Pass global memory through worker execution explicitly; unbound ELF globals must work too.
- Reject trailing typed launch arguments while preserving legitimate padding and inferred descriptors.
- Linked globals and bound allocations share storage. Ordinary native aliases retain allocation lifetime;
  mixed native/ELF global pointers need not have the same numerical address.
- Keep instruction charging, local barriers and device-atomic scheduling ISA-specific. Do not infer an
  implicit grid barrier or collapse packed wave execution into a common interpreter representation.

[HIP/HSA routes](../../server/HIP_RUNTIME_ROUTES.md) describe the application boundary and qualification limits.
