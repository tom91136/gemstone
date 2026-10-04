package net.kurobako.gemstone.isa.amdgpu

import ELF.*
import fixtures.*
import net.kurobako.gemstone.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

class AllocationAddressSuite extends munit.FunSuite {
  // Machine bytes are fixed ISA fixtures: deriving them from our encoder would hide decoder errors.

  // gfx900: env+8 contains a nested pointer absent from direct args. Increment its pointee,
  // reload into output, and store the exact typed env pointer at output+8.
  private val code = Vec[Long](
    0xc00a0100L, 0x00000000L, 0xbf8cc07fL, 0xc0060202L, 0x00000008L, 0xbf8cc07fL, 0xc0020284L, 0x00000000L, 0xbf8cc07fL,
    0x800a810aL, 0x7e000280L, 0x7e02020aL, 0xdc708000L, 0x00080100L, 0xbf8c0f70L, 0xc0020284L, 0x00000000L, 0xbf8cc07fL,
    0x7e02020aL, 0xdc708000L, 0x00060100L, 0x7e000288L, 0x7e020204L, 0x7e040205L, 0xdc748000L, 0x00060100L, 0xbf810000L
  ).flatMap(value => LittleEndian.toVec(value, 4))
  private val env    = AllocationId(1)
  private val nested = AllocationId(118)
  private val output = AllocationId(2)
  private class Memory(nativeAddresses: Map[AllocationId, GuestAddress])
      extends vm.Memory with TestAddressSpace {
    val storage: Map[AllocationId.Type, Array[Byte]] =
      Map(env -> Array.ofDim[Byte](32), nested -> Array.ofDim[Byte](16), output -> Array.ofDim[Byte](16))
    override protected def regionSizes: Iterable[(AllocationId, Long)]       = storage.view.mapValues(_.length.toLong)
    override protected def regionAddress(region: AllocationId): GuestAddress = nativeAddresses(region)
    override def read(id: AllocationId, offset: Long >= 0, size: Int >= 0): Either[Fault, Vec[Byte]] =
      if (!storage.contains(id) || offset.raw > storage(id).length - size.raw)
        Left(Fault.InvalidTask("test memory bounds"))
      else Right(Vec.from(storage(id).slice(offset.raw.toInt, offset.raw.toInt + size.raw)))
    override def write(id: AllocationId, offset: Long >= 0, bytes: Vec[Byte]): Either[Fault, Unit] =
      if (!storage.contains(id) || offset.raw > storage(id).length - bytes.size)
        Left(Fault.InvalidTask("test memory bounds"))
      else { val _ = bytes.copyToArray(storage(id), offset.raw.toInt, bytes.size); Right(()) }
  }
  private val kernel = CodeObjectKernel(
    name = "kernel",
    target = Target("gfx900"),
    kernargSize = 16,
    groupSegmentSize = 0,
    privateSegmentSize = 0,
    computePgmRsrc2 = 0x80,
    kernelCodeProperties = 0x08,
    code = code
  )
  private val dispatch = KernelTask.validated(
    "kernel",
    Dimensions.One,
    Dimensions.One,
    Vec(
      KernelTask.Argument.Memory(env, 0L),
      KernelTask.Argument.Memory(output, 0L)
    )
  ).toOption.get
  private def run(memory: Memory, value: CodeObjectKernel = kernel) =
    executeKernel(
      VMCompiler().compile(value).toOption.get,
      dispatch,
      memory,
      TaskControl()
    )

  test("nested native pointers dereference unpassed allocations without rewriting payload or scalar words") {
    for (base <- Seq(0x3d9d8000L, 0x7a0010001000L)) {
      val target = base + 0x10000L
      val memory = Memory(Map(
        env    -> GuestAddress(base),
        nested -> GuestAddress(target),
        output -> GuestAddress(base + 0x20000L)
      ))
      LittleEndian.write(
        target,
        memory.storage(env),
        0,
        8
      ) // ordinary scalar equal to a pointer; must remain untouched
      LittleEndian.write(target, memory.storage(env), 8, 8)
      val original = memory.storage(env).clone()
      for (value <- Seq(41L, 70L)) {
        LittleEndian.write(value, memory.storage(nested), 0, 4)
        assertEquals(run(memory), Right(()))
        assertEquals(LittleEndian.decode(memory.storage(nested), 0, 4), value + 1)
        assertEquals(LittleEndian.decode(memory.storage(output), 0, 4), value + 1)
        assertEquals(LittleEndian.decode(memory.storage(output), 8, 8), base)
        assert(memory.storage(env).sameElements(original))
      }
    }
  }
}
