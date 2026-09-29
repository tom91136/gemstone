package external

import net.kurobako.gemstone.collection.*
import scala.compiletime.testing.typeCheckErrors

class AMDGPUVisibilitySuite extends munit.FunSuite {
  test("Program loading remains public while its state and ELF storage remain internal") {
    assertEquals(
      typeCheckErrors("""
      import net.kurobako.gemstone.isa.amdgpu.*
      import net.kurobako.gemstone.vm.*
      def load(program: CodeObject): Either[Fault, LoadedProgram] = LoadedProgram.load(program)
    """),
      Nil
    )
    assert(typeCheckErrors("net.kurobako.gemstone.isa.amdgpu.VMState.LaneBits(32, 0L)").nonEmpty)
    assert(
      typeCheckErrors(
        "net.kurobako.gemstone.isa.amdgpu.ELF.index(net.kurobako.gemstone.collection.Vec.empty[Byte])"
      ).nonEmpty
    )
  }
}
