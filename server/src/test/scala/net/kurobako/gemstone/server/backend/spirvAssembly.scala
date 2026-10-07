package net.kurobako.gemstone.server.backend

import munit.Assertions.fail
import munit.Location
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.spirv.*

/** OpenCL SPIR-V kernels for server tests, written as assembly and encoded to the binary the service loads. */
private object spirvAssembly {
  val openCL: String =
    """OpCapability Addresses
      |OpCapability Kernel
      |OpCapability Int8
      |OpCapability Int64
      |%opencl = OpExtInstImport "OpenCL.std"
      |OpMemoryModel Physical64 OpenCL
      |""".stripMargin

  def image(source: String)(using Location): Vec[Byte] = {
    val module = asm.Parser.parse(source).fold(error => fail(s"$error in\n$source"), identity)
    ISA.encode(module).fold(error => fail(error.message), identity)
  }
}
