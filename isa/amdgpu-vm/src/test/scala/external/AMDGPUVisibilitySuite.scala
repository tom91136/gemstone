package external

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.amdgpu.asm.*
import net.kurobako.gemstone.testing.Suite
import scala.compiletime.testing.typeCheckErrors

class AMDGPUVisibilitySuite extends Suite {
  test("Program loading remains public while its state and ELF storage remain internal") {
    assertEquals(
      typeCheckErrors("""
      import net.kurobako.gemstone.isa.amdgpu.*
      def load(program: CodeObject): LoadedProgram = LoadedProgram.load(program)
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
