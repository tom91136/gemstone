# Phase 0 feasibility findings

## Working

- Scala Native 0.5.12 links `libgemstone-core.so` under sbt 2.0.6.
- A generated `libcuda.so.1` contains only C facade code and links to one process-wide `libgemstone_dispatch.so`.
- The core exports only `ScalaNativeInit` and `gemstoneStartServer`; vendor symbols do not expose Scala implementation symbols.
- The generated GNU ld scripts enforce explicit facade and core symbol versions.
- Null pointer validation occurs in C before message dispatch.
- Scala exceptions are contained in the core server loop and converted to protocol status values.
- The shared execution model and PTX vector-add suites pass on JVM and Native.
- The transport recorder emits normalized JSON-lines request, response, and boundary-failure records.

## Resolved: arbitrary foreign threads

The original all-Scala DSO crashed when application-created threads entered a pointer-taking Scala export. Forcing
Scala Native multithreading, changing GC, and calling `ScalaNativeInit` from each thread did not attach those threads to
the runtime.

The replacement path is:

```text
foreign thread -> generated C facade -> pure-C queue -> Scala Native-owned server thread
```

`conformance/smoke/concurrent-foreign-entry.cpp` now executes 16,000 calls from eight C++ threads. No foreign
thread enters Scala, and the test passes under the default Immix collector.

The Scala Native runtime is initialized by the normal DSO loader thread. `gemstoneStartServer` creates the managed server
thread and then marks the loader thread unmanaged before returning to ordinary application code. This currently uses
the internal `scalanative_GC_set_mutator_thread_state` runtime symbol and therefore needs an explicit compatibility test
whenever Scala Native is upgraded.

## Remaining Phase 0 work

- Repeated `dlopen`/`dlclose` and clean server shutdown.
- Loading several generated vendor DSOs while retaining one dispatcher and one core runtime.
- Reverse messages and callbacks into host code.
- `fork` classification and post-fork behaviour.
- Dual mappings, protection changes, and checked native faults.
- An out-of-process transport implementing the same generated protocol.
