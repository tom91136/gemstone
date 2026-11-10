# Rewrite equivalence

Status: step 1 is done. Step 2 compares single definitions, and sequences and step 3 are in progress. It supports `rewrite-search.md`, and it is useful on its own for checking the rendered semantics against each other.

## Goal

Decide whether two short straight-line sequences of SASS or AMDGPU instructions compute the same result on every input, for every lane, under a stated set of live-out registers and memory. A rewrite counts as correct only when this check passes. Differential testing comes first, because it is cheap, and it only rejects candidates.

## What already exists

- Rendered semantics: `core/.../semantics/render` turns each instruction's machine definition into terms (`Definition`, `Render`, `Backend`), with a Scala backend (`ScalaBackend`) that the executable-semantics work runs.
- Differential checking: the `semantics-check` lanes (`isa/semantics-check`) compare rendered semantics with the VMs on generated inputs.
- The VMs execute whole kernels, so a candidate can also run in context.

## Steps

### 1. Differential filter

- Run both sequences on generated register and memory states through the VMs, one instruction at a time, using the `Reference` harness of `semantics-check`. Rendered definitions exist only as build-generated code, and `semantics-check` already compares them with the VMs. The executor is a parameter, so a rendered one can replace it. Generate edge values per operand type: zero, signs, NaN, infinities, denormals, and extremes.
- Compare the live-out state. Any difference rejects the candidate with a counterexample.
- This step decides nothing positive: surviving candidates go on to step 2.

### 2. SMT backend

- Add a `Backend` that prints a definition as SMT-LIB over bit-vectors, with floating point in the SMT-LIB float theory where the solver supports it.
- Encode both sequences over shared symbolic inputs, and assert that some live-out differs. `unsat` proves equivalence over the encoded semantics. `sat` gives a counterexample, which becomes a regression input for step 1.
- Memory is a bounded array over the addresses the window touches. Windows with other side effects (atomics, barriers, async copies, texture) are out of scope and rejected before encoding.

### 3. Trust in the semantics

- A proof is only as good as the rendered semantics. Every instruction a proof uses must pass its `semantics-check` lane. A proof report names the instructions it relies on, and the differential coverage behind each.
- Floating-point modes (rounding, denormal flush, `.FTZ`, `.SAT`) are part of each definition. An instruction whose modes are not all rendered is rejected, not assumed.

## Checks

As in `cost-model.md`. In addition, known-equivalent and known-different pairs (strength reductions, identities, and off-by-one rewrites) form a golden suite.

## Non-goals

- Whole-kernel equivalence, loops, and control flow beyond a straight-line window.
- Proving rendered semantics correct against hardware. The `semantics-check` lanes and conformance do that.

## Open decisions

- Solver: the project avoids new dependencies. Recommendation: print SMT-LIB text and run an external solver binary (Bitwuzla or Z3) found through the toolchain registry, with no JVM library dependency. The check is then unavailable where no solver is installed, and says so.
- Floating point: whether to prove FP rewrites in the SMT float theory, or to limit proofs to integer and bitwise rewrites and leave FP to the differential filter. Recommendation: integer and bitwise first.
