# Capture and replay

The endpoint records authenticated request/response frames, payload blobs, callback traffic,
connection/client lifetime and the resolved execution profile. The archive is versioned and
bounded. SHA256 checks detect corruption; they do not authenticate the archive's author.
Protocol credentials are redacted. Application data remains part of a capture.

```console
sbt --server --batch 'server/run serve --endpoint-file target/endpoint.json --record target/session.gmr'
sbt --server --batch 'server/run shutdown --port <port> --token <token>'
sbt --server --batch 'server/run inspect target/session.gmr'
sbt --server --batch 'server/run replay target/session.gmr'
sbt --server --batch 'server/run step target/session.gmr'
```

The Native executable produced by `serverNative/nativeLink` accepts the same commands.
Bare `--endpoint-file` server invocation remains supported. `run --api ... -- <application>`
uses the existing application launcher; `GEMSTONE_RECORD` selects its server-owned archive.
The archive setting is removed from the child's environment. The former native JSON metadata
writer is retired.

Shut down cleanly to complete the footer. Replay rejects incomplete, corrupt or explicitly
unreplayable captures before creating an endpoint. Files can contain input programs and data;
store them with the same care as those inputs.

Archive version 2 records the CUDA context attached to each request. Version 1 captures predate
context isolation and are rejected rather than replayed with guessed ownership.

## Semantics

Replay creates fresh credentials and process-scoped state, preserving recorded connections and
send/receive ordering. It starts with the recorded instruction limit, quantum and worker count;
a host unable to support that profile fails clearly.

Callback replies are replayed. External application callbacks are not invoked again; their
recorded nested RPCs still execute. Pending CUDA callbacks must be registered and drained through their
originating CUDA connection; a different connection receives an explicit unsupported error. Closing the owner
retires its callbacks. Ordinary CUDA calls on other connections remain available. Pending/completed polling differences are handled explicitly,
while terminal status and data mismatches fail validation.

Inspection validates each distinct payload once and retains bounded frame metadata for repeated references.
Replay rechecks full payload hashes when reading frames.

Inspection and stepping operate at recorded API events. This is not reverse instruction execution
or whole-process rollback. Framing records cannot force the original backend thread linearization;
data races or uncontrolled effects can produce a reported divergence.
