# PTX ISA

The public API is versioned as `net.kurobako.gemstone.isa.ptx.v9_3`. `Parser` parses PTX modules and individual
statements into the `Program`/`Statement` syntax tree, and `Printer` emits a deterministic representation for
parse-print-parse transformations. `Instruction` is a generated enum: each concrete PTX operation has a named case
with named operand fields, modifiers, and optional predication. The sum type exposes `opcode`, `qualifiedOpcode`,
`operands`, `modifiers`, and `predication` uniformly. Unknown operations and invalid arities fail in
`Instruction.decode`; they are not representable as instructions.

`v9_3` is the PTX language/ISA version. An `sm_*` value is a module target selected by its `.target` directive, not
the PTX ISA version: the 9.3 document describes many targets through `sm_121*`. Target-specific validity will
therefore remain a validation concern rather than another copy of the parser package.

The `isa_ptx` source generators read the pinned CUDA PTX HTML prepared by `codegen / prepareIsaReferences`. They emit:

- `generated/Instructions.scala`, containing the enum, `Version`, and the top-level `Instructions` specification
  vector for 195 concrete operations; and
- `DocumentationSuite`, 892 complete example statements extracted from the document and checked for typed parsing
  and AST-preserving canonical round-trips.

Generated files are checked in so ISA changes are reviewable and normal compilation is offline. The explicit
`generatePtxSources` and `generatePtxReferenceTests` tasks update them from the pinned source. The small execution
engine in `Ptx.scala` lowers its supported subset directly from the generated enum cases.
