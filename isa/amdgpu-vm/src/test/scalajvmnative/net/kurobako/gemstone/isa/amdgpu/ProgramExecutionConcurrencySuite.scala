package net.kurobako.gemstone.isa.amdgpu

import fixtures.*
import java.util.concurrent.TimeUnit
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*
import scala.util.Using

class ProgramExecutionConcurrencySuite extends Suite {
  test("concurrent loads of one code-object program isolate relocated globals") {
    val program = CodeObject.decode(Vec.unsafeWrapArray(minimalCodeObject(globalPointer = true))).value
    Using.resource(threads.Pool(4)) { pool =>
      val pending = Vec.fill(8)(pool.executor.submit { () =>
        val loaded = LoadedProgram.load(program)
        val _      = loaded.symbols.value
        loaded
      })
      val programs = pending.map(_.get(5, TimeUnit.SECONDS))
      val first    = programs.head
      val zeros    = Vec[Byte](0, 0, 0, 0)
      assertEquals(first.writeGlobal("device_global", 4L, Vec[Byte](9, 8, 7, 6)), Right(()))
      assertEquals(first.global("device_global").value.initial.slice(4, 8), Vec[Byte](9, 8, 7, 6))
      programs.tail.foreach { program =>
        assertEquals(program.symbols, first.symbols)
        assertEquals(program.global("device_global").value.initial.slice(4, 8), zeros)
      }
    }
  }
}
