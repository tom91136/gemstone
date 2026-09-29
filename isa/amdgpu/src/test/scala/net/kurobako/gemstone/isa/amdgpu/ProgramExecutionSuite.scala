package net.kurobako.gemstone.isa.amdgpu

import fixtures.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*

class ProgramExecutionSuite extends munit.FunSuite {
  test("loading one code-object program twice isolates writable global images") {
    val program = CodeObject.decode(Vec.unsafeWrapArray(minimalCodeObject())).toOption.get

    val first                  = LoadedProgram.load(program).toOption.get
    val second                 = LoadedProgram.load(program).toOption.get
    val name: NonEmpty[String] = "device_global"

    assertEquals(first.writeGlobal(name, 0L, Vec[Byte](1, 2, 3, 4)), Right(()))
    assertEquals(first.global(name).map(_.initial.take(4)), Right(Vec[Byte](1, 2, 3, 4)))
    assertEquals(second.global(name).map(_.initial.take(4)), Right(Vec[Byte](0, 0, 0, 0)))

  }

  test("the code-object decoder rejects malformed input before VM loading") {
    assertEquals(
      CodeObject.decode(Vec.empty).left.map(_.message),
      Left[NonEmpty[String], Nothing]("expected a little-endian ELF64 code object")
    )
  }
}
