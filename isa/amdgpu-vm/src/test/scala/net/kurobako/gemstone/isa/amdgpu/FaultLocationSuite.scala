package net.kurobako.gemstone.isa.amdgpu

import fixtures.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

class FaultLocationSuite extends Suite {
  test("a faulting instruction reports its kernel, byte offset, workgroup and lone lane") {
    val operations = Vec(VMIR.Instruction.Wait, VMIR.Instruction.Wait, VMIR.Instruction.Trap(1))
    assertEquals(
      runIR(operations, RegionMemory(), codeKernel(name = "trapping")).left.map(_.location),
      Left(Some(Fault.Location("trapping", None, Some(8L), Some(Coordinates.origin), Some(0))))
    )
  }
}
