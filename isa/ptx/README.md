# PTX ISA

The public API is versioned as `net.kurobako.gemstone.isa.ptx.v9_3`. `Parser` parses PTX modules and individual
statements into the `Program`/`Statement` syntax tree, and `Printer` emits a deterministic representation for
parse-print-parse transformations. `Instruction` is a generated enum: each concrete PTX operation has a named case
with concise named operand fields, modifiers, and optional predication. Every case references one singleton
`InstructionDef`, which owns its canonical opcode, decoder, and specification `Metadata`; the instruction exposes
those values together with `qualifiedOpcode`, flattened `operands`, `mods`, and `pred`. `Instruction.decode` derives
its dispatch from `InstructionDef.values`, safely rejecting missing, invalid, or ambiguous forms rather than using a
second generated mnemonic match. Unknown operations are not representable as instructions.

`v9_3` is the PTX language/ISA version. An `sm_*` value is a module target selected by its `.target` directive, not
the PTX ISA version: the 9.3 document describes many targets through `sm_121*`. Target-specific validity will
therefore remain a validation concern rather than another copy of the parser package.

The `ptx` source generators read the pinned CUDA PTX HTML prepared by `codegen / prepareISAReferences`. They emit:

- `src/generated/main/scala/.../Instruction.scala`, containing the enums and the companion-owned `version` and
  `definitions` for 195 concrete operations; and
- `src/generated/test/scala/.../DocumentationSuite.scala`, containing 892 complete example statements extracted from
  the document and checked for typed parsing and AST-preserving canonical round-trips.

Generated files are checked in so ISA changes are reviewable and normal compilation is offline. The explicit
`generatePTXSources` and `generatePTXReferenceTests` tasks update them from the pinned source. The small execution
engine in `Execution.scala` lowers its supported subset directly from the generated enum cases.
