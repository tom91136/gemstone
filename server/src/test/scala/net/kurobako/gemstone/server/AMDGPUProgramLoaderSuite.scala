package net.kurobako.gemstone.server

import net.kurobako.gemstone.Col
import net.kurobako.gemstone.execution.*

import java.util.concurrent.atomic.AtomicInteger

final class AMDGPUProgramLoaderSuite extends munit.FunSuite {
  private object EmptyMemory extends ExecutionMemory {
    override def read(id: AllocationId, offset: Long, size: Int)         = Right(Col.empty)
    override def write(id: AllocationId, offset: Long, bytes: Col[Byte]) = Right(())
  }

  test("AMDGPU kernel aliases select the indexed implementation matching the code-object target") {
    val generic = "wrapped_target_archE"
    val gfx900  = "wrapped_target_archE900E"
    val gfx906  = "wrapped_target_archE906E"
    val aliases = AMDGPUKernelVariants.select(Set(generic, gfx900, gfx906), "gfx900").toOption.get

    assertEquals(aliases(generic), gfx900)
    assertEquals(aliases(gfx900), gfx900)
    assertEquals(aliases(gfx906), gfx900)
  }

  test("AMDGPU kernel aliases preserve unrelated symbols") {
    val aliases = AMDGPUKernelVariants.select(Set("first", "second"), "gfx900").toOption.get

    assertEquals(aliases, Map("first" -> "first", "second" -> "second"))
  }

  test("AMDGPU kernel aliases reject a variant group without the code-object target") {
    val generic = "wrapped_target_archE"
    val gfx906  = "wrapped_target_archE906E"

    assertEquals(
      AMDGPUKernelVariants.select(Set(generic, gfx906), "gfx900"),
      Left("AMDGPU kernel variant wrapped_target_archE has no gfx900 implementation")
    )
  }

  test("AMDGPU code-object resolution is initialized lazily and only once") {
    val initializations = AtomicInteger()
    val resolver        = AMDGPUDeferredKernelResolver { () =>
      initializations.incrementAndGet()
      Right(name => Right(name.length))
    }

    assertEquals(initializations.get(), 0)
    assertEquals(resolver.resolve("first"), Right(5))
    assertEquals(resolver.resolve("second"), Right(6))
    assertEquals(initializations.get(), 1)
  }

  test("AMDGPU code-object resolution caches initialization failures") {
    val initializations = AtomicInteger()
    val failure         = ExecutionFailure.InvalidRequest("invalid code object")
    val resolver        = AMDGPUDeferredKernelResolver[String] { () =>
      initializations.incrementAndGet()
      Left(failure)
    }

    assertEquals(initializations.get(), 0)
    assertEquals(resolver.resolve("first"), Left(failure))
    assertEquals(resolver.resolve("second"), Left(failure))
    assertEquals(initializations.get(), 1)
  }

  test("AMDGPU code-object loading rejects an invalid ELF header before deferring its index") {
    val result = AMDGPUCodeObjectProgramLoader.load(Col.empty)

    assertEquals(result, Left(ExecutionFailure.InvalidRequest("expected a little-endian ELF64 code object")))
  }

  test("AMDGPU code-object loading defers malformed section-table failures until first launch") {
    val header = Array.ofDim[Byte](64)
    header(0) = 0x7f.toByte
    header(1) = 'E'.toByte
    header(2) = 'L'.toByte
    header(3) = 'F'.toByte
    header(4) = 2
    header(5) = 1
    header(18) = 224.toByte
    val target = "amdgcn-amd-amdhsa--gfx900\u0000".getBytes(java.nio.charset.StandardCharsets.US_ASCII)
    System.arraycopy(target, 0, header, 24, target.length)
    val program  = AMDGPUCodeObjectProgramLoader.load(Col.unsafeFromArray(header)).toOption.get
    val dispatch = KernelDispatch.create(
      ProgramId.fromLong(1L).toOption.get,
      "kernel",
      Dimensions.One,
      Dimensions.One,
      Col.empty
    ).toOption.get

    assertEquals(
      program.launch(dispatch, EmptyMemory, ExecutionControl.default),
      Left(ExecutionFailure.InvalidRequest("invalid ELF section table"))
    )
  }
}
