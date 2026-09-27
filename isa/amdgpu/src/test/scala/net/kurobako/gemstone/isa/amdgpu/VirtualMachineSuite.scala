package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.Constraint.NonNegative
import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.Constraint.NonEmptyString
import net.kurobako.gemstone.vm.Observer
import net.kurobako.gemstone.vm.Scheduler

final class VirtualMachineSuite extends munit.FunSuite with CodeObjectTestSupport {
  test("loading one code-object program twice isolates writable global images") {
    val program = CodeObject.Decoder.decode(Vec.unsafeFromArray(minimalCodeObject())).toOption.get
    val machine = VirtualMachine(Observer.discard, Scheduler.Sequential)
    val first   = machine.load(program).toOption.get
    val second  = machine.load(program).toOption.get
    val name    = NonEmptyString("device_global")

    assertEquals(machine.writeGlobal(first, name, 0L, Vec[Byte](1, 2, 3, 4)), Right(()))
    assertEquals(machine.global(first, name).map(_._2.take(4)), Right(Vec[Byte](1, 2, 3, 4)))
    assertEquals(machine.global(second, name).map(_._2.take(4)), Right(Vec[Byte](0, 0, 0, 0)))

    machine.unload(first)
    machine.unload(second)
  }

  test("the code-object decoder rejects malformed input before VM loading") {
    assertEquals(
      CodeObject.Decoder.decode(Vec.empty).left.map(_.message),
      Left("expected a little-endian ELF64 code object")
    )
  }
}
