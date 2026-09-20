package net.kurobako.gemstone.server.execution

import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.execution.ProgramDecoder
import net.kurobako.gemstone.isa.amdgpu.HSACodeObjectDecoder
import net.kurobako.gemstone.isa.ptx.v9_3.ModuleDecoder
import net.kurobako.gemstone.isa.spirv.v1_6.SPIRVProgramDecoder

/** Product composition for the ISA components linked into Gemstone. */
private[server] object GemstoneProgramRegistry {
  private val decoders =
    Vec(
      ModuleDecoder,
      HSACodeObjectDecoder,
      SPIRVProgramDecoder
    )

  def apply(): ProgramRegistry =
    ProgramRegistry.validated(decoders)
      .fold(message => throw IllegalStateException(message), value => value)
}
