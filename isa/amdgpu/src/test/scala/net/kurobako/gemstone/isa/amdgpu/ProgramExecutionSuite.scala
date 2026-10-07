package net.kurobako.gemstone.isa.amdgpu

import fixtures.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*

class ProgramExecutionSuite extends munit.FunSuite {
  test("the code-object decoder rejects malformed input before VM loading") {
    assertEquals(
      CodeObject.decode(Vec.empty).left.map(_.message),
      Left[NonEmpty[String], Nothing]("expected a little-endian ELF64 image")
    )
  }

  test("concurrent loads of one code-object program isolate relocated globals") {
    val program = CodeObject.decode(Vec.unsafeWrapArray(minimalCodeObject(globalPointer = true))).value
    scala.util.Using.resource(threads.Pool(4)) { pool =>
      val pending = Vec.fill(8)(pool.executor.submit { () =>
        val loaded = LoadedProgram.load(program)
        val _      = loaded.symbols.value
        loaded
      })
      val programs = pending.map(_.get(5, java.util.concurrent.TimeUnit.SECONDS))
      val first    = programs.head
      assertEquals(first.writeGlobal("device_global", 4L, Vec[Byte](9, 8, 7, 6)), Right(()))
      assertEquals(first.global("device_global").value.initial.slice(4, 8), Vec[Byte](9, 8, 7, 6))
      programs.tail.foreach { program =>
        assertEquals(program.symbols, first.symbols)
        assertEquals(program.global("device_global").value.initial.slice(4, 8), Vec[Byte](0, 0, 0, 0))
      }
    }
  }

  test("code-object loading defers malformed section-table failures until first launch") {
    val header = Array.ofDim[Byte](64)
    header(0) = 0x7f.toByte
    header(1) = 'E'.toByte
    header(2) = 'L'.toByte
    header(3) = 'F'.toByte
    header(4) = 2
    header(5) = 1
    header(18) = 224.toByte
    val target = "amdgcn-amd-amdhsa--gfx900\u0000".getBytes(java.nio.charset.StandardCharsets.US_ASCII)
    Array.copy(target, 0, header, 24, target.length)
    val program = CodeObject.decode(Vec.unsafeWrapArray(header)).value

    val loaded = LoadedProgram.load(program)
    assertEquals(
      loaded.execute(singleTask(), RejectingMemory(), TaskControl(), _ => Right(())),
      Left(Fault.InvalidRequest("invalid ELF section table"))
    )
  }
}
