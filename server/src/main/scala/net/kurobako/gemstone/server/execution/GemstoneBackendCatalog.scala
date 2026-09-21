package net.kurobako.gemstone.server.execution

import net.kurobako.gemstone.isa.spirv.v1_6.Validation

/** Product composition for the ISA runtimes linked into Gemstone. */
private[server] object GemstoneBackendCatalog {
  val default: BackendCatalog = BackendCatalog.from(
    PTXRuntimeBackend,
    HSARuntimeBackend,
    SPIRVRuntimeBackend(Validation.Environment.OpenCLCompute)
  )
}
