# PTX ISA

The public package is `net.kurobako.gemstone.isa.ptx.v9_3`. PTX language version and
module `sm_*` target are distinct: targets are validation inputs, not duplicate parser packages.

`Parser` builds the typed syntax tree; `Printer` provides canonical rendering; `Validator`
checks implemented version/target constraints. `Lowering` produces PTX kernels and device
functions, and `Interpreter`, `Semantics` and `StateEffects` own execution and staged writes.
See [shared execution contracts](../../docs/ISA_EXECUTION.md).

Generated instruction definitions own names, forms, operand metadata and decoding. Unknown or
ambiguous instructions cannot enter the typed AST. Parsing or a generated descriptor does not
establish executable support; unsupported execution fails explicitly.

The generators consume [pinned NVIDIA references](../../codegen/ISA_REFERENCES.md) and retain
reviewable instruction sources and documentation examples under `src/generated`. Change the
generator, not its output. Missing references may require cache preparation during a cold build.

```console
sbt --server --batch 'ptx/generatePTXSources; ptx/generatePTXReferenceTests'
```

Runtime regressions cover supported control flow, memory, reductions and cross-workgroup
behavior separately from parse/print examples. A reduction mask cannot invent a value for a
nonparticipating lane; vector publication does not imply a grid-wide barrier or general hardware
128-bit atomicity. Shared-owner memory bounds, coherence and lifetime must survive fast-path changes.
