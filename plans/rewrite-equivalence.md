# Rewrite equivalence

Status: all three steps are done. `Proof.prove` encodes integer, bitwise and floating-point sequences in the SMT backend, refuses an instruction whose modes the definition does not render, and reports each instruction it relied on with the differential coverage behind it. AMDGPU floating-point arithmetic is refused, because the renderer reads MODE's denormal fields but not its rounding field.

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

## Limits

- AMDGPU floating-point arithmetic is refused, because its definitions read MODE's denormal fields but not its rounding field. Bitwuzla has no `fp.to_ieee_bv`, so a floating-point value's bits are tied to its term by an equality, and the payload of a NaN the rendered semantics leaves open is the solver's choice: that of binary16 and binary32 fused multiply-add with a NaN operand, whose result the host picks. An invalid operation gives the negative default NaN, as the JVM on x86 and the AMDGPU do. The differential filter types AMDGPU registers as integers, so it samples no float edge values; a pair that only differs on one needs more runs than the default 64.

## Open decisions

- Solver: the project avoids new dependencies. Recommendation: print SMT-LIB text and run an external solver binary (Bitwuzla or Z3) found through the toolchain registry, with no JVM library dependency. The check is then unavailable where no solver is installed, and says so.
- Floating point: proved in the SMT floating-point theory, with each definition's rounding mode, flush, saturation and NaN handling. Integer and bitwise rewrites came first.
