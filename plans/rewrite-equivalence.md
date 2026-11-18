# Rewrite equivalence

Status: all three steps are done. `Proof.prove` encodes integer, bitwise and floating-point sequences in the SMT backend, refuses an instruction whose modes the definition does not render, and reports each instruction it relied on with the differential coverage behind it. AMDGPU arithmetic renders MODE's FP_ROUND fields beside its denormal fields, and the solver proves it for every rounding mode. A decoded instruction enters a proof as the form of its own operands (`AMDGPUForms`, `SASSForms`), with constant-bank reads on SASS and carries through VCC on AMDGPU.

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
- Locations are the 32-bit register units and flags the live sets name. A wide operand, such as `v[0:1]`, `s[2:3]`, VCC in a wave64 or a SASS pair, joins the units it spans, so a span and the registers under it are one state. Integers of one width share a location whatever their sign.
- A SASS constant-bank read is an input the window does not write: a read of one array of the banks' bytes, indexed by bank and offset, which both sequences share and nothing stores to. Reads that overlap agree byte for byte.
- An AMDGPU proof is over one lane, the lane a shared input below 64: `wave.lane()` is that input and `wave.ballot(p)` holds `p` in the lane's bit and zero elsewhere, as a wave with that lane alone active writes it. This holds for the whole wave only while no lane's result depends on another lane's bit, so a sequence that reads whole a mask a lane wrote before it is refused (`LaneMasks`, `Equivalence.crossLane`).

### 3. Trust in the semantics

- A proof is only as good as the rendered semantics. Every instruction a proof uses must pass its `semantics-check` lane. A proof report names the instructions it relies on, and the differential coverage behind each.
- Floating-point modes (rounding, denormal flush, `.FTZ`, `.SAT`) are part of each definition. An instruction whose modes are not all rendered is rejected, not assumed.
- A decoded instruction is admitted by its own form, and by nothing else: its own operands must render to a definition through the explorer's path, `Rejection` must admit that definition (plain memory accesses, constant-bank reads, and the lane's own bit of a lane mask), every parameter and result must bind a location or a literal, and the lane must hold a checked case of the form. For SASS that is a case of the mnemonic whose instance has operands of the same kinds. For AMDGPU the generator must check the mnemonic on the target or on the checked target of its ISA (`AMDGPUGenerator.checked`, so gfx1036 takes gfx1030's), and a proof also needs a case it keeps of the encoded mnemonic there, which is looked up only when a proof asks: describing every instance of a mnemonic costs far more than admitting one instruction. Windows, moves, the differential executor and `Proof` take the same `Form`, and a proof report names the cases behind each instruction.

## Checks

As in `cost-model.md`. In addition, known-equivalent and known-different pairs (strength reductions, identities, and off-by-one rewrites) form a golden suite.

## Non-goals

- Whole-kernel equivalence, loops, and control flow beyond a straight-line window.
- Proving rendered semantics correct against hardware. The `semantics-check` lanes and conformance do that.

## Limits

- AMDGPU add, subtract, multiply and fused multiply-add round as MODE.FP_ROUND says: its low two bits for binary32 and the next two for binary16 and binary64. Other AMDGPU float instructions are refused because they keep a fixed nearest-even rounding the hardware may not: the mixed-precision and dot forms, V_DIV_FMAS, atomics, and the matrix units. Bitwuzla has no `fp.to_ieee_bv`, so a floating-point value's bits are tied to its term by an equality, and the payload of a NaN the rendered semantics leaves open is the solver's choice: that of binary16 and binary32 fused multiply-add with a NaN operand, whose result the host picks. An invalid operation gives each ISA's measured NaN whatever the host is: the AMDGPU's negative quiet NaN at every width (gfx1036), NVIDIA's canonical 0x7fff and 0x7fffffff at binary16 and binary32 and the negative quiet NaN at binary64 (sm_89). Both are committed as `DefaultNaNSuite` in `amdgpu-vm` and `nvidia`. Other hardware generations are unmeasured. The differential filter types AMDGPU registers as integers, so it samples no float edge values; a pair that only differs on one needs more runs than the default 64.
- The lane checks constant-bank forms in bank 3 alone, which the rendering binds to memory, so a form renders and executes with its operand moved there and a proof assumes that the number of a bank selects what is read and nothing else. A constant offset held in a register lowers to a memory load and is refused. A solver counterexample that rests on what a bank holds, or that lies in a lane other than the reference's lane 4, cannot be replayed on the VMs and is undecided.
- SASS coverage tells forms apart by operand kinds, not by modifiers, so a modifier combination the explorer does not instance rides on the cases of its operand kinds; its own definition is still rendered and checked for modes. Refused for want of a rendering or a case: guarded instructions (`@P0 MOV`), which lower to predicated steps; writes of uniform registers (`UMOV`, `UIADD3`, `ULDC`), which render through `Warp.leader`; and forms that write a predicate beside a constant read (`LOP3.LUT P0, ...`, `LEA R2, P0, ...`), which no case instances. AMDGPU coverage is per encoded mnemonic on the target, since the generator keeps one case per definition up to names and numbers and none per operand form.
- Loads and stores to ordinary memory inside windows are not rendered as plain accesses: AMDGPU renders `GlobalMemory` and `LocalMemory` calls, and the SASS cases leave out every definition with a `Memory` context, whose reference does not observe memory.

## Open decisions

- Solver: the project avoids new dependencies. Recommendation: print SMT-LIB text and run an external solver binary (Bitwuzla or Z3) found through the toolchain registry, with no JVM library dependency. The check is then unavailable where no solver is installed, and says so.
- Floating point: proved in the SMT floating-point theory, with each definition's rounding mode, flush, saturation and NaN handling. Integer and bitwise rewrites came first.
