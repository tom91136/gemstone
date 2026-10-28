package net.kurobako.gemstone.isa.amdgpu

import fixtures.*
import java.nio.charset.StandardCharsets
import net.kurobako.gemstone.*
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

  test("code-object loading defers malformed section-table failures until first launch") {
    val header = Array.ofDim[Byte](64)
    header(0) = 0x7f.toByte
    header(1) = 'E'.toByte
    header(2) = 'L'.toByte
    header(3) = 'F'.toByte
    header(4) = 2
    header(5) = 1
    header(18) = 224.toByte
    val target = "amdgcn-amd-amdhsa--gfx900\u0000".getBytes(StandardCharsets.US_ASCII)
    Array.copy(target, 0, header, 24, target.length)
    val program = CodeObject.decode(Vec.unsafeWrapArray(header)).value

    val loaded = LoadedProgram.load(program)
    assertEquals(
      loaded.execute(singleTask(), TaskContext(RejectingMemory(), TaskControl(), _ => Right(()))),
      Left(Fault.InvalidRequest("invalid ELF section table"))
    )
  }

  private def loaded(code: Array[Byte]): LoadedProgram =
    LoadedProgram.load(CodeObject.decode(Vec.unsafeWrapArray(
      elfImage.amdgpu(code, kernargSize = 0, kernelCodeProperties = 0x0a)
    )).value)

  test("building a kernel reports a compile failure and an absent kernel, and a built kernel launches") {
    val context = TaskContext(RegionMemory(), TaskControl(), _ => Right(()))
    val fine    = loaded(bytes.words(0xbf810000))
    assertEquals(fine.build("kernel"), Right(()))
    assertEquals(fine.execute(singleTask(), context), Right(()))
    fine.build("absent") match {
      case Left(Fault.NotFound(_)) => ()
      case other                   => fail(s"expected a missing kernel, got $other")
    }
    val broken = loaded(Array.fill(4)(0xff.toByte))
    broken.build("kernel") match {
      case Left(Fault.InvalidProgram(_)) => ()
      case other                         => fail(s"expected an invalid program, got $other")
    }
  }
}
