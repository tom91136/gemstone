# Feedback for review and simplify passes

Findings that a review pass raised and that were not applied, with the reason. A later pass should read this before raising the same point again.

## AMDGPU `Instruction.Wait` versus `Instruction.WaitCounts`

Raised in round 2 of the simplify pass on the profiling work (2026-11-02). The proposal was to delete `Instruction.Wait` and use `Instruction.WaitCounts(Vec.empty)` everywhere, since both arms end in `ScalarControl.fence` and `waited` does nothing on an empty vector.

Not applied: the two say different things.
- `WaitCounts` is a counter wait: `s_waitcnt`, its split forms and the GFX12 `s_wait_*` instructions. Its payload is what the profiler reports as `AsyncKind.WaitCounter`.
- `Wait` is where `VMCompiler.hint` sends every instruction whose effect the VM does not need to model: trace data, performance-level changes, wake-ups, instruction-cache invalidation, debugger branches, prefetches, cache write-backs and discards, `ds_nop`, `v_nop`, `v_pipeflush`, `s_sleep_var`, and the waits with no memory counter (`s_waitcnt_depctr`, `s_delay_alu`, `s_wait_alu`).

Folding them together would make the rendered semantics, the coverage policy and the VMIR dump call a prefetch or a trace marker a counter wait with no counters. The shared body is one `fence` call, so there is nothing to gain. If the name `Wait` misleads, the better change is to rename it for what it is, an instruction with no modelled effect beyond ordering (for example `Ordered` or `NoEffect`), not to merge it into `WaitCounts`.

Reconsidered on 2026-11-06 and kept separate: `Wait` has its own rendered semantics in `InstructionSemantics` ("nothing a lane observes"), which a merge would replace with a counter wait that names no counters, and the compiler uses it as the fallback when no WAITCNT encoding exists. A rename alone changes no behaviour.

Guidance: do not suggest merging an IR case into another because their interpreter arms coincide, when the cases mean different things to the renderer, the coverage tables or the observer.
