# Performance ledger

This file records measured hotspots and experiments that are too invasive to pursue casually. Numbers are local JVM
measurements, not release thresholds; compare like-for-like runs on the same machine.

## Focused OpenCL CTS benchmark scout (2026-09-22)

Five saved emulated compact CTest runs on `soraws-uk` consistently put
`basic/async_strided_copy_global_to_local` at 129--465 seconds and
`basic/async_strided_copy_local_to_global` at 97--477 seconds. The contiguous async copy cases took 82--234
seconds. Each CTS case sweeps scalar types and vector widths over hundreds of workgroups, so a whole-case wall
clock obscures the cost of one dispatch. `opencl-cts-async-copy` in the benchmark project retains the relevant
barrier, `OpGroupAsyncCopy`, and `OpGroupWaitEvents` paths, with direction and stride controls and output checks.
Its bounded default is suitable for repeated local measurements; it is not a CTS pass or a prediction of the full
CTS case duration. On this machine, a one-dispatch scout with 8 groups, 8 items per group, and stride 2 took about
43 ms per dispatch for the local-to-global direction. These are orientation numbers, not regression thresholds.

## Focused CUDA, HIP, and HSA scout (2026-09-22)

Across the same five saved compact CTest runs, the CloverLeaf miniapp took 43.5--212.9 seconds on CUDA and
23.6--86.9 seconds on HIP. MiniBude took 7.7--9.1 seconds on CUDA and 6.4--8.2 seconds on HIP. The compact HSA
route contained only `code-object-dispatch` and `async-copy-after-dispatch`, each at 0.4--6.2 seconds including
process startup and runtime setup; there is no consistently long HSA case in this workload.

`cuda-cloverleaf-flux` and `hip-cloverleaf-flux` isolate the double-precision neighbouring-cell flux pattern
from the CloverLeaf source in PTX and AMDGPU code-object form. They verify both output arrays and measure repeated
dispatches after program preparation. The full miniapp spends time across many kernels, transfers, and timesteps;
these probes are representative work, not a claim that flux calculation alone accounts for its wall time.
The initial two-workgroup HIP scout left the second group's output unwritten because the benchmark filled HIP's
hidden kernarg fields with zero. The code object multiplies its workgroup ID by `hidden_group_size_x`, so every group
computed indices starting at zero. The fixture now reads the hidden-field layout from the code object's metadata and
supplies block counts and group sizes from launch geometry. Checked 64-, 65-, 128-, and 129-cell dispatches all pass,
including partial final workgroups. This fixes the benchmark's launch arguments; it does not change AMDGPU
instruction execution.
`hsa-vector-copy` compiles the direct-HSA conformance test source with the same target and code-object flags, then
checks copied data and the unused guard allocation. It measures Gemstone's AMDGPU dispatch path, not HSA queue or
signal overhead.

## Focused benchmark JFR scout (2026-09-22)

Short JVM JFR `profile` recordings used one warmup and one measured sample per case. The measured work repeated a
correctness-checked dispatch enough times to collect CPU samples; the recording covers the whole forked JVM process,
including startup and the warmup. The raw recordings are in the ignored `target/benchmark-jfr/` directory. These
figures are orientation measurements under JFR, not comparison baselines or estimates of conformance wall time.
These recordings predate the container-based fixture generator. In particular, the CUDA recording used Clang PTX;
the current generator emits CUDA 13.3 `nvcc` PTX. Rerun JFR before comparing a change against those numbers.

| Case | Dispatches per sample | Measured ns/dispatch | Caller-thread bytes/dispatch | Dominant JFR CPU samples |
| --- | ---: | ---: | ---: | --- |
| OpenCL async copy, local-to-global, stride 2 | 5,000 | 691,154 | 3,286,588 | `HashMap.get` 11.4%, boxed equality 11.1%, `TrieMap` lookup 7.5% |
| CUDA CloverLeaf flux, 64 cells | 15,000 | 185,855 | 651,562 | PTX execution/fast execution 19.7%, register index lookup 5.4%, launch coordinates 6.1% |
| HIP CloverLeaf flux, 64 cells | 15,000 | 229,625 | 818,072 | AMDGPU `stepInstruction` 43.4% |
| HSA vector copy, one group | 20,000 | 46,440 | 89,240 | AMDGPU `stepInstruction` 53.6% |

The OpenCL profile also sampled `resumeInvocation` (6.2%) and allocation in invocation values, maps, and `Some`.
CUDA allocation samples included `long[]`, `Right`, tuples, and object arrays. HIP allocation samples included
tuples, boxed longs, `MemoryRegion`, and vector builders; HSA included `Right`, byte arrays, lambdas, and tuples.
The HIP/HSA memory fixtures themselves construct address-space metadata, so some of their allocation samples are
benchmark overhead. GC pause totals were 55 ms (CUDA), 100 ms (HIP), 60 ms (HSA), and 90 ms (OpenCL) across each
full recording. The JFR self-sample percentages are useful for choosing a closer follow-up, not for attributing
end-to-end runtime to one method.

## OpenCL collective register-file reuse (2026-09-22)

The OpenCL local-to-global async-copy case allocates one SPIR-V register file per work item while its collective
operations are suspended. A completed workgroup releases those values, so sequential dispatch now reuses one file
per local lane in the next workgroup. Parallel dispatch still creates separate files for concurrently running groups.

On a busy host, `taskset -c 4` with 16 groups, 8 local items, stride 2, 2 warmups, 5 measured samples, and 2,000
dispatches per sample gave a median of 742 us/dispatch before the change and 708 us/dispatch after it. The sample
ranges overlapped (693--843 us before; 659--739 us after), so the timing improvement is provisional. Caller-thread
allocation was more stable: about 3.385 MB/dispatch before and 2.267 MB/dispatch after, a 33% reduction. Both runs
verified the output checksum on every sample. The SPIR-V test suite passed all 50 tests; the benchmark also passed
global-to-local and four-worker correctness checks. The allocation figure for the parallel check covers only the
calling thread and should not be compared with sequential allocation.

## SPIR-V type lookup and size cache (2026-09-22)

The same OpenCL profile placed immutable type-map lookup and boxed key comparison in several instruction paths, and
`TrieMap` lookup in type-size calculation. The loaded program now stores defined types in a bounded ID-indexed array
and stores ordinary non-negative type sizes in an atomic integer array. Large or unusual IDs retain the map path.
The type array ends at the largest defined type ID rather than the module's full ID bound.

With the same pinned 16-group async-copy probe, the type array reduced caller-thread allocation from about 2.267
MB to 1.813 MB per dispatch; the size cache reduced it again to about 1.641 MB. This is roughly 52% below the
original 3.385 MB. The five-sample medians were 750 us and 765 us respectively, versus 708 us before these two
changes. Sample ranges overlap substantially (707--804 us, 626--877 us, and 659--739 us respectively), and host
load was variable, so these runs do not establish a dispatch-time improvement. Both copy directions and a four-worker
run verified output; all 50 SPIR-V tests, three benchmark tests, and Scala Native compilation passed.
An updated JFR scout no longer showed `HashMap.get` or `TrieMap` lookup among the leading self-sample methods. Its
remaining hot samples were spread across instruction dispatch, memory operations, register lookup, and refinement.
The recording is in the ignored `target/benchmark-jfr/` directory.

## Whole benchmark inventory scout (2026-09-22)

All 13 benchmark cases registered at the time were exercised on the local JVM. The generated CUDA, HIP, HSA, and OpenCL images
were built by the current container fixture generator; the other cases use their existing source or synthetic inputs.
The raw output is in ignored `target/benchmark-scout/` logs. Times below are orientation measurements on a busy host,
not cross-backend comparisons or speedup claims. The runner's JSON `elapsed_nanos` includes each case's setup and
verification and sets `operation_count` to one; the case-specific timing fields are the repeated inner operations.

| Case | Scout observation |
| --- | --- |
| `cuda-cloverleaf-flux` | 64 cells, 5,000 dispatches/sample: 251--268 us/dispatch across five samples; about 707 KB caller-thread allocation. Current fixture is `nvcc` PTX. |
| `hip-cloverleaf-flux` | 64 cells: 332--365 us and about 816 KB/dispatch before fixture cleanup; 191--207 us and about 252 KB after. |
| `hsa-vector-copy` | One group: 62--69 us and about 102 KB/dispatch before fixture cleanup; 43--46 us and about 62 KB after. |
| `opencl-cts-async-copy` | The earlier sequential local-to-global probe with stride 2 measured 626--877 us and about 1.64 MB/dispatch after the SPIR-V changes. |
| `ptx-execution` | Tiny mode's later rounds were 69--77 us/launch. The 4,096-lane vector mode varied from 2.65 to 5.54 ms/launch even after 100 warmup launches; allocation stabilized near 20.8 MB/launch. |
| `ptx-program-lifecycle` | The 256-entry, 16-instruction decode fixture reached roughly 2.4--3.2 ms and 11.48 MB/decode after five runner warmups, but kept drifting. Launch mode fell from 47 to 29 us over three samples after one warmup. |
| `ptx-register-index` | Numbered names took 12--14 ns/index after one warmup; named names about 3--5 ns/index after three warmups. Both reported effectively zero allocation. |
| `program-image-key` | A reused cached 4 MiB key took about 4 ns/lookup in two samples and 67 ns in an outlier. Raw key lookup took about 3.5 ms. Tiny cached operations are especially sensitive to host noise. |
| `spirv-execution` | 64 workgroups and 64 integer additions at one worker: 0.69--0.98 ms/dispatch, with later allocation around 164 KB. This is a separate arithmetic path from the OpenCL collective probe. |
| `amdgpu-execution` | One-group vector copy: 175--301 us/dispatch after fixture cleanup; later allocation near 100 KB. |
| `amdgpu-alu` | 512-instruction move mode was around 0.71 ms/dispatch; binary mode ranged 0.71--0.92 ms even after longer warmup. Its later allocation was about 60 KB. |
| `amdgpu-vector-memory` | Reports allocation rather than an inner elapsed time: about 61.9 KB/dispatch after warmup, with a checked copy. |
| `journal-inspection` | Five inspections of a 52.5 MB journal took 17.7--24.4 ms each and allocated about 105.8 MB each. |

JFR on the current generated binaries sampled CUDA work in PTX execution, little-endian decoding, launch coordinates,
and register operations. HIP and HSA sampled AMDGPU `stepInstruction` most often. HSA also sampled the benchmark's
`regionSizes` method in 5.7% of CPU samples and 14% of allocation pressure. The HSA and HIP fixtures now retain their
fixed region-size metadata, and the older AMDGPU execution fixture updates a retained size map when it allocates.
This changes what the probes measure; it does not change emulator execution. The before/after allocation differences
were stable within each set of samples (HIP about 816 to 252 KB, HSA about 102 to 62 KB, older AMDGPU copy about 121
to 100 KB). The time differences are compatible with removing fixture work but remain vulnerable to host load and JIT
effects. All output checks, benchmark tests, scalafix, and Scala Native benchmark compilation passed.

## Pinned PTX execution iteration (2026-09-22)

The CUDA CloverLeaf flux benchmark ran with the parent `sbt` process pinned to CPU 4 using `taskset -c 4`; the forked
JVM inherited that affinity. Each run used three warmups and five measured samples of 5,000 dispatches at 64 cells.
The raw A/B/A/B logs are in ignored `target/benchmark-scout/cuda-*.log`. The current container-generated `nvcc` PTX
uses numbered registers and predicates. Its JFR recording put PTX execution, parameter decoding, and register and
parameter-buffer work in the hot path.

| Change | Stable caller-thread allocation per dispatch | Measured time per dispatch |
| --- | ---: | ---: |
| Original numbered-register maps | about 686 KB | 156--158 us in the first pinned run |
| Named register and predicate maps allocated only if used | about 670 KB | 157--164 us in the first candidate run |
| Parameter arguments decoded once per instruction, with lazy maps retained | about 630 KB | 153--156 us in the quiet candidate and bracket runs |
| Parameter-buffer lookup via direct iterator, with both changes retained | about 551--582 KB | 145--154 us in quiet samples; other samples reached 206 us |

Restoring the previous parameter-load code between candidate runs returned allocation to about 670 KB/dispatch;
restoring the previous buffer lookup returned it to about 631 KB/dispatch. The 4,096-lane PTX vector case fell from
about 19.59 MB to 18.81 MB per launch after parameter predecode. These allocation differences are reproducible, but
the time series drifted within and between JVM processes. No speedup is established. These changes retain named
register and local-parameter behavior, and the CUDA and vector benchmarks verified their outputs.
The 87 PTX JVM tests passed, PTX Scala Native compilation passed, and scalafix passed with a local suppression for
the measured direct iterator lookup.

The HIP, HSA, and OpenCL fixtures were also rerun on CPU 4. Three warmup samples were insufficient for HSA and
OpenCL, whose measured times continued to fall. With ten warmups and five measured samples, HIP flux took
199--256 us/dispatch at about 250 KB caller-thread allocation, HSA vector copy took 42--76 us at about 57--60 KB,
and OpenCL CTS local-to-global copy took 470--484 us at about 1.67 MB apart from one 619-us outlier. Every sample
verified its output. The remaining time spread is large enough that these are orientation baselines, not thresholds
for a small speedup. The raw logs are `target/benchmark-scout/pinned-other-backends*.log`.

### Follow-up PTX probes

With ten warmups and CPU 4 affinity, making per-lane PTX local-memory maps lazy produced mixed CUDA allocation
results: the first baseline/candidate pair was about 551/577 KB per dispatch, while the bracketed second pair was
about 613/555 KB. Timing overlapped. The local-memory PTX tests passed, but the whole-dispatch allocation evidence
does not support keeping that change, so it was reverted. A lower-bound guard for parameter-buffer addresses also
failed a bracketed comparison: about 595 KB with the guard versus 543 KB after restoring the previous lookup, with
no reliable time improvement. That guard was reverted as well. Raw measurements are in the ignored
`target/benchmark-scout/cuda-local-map-*.log` and `cuda-param-min-*.log` files.

## Quiet pinned AMDGPU and SPIR-V iteration (2026-09-22)

All runs below were pinned to CPU 4 in the independent `gemstone-perf` clone. Raw logs and JFR recordings are under
ignored `target/benchmark-scout/` and `target/benchmark-jfr/`. The HIP, HSA, and AMDGPU probes checked their output
on every sample. The direct SPIR-V arithmetic probe completed without a fault.

- The 512-instruction `amdgpu-alu` binary probe took about 662 and 671 us/dispatch in the A and A2 baselines, with
  about 60 KB of caller-thread allocation. JFR put 141 of 341 execution samples in vector-register bank lookup.
  Moving an unused destination read into only the two accumulate operations was slower (814 us median in B);
  pre-resolving source and destination banks per instruction was also slower (701--723 us) and added about 4 KB per
  dispatch. Both changes were reverted. This result applies to the synthetic binary opcode probe, not to every
  AMDGPU kernel.
- The direct `spirv-execution` probe ran 64 workgroups, 64 integer additions per invocation, one worker, and 30,000
  dispatches under JFR. It took 144 us/dispatch with 94 KB caller-thread allocation. `InvocationValues.scalarType`
  accounted for 148 of 314 execution samples, called from scalar arithmetic's runtime operand checks. Removing those
  checks needs a typed instruction specialization that preserves missing-value and vector fallback behavior; no
  speculative change was kept. A separate pinned, non-JFR run with 30,000 dispatches per sample took 87--117 us and
  88--90 KB after warmup. The 500-dispatch samples showed large timing and allocation swings, so this path needs long
  samples for comparisons.
- The checked HIP CloverLeaf fixture at 128 cells had a steady median of 371 us/dispatch and 504 KB in the first
  baseline, then 383 us and 497 KB in the A2 baseline. The checked one-group HSA copy had steady medians of 43 us and
  62 KB, then 42 us and 56 KB. The HSA allocation shift between baseline runs indicates JIT state matters even with
  five warmup samples. HIP and HSA JFR traces both sampled the AMDGPU instruction switch and carry-producing vector
  instructions. Replacing their boolean carry arrays with a mask captured by `foreachActive` raised HIP allocation
  to 567 KB and did not improve its time. An explicit lane loop took about 757 us on HIP and 78 us on HSA and raised
  allocation on both. Both carry-mask variants were reverted.

These A/B/A checks found no safe new interpreter optimization. The large AMDGPU instruction switch appears sensitive
to small code-shape changes, so the next attempt should isolate or predecode a specific hot opcode and repeat checked
HIP/HSA runs alongside the synthetic probe.

## AMDGPU instruction-switch compilation (2026-09-22)

The original `stepInstruction` generated 23,798 bytes of JVM bytecode. In a 50,000-dispatch HSA run with
`-XX:+PrintCompilation`, HotSpot compiled the wave loop and several instruction callbacks but did not compile that
switch. Splitting its exhaustive match at scalar, vector, and compare/memory boundaries produced four handlers of
about 4.1--5.6 KB each. The same JIT log then showed all four reaching C2 compilation. The instruction cases and
their execution bodies were left in place; only the dispatch method boundaries changed.

CPU-4 A/B/A/B runs used five outer warmups and five measured samples for the checked HIP and HSA fixtures. Numbers
below are measured-sample medians; allocation is caller-thread bytes per dispatch. The synthetic arithmetic probe
used two outer warmups, five measured samples, and 500 internal warmup dispatches per sample.

| Probe | Original A2 | Split B1 | Split B2 | Final split run |
| --- | ---: | ---: | ---: | ---: |
| HIP CloverLeaf, 128 cells | 389 us / 497 KB | 176 us / 568 KB | 157 us / 511 KB | 170 us / 496 KB |
| HSA vector copy, one group | 42 us / 62 KB | 15 us / 63 KB | 14 us / 55 KB | 14 us / 57 KB |
| AMDGPU ALU, 512 binary operations | 649 us / 60 KB | 322 us / 31 KB | 321 us / 31 KB | 327 us / 31 KB |

The HIP allocation increase in the first split run disappeared after further JIT warmup; its throughput improvement
repeated in every split run.
Every HIP/HSA sample passed its output check. Separate HIP runs also passed at 65 and 129 cells, covering partial
final workgroups. The older `amdgpu-execution` fixture also passed its checksum at about 49--57 us/dispatch after
warmup; that run was not paired with an original-code baseline. The AMDGPU JVM suite passed 319 tests, the benchmark
suite passed three, and AMDGPU Scala Native compiled. Logs are
`target/benchmark-scout/amdgpu-switch-split-{b,a2,b2,final-b,extra,validation}.log` and the paired JIT
compilation logs in the same ignored directory. These speedups are JVM results; the same code compiles for Native,
but Native performance has not been measured here.

## PTX and SPIR-V instruction-handler compilation (2026-09-22)

The same bytecode and JIT inspection was applied to the other interpreter backends. PTX's checked `execute` handler
was 9,315 bytes of JVM bytecode. A long pinned vector run compiled `executeFast` and instruction callbacks, but never
compiled `execute`. Moving the memory and control cases into a second exhaustive handler produced methods of 4,085
and 4,056 bytes; both reached C2 in the corresponding `-XX:+PrintCompilation` run. `executeFast` remains 7,837 bytes
and also reaches C2. Opcode behavior is unchanged; the added final error case is unreachable for compiled VMIR and
protects the new internal dispatch boundary.

Pinned A/B/A/B measurements used the checked CUDA fixture with five outer warmups and five measured samples. The
standalone PTX vector probe used 1,000 warmup launches and five rounds of 1,000 measured launches.

| Probe | Original A1 | Split B1 | Original A2 | Split B2 |
| --- | ---: | ---: | ---: | ---: |
| CUDA CloverLeaf, 64 cells | 156 us / 551 KB | 108 us / 549 KB | 146 us / 581 KB | 108 us / 546 KB |
| PTX vector add, 4,096 lanes | 2.08 ms / 18.20 MB | 2.01 ms / 17.47 MB | 2.16 ms / 18.37 MB | 2.01 ms / 18.07 MB |

Every sample passed its output check. The PTX JVM suite passed 87 tests, the benchmark suite passed three, Scala
Native compiled, scalafix passed, and the changed interpreter file passes scalafmt. The project PTX scalafmt gate
still reports the previously modified `VMCompiler.scala`. Raw measurements and JIT output are in ignored
`target/benchmark-scout/ptx-switch-split-*.log` and `ptx-jit-{original,split}.log`.

SPIR-V did not have the same issue. Its hot handlers are already divided: `resumeInvocation` is 263 bytes,
`executeInstruction` 1,762 bytes, and the largest relevant family handler, `executeArithmetic`, is 5,778 bytes. In a
100,000-dispatch pinned arithmetic run, `resumeInvocation`, `executeInstruction`, `executeInteger`, and `binary` all
reached C2. The run took 99 us/dispatch with about 78 KB of caller-thread allocation. No method split was made.

A final CPU-4 pass in the combined source state verified every sample for CUDA, HIP, HSA, and OpenCL. The last three
measured samples were about 100--107 us for CUDA CloverLeaf, 190--213 us for HIP CloverLeaf, 15--19 us for HSA copy,
and 30--32 us for OpenCL global-to-local async copy, apart from one 35-us OpenCL sample. These figures are a final
correctness and scale check rather than another paired comparison; the raw log is
`target/benchmark-scout/all-isa-switch-split-final.log`.

## AMDGPU internal-region access (2026-09-22)

HIP JFR showed `regions.view.collectFirst` and `find` allocating iterators around every internal global-memory access.
The read and write helpers now share an indexed loop over the small region array. Scalar reads also decode directly
from the matched backing array instead of copying a temporary byte array. CPU-4 A/B/A/B runs used ten outer warmups,
five measured samples, 5,000 HIP dispatches per sample, and 20,000 HSA dispatches per sample.

| Probe | Original A1 | Indexed B1 | Original A2 | Indexed B2 |
| --- | ---: | ---: | ---: | ---: |
| HIP CloverLeaf, 128 cells | 171.2 us / 519.3 KB | 112.5 us / 465.0 KB | 167.0 us / 568.5 KB | 117.3 us / 464.6 KB |
| HSA vector copy, one group | 13.52 us / 56.49 KB | 11.59 us / 53.39 KB | 14.05 us / 58.65 KB | 11.39 us / 50.91 KB |

With 15 outer warmups, direct scalar decoding moved the HIP median from 117.7 us and 464.4 KB to 103.8 us and
397.3 KB. The corresponding HSA run reached 10.48 us and 46.30 KB. A symmetric direct scalar write was rejected:
although HSA improved to 10.27 us and 43.48 KB, HIP regressed to 110.8 us and 456.0 KB. Every retained and rejected
sample passed its checksum. The retained AMDGPU source passed 322 JVM tests, the benchmark suite passed three tests,
Scala Native compiled, and scalafix passed. Raw logs are `target/benchmark-scout/amdgpu-region-search-*.log` and
`target/benchmark-scout/amdgpu-direct-scalar-*.log`.

## Final PTX and SPIR-V allocation scout (2026-09-22)

Two general PTX storage layouts reduced allocation but cost too much throughput and were rejected. Replacing the
96-entry register chunk directory with six growing class arrays changed CUDA CloverLeaf from 100.8 us and 550.3 KB
to 101.6 us and 527.2 KB, while the 4,096-lane vector probe changed from 1.925 ms and 17.45 MB to 1.992 ms and
16.89 MB. Growing predicate-word storage changed CUDA to 101.4 us and 534.9 KB, but raised the vector probe to
1.988 ms and 18.07 MB. The next PTX storage change should use register bounds computed for each kernel instead of a
generic register-file layout.

A newly constructed SPIR-V `InvocationValues` already uses the current global-value map as its immutable fallback.
Copying that same map into its dense local slots was redundant. New invocation files now use the fallback directly;
only reused files refresh their dense slots because workgroup and private regions may have changed. Pinned bracketed
runs of the checked one-group OpenCL global-to-local fixture produced these medians:

| State | Time / dispatch | Caller allocation / dispatch |
| --- | ---: | ---: |
| Original A1 | 31.30 us | 188.68 KB |
| No redundant copy B1 | 30.76 us | 180.11 KB |
| Original A2 | 29.75 us | 186.12 KB |
| No redundant copy B2 | 30.32 us | 176.66 KB |

The time ranges overlap, so no throughput improvement is claimed. Allocation fell by 4.5--5.1% in both brackets.
The 64-group arithmetic probe remained within timing and late-JIT allocation variation. Separate 16-group runs
passed checksums for both global-to-local and local-to-global directions, exercising the retained refresh path.
Raw logs are `target/benchmark-scout/spirv-{fallback-values,new-values-no-copy}*.log` and
`target/benchmark-scout/spirv-no-new-copy-multigroup-validation.log`.

## Quiet-host storage and reuse iteration (2026-09-23)

The four pending candidates were rerun on CPU 4 with checked outputs and long inner samples. The PTX state cache was
bracketed with ten outer warmups, five CUDA samples of 5,000 dispatches, and five vector rounds of 1,000 launches.
The SPIR-V cases used ten outer warmups and five samples of 10,000 dispatches. The atomic case used ten warmups and
seven samples of 200,000 dispatches.

| Candidate | Previous state | Retained state |
| --- | ---: | ---: |
| PTX block-state reuse, CUDA 64 cells | 103.2 us / 540.1 KB | 99.0--100.0 us / 361.5--362.0 KB |
| PTX block-state reuse, 4,096 lanes | 1.951 ms / 16.04 MB | 1.917--1.923 ms / 8.86 MB |
| PTX kernel register bound, 4,096 lanes | 2.158 ms / 8.86 MB | 1.902 ms / 8.47 MB |
| SPIR-V single-use collective values | 31.40 us / 185.60 KB | 30.71 us / 171.78 KB |
| SPIR-V unchanged-pointee bitcast | 31.36 us / 171.66 KB | 30.83 us / 170.88 KB |

PTX now retains at most one idle block state in an atomic slot and clears all lane, parameter, shared, and local state
before reuse. A kernel-derived register bound limits both initial directories and the directory scan during reset;
named-only kernels use one directory entry. SPIR-V skips touched-slot storage for a one-workgroup collective dispatch,
where invocation files cannot be reused, and preserves pointer objects when a bitcast keeps the pointee type. The
multi-workgroup collective path still tracks and clears touched slots before reuse.

AMDGPU's indexed global-atomic lookup measured 766--785 ns/dispatch versus 778--800 ns for the iterator form. The
samples overlap, and allocation varied between equal and 72 bytes lower, so no throughput or allocation improvement
is claimed. The indexed form remains because it is bounded, avoids constructing the view/zip/find pipeline, and
matches the retained scalar read/write lookup without a measured regression. Raw logs are
`target/benchmark-scout/quiet-{pending-current,ptx-pool,ptx-register,spirv-single-use,spirv-pointer,amdgpu-atomic}-*.log`.

## PTX primitive effect staging (2026-09-23)

PTX scalar arithmetic already writes through allocation-light instruction handlers, but scalar loads and parameter
loads still created one `VMStateWrite.Register` object per lane and appended it to a new `ArrayBuffer`. The staged
write rule is needed because a later lane can fault before any result from the instruction may become visible. The
replacement keeps this rule while storing register effects in reusable parallel arrays owned by the block state,
with lane and value fields kept as primitives. Memory, atomic, predicate, parameter, and other uncommon effects
retain the general object path. A commit first validates every external memory effect, then applies all effects in
their original order.

Pinned restore-and-reapply runs used ten outer warmups, five CUDA samples of 5,000 dispatches, and five vector rounds
of 1,000 launches. All outputs were checked.

| Probe | Restored object staging | Primitive staging B1 | Primitive staging B2 |
| --- | ---: | ---: | ---: |
| CUDA CloverLeaf, 64 cells | 98.30 us / 362.0 KB | 92.82 us / 286.0 KB | 92.67 us / 282.1 KB |
| PTX vector, 4,096 lanes | 1.913 ms / 8.861 MB | 1.838 ms / 7.669 MB | 1.833 ms / 7.669 MB |

Against the restored baseline, the two retained runs make CUDA about 5.6--5.7% faster with 21--22% less caller
allocation, and the vector probe about 3.9--4.2% faster with 13.5% less allocation. A post-change JFR sample contains
no sampled `VMStateWrite.Register` allocation; general `VMStateWrite.Memory` effects remain visible as intended.
Focused tests cover validation atomicity, growth beyond the initial 64 entries, ordering, clearing, and reuse. The
complete PTX suite passed 89 tests, Scala Native compiled, and Scalafix and Scalafmt passed. Raw logs are
`target/benchmark-scout/quiet-ptx-primitive-effects-{a2,b1,b2}.log`; the follow-up recording is
`target/benchmark-jfr/quiet-ptx-primitive-effects-20260923.jfr`.

## PTX scalar arithmetic and launch preparation (2026-09-23)

The next profile showed unsigned integer multiply-add converting each operand to a decimal string and then to
`BigInt` for every active lane. PTX now computes the low and high halves of unsigned 64-bit multiplication from
32-bit limbs and detects addition carry with unsigned comparisons. This implementation is shared by JVM and Scala
Native. Boundary values and 256 deterministic random inputs were checked against the previous `BigInt` reference for
32-bit low/high, 64-bit low/high, 32-to-64 wide, and carry-in forms.

| Probe | Restored `BigInt` | Primitive arithmetic |
| --- | ---: | ---: |
| CUDA CloverLeaf, 64 cells | 91.00 us / 331.2 KB | 83.79 us / 293.2 KB |
| PTX vector, 4,096 lanes | 1.718 ms / 8.865 MB | 1.511 ms / 7.348 MB |

This is about 8% faster with 11% less caller allocation on CUDA and 12% faster with 17% less allocation on the large
probe. Raw restore-bracket logs are `target/benchmark-scout/quiet-ptx-primitive-mad-{a2,b1}-*.log`.

Complete launches now reuse the already constructed all-lanes mask instead of deriving coordinates, allocating a
one-lane mask, and unioning that mask for every work item. A partially filled launch checks each group's active size
first and retains the original lane calculation for its partial group. The 32-lane CUDA case was timing-neutral; its
allocation varied upward between JVM processes. The 256-lane probe improved from 1.511 ms and 7.348 MB to 1.302 ms
and 4.985 MB. A checked 65-cell CUDA run exercised the retained partial-group path. Raw logs are
`target/benchmark-scout/quiet-ptx-full-group-b{1,2}-*.log`.

Finally, parameter loads now walk their predecoded destination array with an indexed loop. This removes the
per-lane `zipWithIndex`, builder, iterator, and tuple pipeline and also gives HotSpot a substantially smaller hot
shape. A restore-and-reapply bracket produced these medians:

| Probe | Restored collection loop | Indexed loop B1 | Indexed loop B2 |
| --- | ---: | ---: | ---: |
| CUDA CloverLeaf, 64 cells | 85.39 us / 306.3 KB | 71.72 us / 131.7 KB | 71.10 us / 131.3 KB |
| PTX vector, 4,096 lanes | 1.305 ms / 5.112 MB | 1.164 ms / 2.328 MB | 1.179 ms / 2.328 MB |

The indexed loop is 16--17% faster with about 57% less allocation on CUDA and 10--11% faster with about 54% less
allocation on the large probe. A post-change JFR recording no longer samples `BigInt`, unsigned decimal conversion,
or the parameter `zipWithIndex` builders. Its remaining application allocation is concentrated in boxed `Either`
results and general memory-write effects. The PTX JVM suite, Scala Native compilation, Scalafix, and Scalafmt pass.
Raw logs are `target/benchmark-scout/quiet-ptx-parameter-loop-{a2,b1,b2}-*.log`; the recording is
`target/benchmark-jfr/quiet-ptx-post-scalar-20260923.jfr`.

## PTX lane loops and scalar memory effects (2026-09-23)

Two follow-up profiles showed that successful scalar memory instructions still paid for boxed lane arguments,
per-lane `Right(())` values, and object-backed global-memory effects. The retained changes now compute thread
coordinates directly from the validated launch dimensions, read compiled scalar operands without the fault-aware
lookup wrapper, execute successful parameter and scalar memory loops directly over the active mask, and stage scalar
global writes in reusable primitive arrays. Memory faults are still detected before any staged write is committed.

The sequence below uses pinned CPU 4. Values are medians from five measured runs; the final memory-effect timing is
shown as a range because its immediate run and post-rejection bracket differed while allocation was stable.

| Probe | Indexed parameter loop | Direct coordinates | Direct operands | Direct lane loops | Primitive memory staging |
| --- | ---: | ---: | ---: | ---: | ---: |
| CUDA CloverLeaf, 64 cells | 71.10 us / 131.3 KB | 72.51 us / 140.1 KB | 70.40 us / 81.1 KB | 67.85 us / 55.1 KB | 64.36--65.76 us / 37.2--47.7 KB |
| PTX vector, 4,096 lanes | 1.179 ms / 2.328 MB | 1.091 ms / 1.882 MB | 1.017 ms / 999.3 KB | 0.979 ms / 503.6 KB | 0.981--1.029 ms / 274.0 KB |

Relative to the indexed-loop state, the current CUDA path is about 7.5--9.5% faster with 64--72% less allocation.
The large probe is about 13--17% faster with about 88% less allocation. The direct-coordinate change alone slightly
regressed the small case, but retained a material large-workgroup gain; the following operand and loop changes more
than recovered the small-case loss. The primitive memory stage is retained primarily for its stable allocation
reduction: large-case timing was neutral in its immediate run and roughly 5% slower in the later noisy bracket.

Focused interpreter, cross-workgroup, arithmetic, and state-effect tests pass. The state-effect test exercises the
new primitive memory path and confirms that an invalid write prevents an earlier staged register update from
committing. Raw logs are `target/benchmark-scout/quiet-ptx-{thread-coordinate,direct-operands,direct-lanes,primitive-memory}*`;
post-change recordings are `target/benchmark-jfr/quiet-ptx-{direct-operands,direct-lanes}-20260923.jfr`.

## SPIR-V typed scalar execution and invocation reuse (2026-09-23)

The pinned scalar arithmetic baseline spent 36% of JFR CPU samples in `InvocationValues.scalarType` and took a
median 95.42 us with about 89.45 KB of caller-thread allocation per dispatch. The loaded program now predecodes
scalar integer add, subtract, and multiply instructions, including constant operands and result masks. The fast path
uses one fused scalar availability/value lookup and falls back to the checked generic instruction path for missing or
vector operands.

Sequential, non-collective workgroups now reset their completed invocation object and coordinate arrays together with
the existing reusable register file. Collective and parallel workgroups retain distinct invocation state. Kernels
without arguments also skip the otherwise repeated empty `zipWithIndex` and map construction, and non-collective
groups do not allocate an unused execution buffer. The measured progression was:

| State | Median time per dispatch | Caller-thread allocation |
| --- | ---: | ---: |
| Baseline | 95.42 us | 89.45 KB |
| Typed scalar instructions | 37.02 us | 87.45 KB |
| Fused scalar read | about 37--38 us | 85.38 KB |
| Reused invocation state | 28.54 us | 51.65 KB |
| Empty-argument and buffer fast paths | 27.40 us | 27.06 KB |

The final scalar probe is about 71% faster with about 70% less allocation than the baseline. The multigroup OpenCL
async-copy fixture exercised the collective path and invocation reset across groups; every dispatch retained checksum
332544. Its measured samples were noisy and are recorded as correctness evidence rather than a speed comparison.
All 66 SPIR-V JVM tests passed, Scala Native compiled, and Scalafix and Scalafmt passed. Raw logs are
`target/benchmark-scout/quiet-spirv-redesign-{a1,b1,b2,b3,b4,b5}*`; profiles are
`target/benchmark-jfr/quiet-spirv-redesign-{a1,b1,b3}.jfr`.

## AMDGPU address resolution and carry input (2026-09-23)

Fresh pinned baselines measured HIP CloverLeaf at 125.26 us and 475.15 KB per dispatch, HSA vector copy at 11.10 us
and 52.45 KB, and the 512-operation ALU probe at about 330 us and 31 KB. HIP JFR attributed about 16% of CPU samples
to the benchmark address space's `view.collectFirst` pipeline and showed its closures, iterators, boxed longs, and
locations among leading allocation sites. The benchmark address space now uses an indexed scan where available; the
server uses the same allocation-light search shape over its session allocation iterator. HIP then measured 95.08 us
and 278.64 KB, while HSA measured 10.10 us and 42.27 KB. The exact fixture and server containers differ, so the
benchmark result establishes the search algorithm's value rather than a server-wide speedup.

The cleaned profile showed carry-producing vector instructions resolving the same scalar carry source through
`Option.exists` once per active lane. Add, subtract, and reverse-subtract now read that invariant mask once per
instruction. In a restore bracket, the retained form moved HIP from 92.05 to 89.83 us, HSA from 10.63 to 10.05 us,
and the ALU median from 320.67 to 273.49 us. HIP allocation rose from 324.72 to 329.65 KB; HSA allocation fell from
48.21 to 42.10 KB. The small HIP allocation cost is retained for the repeated throughput gain and the improvement on
the other probes.

Finally, the HIP and HSA fixtures now override scalar reads and writes directly, matching the production server
memory implementation instead of profiling the generic temporary-byte-vector fallback. With the retained interpreter
change, the aligned fixtures measured 82.77 us and 300.75 KB for HIP and 9.52 us and 42.88 KB for HSA. Every sample
passed its checksum. The complete AMDGPU suite passed 337 JVM tests, the server suite passed 55, AMDGPU and server
Scala Native compiled, and the affected Scalafix and Scalafmt gates passed. The code-object ownership scout retained
the existing design: immutable ELF bytes and parsed tables are cached, while each session gets lazy mutable global
images. Raw logs are `target/benchmark-scout/quiet-amdgpu-{redesign,address-loop,carry-hoist,direct-fixture}*` and the
profiles are `target/benchmark-jfr/quiet-amdgpu-redesign-hip-{a1,b1}.jfr`.

## AMDGPU lane and scalar-memory hot paths (2026-09-23)

A fresh CPU-4 HIP profile showed the remaining cost concentrated in 64-bit floating-point lane execution, global
loads and stores, mask-producing arithmetic, and external address resolution. The retained changes keep the existing
instruction representation and public memory API:

- `VBinary64Modified` uses an indexed active-lane loop, retains destination arrays once, and resolves scalar,
  constant, and literal operands once per instruction. HIP reached 82.69 us and 278.06 KB; HSA reached 8.90 us and
  37.12 KB.
- Scalar `GlobalLoad` uses an indexed lane loop and resolves invariant address and width inputs once. HIP reached
  79.19 us and 214.99 KB. HSA remained close to its prior timing at 9.13 us and 36.94 KB.
- Scalar global stores write directly into internal images or through `Memory.writeValue`, avoiding a temporary byte
  array that was unused for external memory. HIP reached 74.97 us and 208.08 KB; HSA reached 8.91 us and 35.54 KB.
- `VAddCarry` reuses one per-wave primitive mask holder. This preserves the JIT-friendly closure shape while avoiding
  the per-instruction boolean array and its second reduction pass. A final confirmation measured HIP at 70.76 us and
  208.43 KB and HSA at 8.82 us and 35.18 KB.

Relative to the aligned 82.77 us / 300.75 KB HIP state at the end of the previous pass, the final pinned result is
about 14.5% faster with about 30.7% less caller-thread allocation. HSA moved from 9.52 us / 42.88 KB to 8.82 us /
35.18 KB, about 7.4% faster with about 18.0% less allocation. Every measured sample passed its checksum.

Several broader versions were rejected. An iterative `GlobalStore` loop regressed HIP to 84.58 us even though it
reduced allocation. Moving the large generic `VBinary` body into an explicit loop damaged JIT compilation and took
more than 440 us; even retaining only its destination array raised the median to 81.83 us. Reusing the mask holder
for every carry, compare, division-scale, and multiply-add producer improved HSA allocation but moved HIP back to
74.32 us. A compare-only follow-up made HSA slower and more allocation-heavy. All were restored, leaving only the
narrow measured paths above. Raw logs are
`target/benchmark-scout/quiet-amdgpu-{vbinary64,globalload,direct-write,mask-accumulator}*`; the latest retained-path
profiles are `target/benchmark-jfr/quiet-amdgpu-{globalload-d3,direct-write-d4b}.jfr`. The final source passed all 310
AMDGPU JVM tests and all three benchmark tests, compiled for Scala Native, and passed the AMDGPU Scalafix gate.
The final retained-state recording is `target/benchmark-jfr/quiet-amdgpu-final-20260923.jfr`; it measured 68.90 us
and 204.37 KB under JFR. Its remaining leading costs are generic lane-operation lambdas, runtime refinement, and
external address translation. Address translation also accounts for much of the remaining `Location`, boxed `Long`,
`Right`, and `Either.cond` allocation, so the next substantial AMDGPU step needs a measured address-space API or
translation-cache design rather than another broad rewrite of the instruction switch.

## Combined address translation and memory access (2026-09-23)

`AddressSpace.locate` previously forced every external lane access to allocate an `AddressSpace.Location` and its
successful `Either` before calling `Memory`. `AddressSpace` now provides combined byte, scalar, and atomic operations
with backwards-compatible defaults. Address-space owners can override that boundary and resolve plus access their
storage in one pass. AMDGPU and PTX use the combined operations; `ExecutionService.CompletionMemory` implements them
with an inline allocation lookup, while `locate` remains available to existing callers.

The benchmark address space uses its fixed address stride for an O(1) candidate lookup, falls back to an indexed
scan for noncanonical tables, and crosses one `assume` boundary only after validating the complete byte range. The
HSA fixture retains its three regions in an indexed immutable vector. The pinned results were:

| Probe | Previous retained state | Combined address access |
| --- | ---: | ---: |
| HIP CloverLeaf, 128 cells | 70.76 us / 208.43 KB | 64.60 us / 125.63 KB |
| HSA vector copy, one group | 8.82 us / 35.18 KB | 8.18 us / 28.60 KB |

HIP is about 8.7% faster with 39.7% less caller-thread allocation; HSA is about 7.2% faster with 18.7% less
allocation. The first generic indexed implementation reduced HIP allocation to 153.17 KB but regressed its median
to 74.24 us. Exploiting the address-space layout recovered the throughput, and removing redundant runtime refinement
reduced allocation by another 28.6 KB. Every measured sample passed its checksum.

The final JFR recording, `target/benchmark-jfr/quiet-amdgpu-combined-address-a4.jfr`, no longer contains
`AddressSpace.Location` in its allocation table; runtime refinement fell from 15.25% to 2.35% of CPU samples after
the checked proof boundary moved into the resolver. Raw measurements are
`target/benchmark-scout/quiet-amdgpu-combined-address-{a1,a2,a3-hsa,a4}*`.
The shared change passed 310 AMDGPU tests, 90 PTX tests, 43 server tests, and all three benchmark tests. Core,
AMDGPU, PTX, and server Scala Native compilation and all affected Scalafix gates passed.

## Accepted

- PTX program preparation caches immutable parsed programs and per-instance lowering. The
  `ptx-program-lifecycle` benchmark keeps this path independently measurable.
- PTX register lookup uses precomputed indices. `ptx-register-index` reports zero allocation and roughly 3--4 ns per
  named lookup on the development machine.
- AMDGPU internal-region lookup uses an indexed loop, and scalar internal reads decode from the backing array.
- AMDGPU address resolution uses allocation-light scans, and carry-producing vector instructions read invariant
  input masks once per instruction.
- AMDGPU 64-bit vector arithmetic and scalar global loads use indexed lane loops with invariant operand resolution;
  scalar global stores avoid temporary byte arrays, and `VAddCarry` reuses a per-wave primitive result-mask holder.
- Address spaces can combine translation with byte, scalar, or atomic memory access. AMDGPU, PTX, the production
  execution service, and the benchmark fixtures use this boundary to avoid intermediate locations and nested success
  values.
- SPIR-V predecodes common scalar integer instructions and reuses completed sequential invocation state.
- New SPIR-V invocation files use their immutable global fallback map without copying it into dense local slots.
- PTX reuses one bounded block state across dispatches and sizes dense register directories per kernel.
- PTX stages scalar register effects in reusable parallel arrays with primitive lane/value storage while retaining
  validation-before-commit semantics for memory effects.
- PTX integer multiply-add uses portable limb arithmetic instead of unsigned decimal strings and `BigInt`.
- Complete PTX launches reuse their all-lanes mask, and parameter loads walk predecoded destinations by index.
- PTX reads compiled scalar memory operands directly, uses allocation-free active-lane loops for parameter and scalar
  memory instructions, and stages scalar global-memory writes in reusable primitive arrays.
- PTX thread coordinates use direct arithmetic over validated launch dimensions.
- Single-workgroup SPIR-V collective values omit reuse-only touched-slot storage, and unchanged-pointee pointer
  bitcasts reuse their pointer value.
- AMDGPU atomics use the indexed internal-region lookup measured by `amdgpu-atomic`.
- PTX parser fragments that do not depend on the current operand are shared instead of rebuilt. On the generated
  64-entry fixture this reduced median decode time from roughly 2.96 ms to 2.85 ms.
- Parsed PTX tokens cross one documented, parser-internal refinement proof boundary. Together with the generated
  instruction decoder's allocation-light two-pass selection and static directive/instruction tails, the
  64-entry/16-instruction fixture moved from roughly 7.09 ms and 49.44 MB per decode to 5.70 ms and 43.43 MB per
  decode.
- Program-image caches use `ByteKey`, which computes a full content hash once while retaining full content equality.
  The `program-image-key` benchmark keeps the raw and cached behaviour visible. A 4 MiB raw key took roughly 3.13 ms
  per hash-map lookup; a reused cached key was constant-time. This addresses repeated hashing within LRU misses seen
  in CUDA and AMDGPU JFR recordings.
- The full 1,818-case CUDA route passed with 32 workers in 373.3 seconds, down from 424.7 seconds in the identical
  immediately preceding JFR run (12.1%). Compared with the original 703-second recording, collection-growth samples
  fell from 14,533 to 4,997 and full-image hashing samples from 13,697 to 4,637. The original recording used a
  different run context, so its raw wall-clock duration is not a strict before/after benchmark.
- `Parser` is the cursor-based PTX parser used by `VMLoadedProgram` and the public parsing entry point. On the
  377,535-byte, 256-entry/64-instruction fixture, cold decode fell from roughly 62.5 ms and 466 MB to 7.7--11.0 ms
  and 38 MB.
- Concurrent unit instruction-budget reservations use `AtomicLong.getAndIncrement`; batched costs and the
  `Long.MaxValue` boundary retain the CAS implementation. The exact shared limit is unchanged. In the 256-workgroup,
  512-instruction SPIR-V benchmark at 32 workers, dispatch time fell from roughly 5.1--7.1 ms to 3.2--4.4 ms.
- SPIR-V invocation reset tracks dirty dense slots in a primitive array and its scalar arithmetic path reuses the
  interpreter's success value. On the same benchmark at one worker, caller-thread allocation fell from roughly 4.0 MB
  to 0.35 MB per dispatch; timings were broadly neutral, so the change remains for its simpler storage model and lower
  allocation pressure.
- With the cursor-based `Parser` selected, the full README CUDA route passed all 1,818 cases in 143.0 seconds. This is an acceptance
  result rather than a strict comparison with the 373.3-second JFR run: profiling was disabled and host load can differ.
- With the concurrent-budget and SPIR-V allocation changes, the README OpenCL route passed all 383 cases in 180.5
  seconds. The earlier recorded route took about 317 seconds, but this is likewise directional rather than a controlled
  comparison because profiling and host load differ.
- The complete seven-route conformance profile passed 9,803 cases in 702.105 route-seconds under JFR: PTX/CUDA took
  142.212 seconds, the three AMDGPU routes took 262.657 seconds, and the three SPIR-V routes took 297.236 seconds. A
  hot-loop `Either[ExecutionFailure, Unit]` match now accepts `Right(_)` rather than testing Unit equality on every
  instruction. This removed `java.util.Objects.equals` from the SPIR-V hot-method tables; `resumeInvocation` remains
  the dominant interpreter cost. These timings combine the initial green PTX/AMDGPU recordings with post-fix SPIR-V
  reruns and are profiling observations, not stable performance thresholds.

## Rejected experiments

- A reusable checked-memory read result added API and mutable state without reducing allocation. The 4,096-lane
  median regressed from about 1.017 ms to 1.099 ms, so it was removed.
- An `ArrayCheckedMemory.validateRange` bulk override improved the small CUDA probe but made the large probe slower or
  more allocation-heavy across two variants. The trait fallback was restored.
- Inlining an allocation-free multiply-add result callback expanded the PTX fast path enough to move CUDA from about
  64 us to 346--349 us per dispatch. Its small allocation reduction did not justify the code-shape regression, and
  the tuple-returning helper was restored.

- Writing PTX kernel parameters directly into lane registers sometimes improved the 4,096-lane probe by 2--3%, but
  CUDA timing overlapped its baseline and allocation rose by about 20--23 KB per dispatch. Vector allocation also
  varied from 0.29 MB lower to 0.20 MB higher. The staged path was restored.
- Passing dispatch, workgroup, and private value maps separately avoided per-invocation map merges but did not
  improve pinned allocation (about 2.271 MB versus 2.267 MB per dispatch after register-file reuse). It was reverted.
- Replacing `InvocationValues.get`'s `Option.orElse` chain with direct branches passed correctness checks but raised
  pinned allocation from about 1.64 MB to 1.86 MB per dispatch and slowed the measured run. It was reverted.
- Rewriting broad portions of the PTX grammar into static combinator trees increased grammar complexity without a
  measurable improvement.
- Replacing the small register-part predicate with an imperative character loop regressed decode time by roughly 9%
  without a meaningful allocation reduction.

## Deferred redesigns

- Content-addressed caching still requires one full image hash at each external load boundary. Eliminating that scan
  requires carrying an authenticated digest or a stable program-image identity from RPC decoding through the ISA
  loaders, rather than weakening cache equality.
- SPIR-V still performs one atomic budget update per concurrent instruction. Leasing could reduce contention further,
  but a correct design must neither overshoot the limit nor reject while another worker holds unused credits. The
  simple batching variants violate that invariant and remain rejected until there is a bounded reclamation design.
- AMDGPU and SPIR-V interpreter loops dominate their respective routes. Further gains belong in opcode-specific
  execution benchmarks and should remain confined to proven hot paths.
- The recorded AMDGPU vector-copy fixture took roughly 1.20--1.26 ms and allocated 1.24 MB per 64-workgroup dispatch.
  Its JFR profile attributed 55.7% of CPU samples to instruction execution. The former partial packed/fallback model
  has since been removed in favour of one canonical `VMIR.Instruction`; future optimization should begin from fresh
  measurements of that representation rather than recreate a second instruction hierarchy.

## Full-profile diagnostic (2026-09-17)

The 32-worker, 96 GiB-heap run covered a much larger and different workload than the earlier 9,803-case profile and
is not a before/after benchmark. Six routes returned status 8; HSA was the only green route and contained only two
tests, so its 1.340-second recording is not representative. The measured route results were:

| Route | Tests | Failed | Route seconds | JFR GC pause | Peak post-GC live heap |
| --- | ---: | ---: | ---: | ---: | ---: |
| CUDA | 10,511 | 10 | 905.177 | 269 s (29.7%) | 78.50 GiB |
| HIP | 10,879 | 2 | 484.564 | 42.4 s (8.8%) | 14.98 GiB |
| HIP-HSA | 10,879 | 5 | 629.477 | 63 s (10.0%) | 12.32 GiB |
| HSA | 2 | 0 | 1.340 | 26.8 ms | 0.03 GiB |
| OpenCL | 604 | 45 | 1,133.963 | 25.2 s (2.2%) | 19.33 GiB |
| SYCL-OpenCL | 10,739 | 37 | 234.367 | 3.63 s (1.5%) | 1.24 GiB |
| SYCL-Level Zero | 10,739 | 32 | 249.281 | 4.79 s (1.9%) | 1.93 GiB |

Total route time was 3,638.169 seconds (60.64 minutes). Failed and timed-out tests are included in these numbers;
JFR overhead, concurrent client processes and workload differences also prevent comparison with the earlier green
profile.

### JFR observations

- SPIR-V execution is the clearest actionable hotspot. `resumeInvocation` accounts for 50.63% of OpenCL, 50.69% of
  SYCL-OpenCL and 48.71% of SYCL-Level Zero CPU samples. Allocation samples are led by immutable-map lookup nodes
  (19.64--20.73%), followed by lambda/object allocation (13.10--16.95%), `Some`, boxed integers, `Right`, runtime
  values and invocation state. Summing the maximum per-thread allocation counters gives lower bounds of 19.50 TiB
  for OpenCL and 3.01/2.99 TiB for the two SYCL routes, about 17.6/13.2/12.4 GiB per second; short-lived threads missed
  between statistics samples make these conservative. Prioritize an allocation benchmark around
  `resumeInvocation`, then replace measured hot-path maps/options/boxed runtime values with reusable dense or
  primitive state while preserving the current instruction-limit semantics.
- CUDA splits CPU between expected emulation (`executeFast`, 22.14%) and avoidable preparation: cursor lookup plus
  tokenization is 20.53%, full-image hashing plus byte-array equality is 14.03%, and input reads plus iterable copies
  are 12.40%. The 1,400 young pauses total 269 seconds (192 ms average, 1.61 seconds maximum), and post-GC live data
  reaches 78.50 GiB before falling to 7.93 GiB at shutdown. Stable program identity/digests across the RPC/load
  boundary remain the highest-value structural fix; independently benchmark token lookup/tokenization and bound
  concurrent prepared-program retention before changing parser semantics or cache equality.
- HIP and HIP-HSA confirm that the AMDGPU instruction loop is the main emulator cost (19.06% and 21.07%). Hash/fold work contributes
  roughly 20.22% and 17.70% of CPU samples. Allocation pressure is dominated by `copyAs`, byte `copyOfRange`, byte
  array construction and `copyOf` (59.73% on HIP and 55.03% on HIP-HSA), with socket reads adding 14.01% and 16.99%.
  Measure the RPC-to-code-object ownership path and benchmark removal or coalescing of proven redundant copies; do not
  weaken the session-private mutable-global isolation of cached code objects.
- The server creates one platform `gemstone-rpc-*` thread per short-lived connection: 10,512 starts on CUDA, 18,597
  on HIP, 18,743 on HIP-HSA, 10,741 on SYCL-OpenCL and 9,179 on SYCL-Level Zero, while peak live-thread counts remain
  only 72--104. This is secondary because thread operations do not lead CPU/allocation tables, but a bounded or
  virtual connection-handler experiment is justified if an isolated connection-churn benchmark shows a gain.
- The emulator and clients saturate the host: average machine CPU is 76% for CUDA, 88--92% for AMDGPU and 94--95%
  for the SYCL routes; JVM CPU alone is about 51%, 27--36% and 71%, respectively. `executeFast`, the AMDGPU instruction loop and
  `resumeInvocation` therefore represent real software-emulation work, and stress/BLAS cases plus 32-way client
  contention are workload costs rather than evidence that scheduler parallelism should simply be increased.
