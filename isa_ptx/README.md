# PTX ISA

The public API is versioned as `net.kurobako.gemstone.isa.ptx.v9_3`. `Parser` parses PTX modules and individual
statements into the generic `Program`/`Statement` syntax tree, and `Printer` emits a deterministic
representation for parse-print-parse transformations. Instructions use one `Statement.Instruction(Instruction)` node
with an `Opcode`, `Operand` values, orthogonal `Modifier` values, and optional predication; opcode-specific Scala
classes are deliberately not generated.

`v9_3` is the PTX language/ISA version. An `sm_*` value is a module target selected by its `.target` directive, not
the PTX ISA version: the 9.3 document describes many targets through `sm_121*`. Target-specific validity will
therefore be a catalog/validation concern rather than another copy of the parser package.

The `isa_ptx` source generators read the pinned CUDA PTX HTML prepared by `codegen / prepareIsaReferences`. They emit:

- `generated.Catalog`, a compact catalogue of the 178 instruction sections and their documented syntax forms; and
- `DocumentationSuite`, 799 complete example statements extracted from the document and checked for
  parsing, known mnemonics, and AST-preserving canonical round-trips.

Generated files are checked in so ISA changes are reviewable and normal compilation is offline. The explicit
`generatePtxSources` and `generatePtxReferenceTests` tasks update them from the pinned source. The small execution
engine in `Ptx.scala` lowers its supported subset from the same generic syntax tree.
