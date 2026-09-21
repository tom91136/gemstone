# Performance ledger

This file records measured hotspots and experiments that are too invasive to pursue casually. Numbers are local JVM
measurements, not release thresholds; compare like-for-like runs on the same machine.

## Accepted

- PTX program preparation caches immutable parsed programs and per-instance lowering. The
  `ptx-program-lifecycle` benchmark keeps this path independently measurable.
- PTX register lookup uses precomputed indices. `ptx-register-index` reports zero allocation and roughly 3--4 ns per
  named lookup on the development machine.
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
