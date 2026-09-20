package net.kurobako.gemstone.isa.amdgpu

import CodeObject.*

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*
import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.execution.*
import net.kurobako.gemstone.binary.{LittleEndian, ScalarByteWidth}
import net.kurobako.gemstone.memory.GuestAddress

final class AllocationAddressSuite extends munit.FunSuite {
  // gfx900: env+8 contains a nested pointer absent from direct args. Increment its pointee,
  // reload into output, and store the exact typed env pointer at output+8.
  private val code = Vec[Long](
    0xc00a0100L, 0x00000000L, 0xbf8cc07fL, 0xc0060202L, 0x00000008L, 0xbf8cc07fL, 0xc0020284L, 0x00000000L, 0xbf8cc07fL,
    0x800a810aL, 0x7e000280L, 0x7e02020aL, 0xdc708000L, 0x00080100L, 0xbf8c0f70L, 0xc0020284L, 0x00000000L, 0xbf8cc07fL,
    0x7e02020aL, 0xdc708000L, 0x00060100L, 0x7e000288L, 0x7e020204L, 0x7e040205L, 0xdc748000L, 0x00060100L, 0xbf810000L
  ).flatMap(value => LittleEndian.toVec(value, ScalarByteWidth(4)))
  private val env    = AllocationId(1)
  private val nested = AllocationId(118)
  private val output = AllocationId(2)
  private final class Memory(override val allocationAddresses: Vec[AllocationAddress]) extends ExecutionMemory {
    val storage = Map(env -> Array.ofDim[Byte](32), nested -> Array.ofDim[Byte](16), output -> Array.ofDim[Byte](16))
    override def read(id: AllocationId, offset: Long :| Positive0, size: Int :| Positive0) =
      if (!storage.contains(id) || offset > storage(id).length - size)
        Left(ExecutionFailure.InvalidRequest("test memory bounds"))
      else Right(Vec.from(storage(id).slice(offset.toInt, offset.toInt + size)))
    override def write(id: AllocationId, offset: Long :| Positive0, bytes: Vec[Byte]) =
      if (!storage.contains(id) || offset > storage(id).length - bytes.size)
        Left(ExecutionFailure.InvalidRequest("test memory bounds"))
      else { val _ = bytes.copyToArray(storage(id), offset.toInt, bytes.size); Right(()) }
    override def recordPointerRelocation(relocation: PointerRelocation): Unit = ()
  }
  private val kernel   = CodeObjectKernel("kernel", Target("gfx900"), 16, 0, 0, 0x80, 0x08, code)
  private val dispatch = KernelDispatch.validated(
    ProgramId(1),
    KernelName("kernel"),
    Dimensions.One,
    Dimensions.One,
    Vec(KernelArgument.Allocation(env, 0), KernelArgument.Allocation(output, 0))
  ).toOption.get
  private def run(memory: Memory, value: CodeObjectKernel = kernel) =
    DispatchInterpreter.execute(
      DispatchInterpreter.prepare(value).toOption.get,
      dispatch,
      memory,
      ExecutionControl.default
    )

  test("nested native pointers dereference unpassed allocations without rewriting payload or scalar words") {
    for (base <- Seq(0x3d9d8000L, 0x7a0010001000L)) {
      val target      = base + 0x10000L
      val descriptors = Vec(
        AllocationAddress(env, 32, Some(GuestAddress(base))),
        AllocationAddress(nested, 16, Some(GuestAddress(target))),
        AllocationAddress(output, 16, None)
      )
      val old = Memory(Vec.empty)
      LittleEndian.write(target, old.storage(env), 8, ScalarByteWidth(8))
      assert(run(old).isLeft, "without an explicit alias the actual nested address must be unmapped")
      val memory = Memory(descriptors)
      LittleEndian.write(
        target,
        memory.storage(env),
        0,
        ScalarByteWidth(8)
      ) // ordinary scalar equal to a pointer; must remain untouched
      LittleEndian.write(target, memory.storage(env), 8, ScalarByteWidth(8))
      val original = memory.storage(env).clone()
      for (value <- Seq(41L, 70L)) {
        LittleEndian.write(value, memory.storage(nested), 0, ScalarByteWidth(4))
        assertEquals(run(memory), Right(()))
        assertEquals(LittleEndian.decode(memory.storage(nested), 0, ScalarByteWidth(4)), value + 1)
        assertEquals(LittleEndian.decode(memory.storage(output), 0, ScalarByteWidth(4)), value + 1)
        assertEquals(LittleEndian.decode(memory.storage(output), 8, ScalarByteWidth(8)), base)
        assert(memory.storage(env).sameElements(original))
      }
      // The previous encoded pointer representation is still a valid storage alias.
      LittleEndian.write(0x400000000000L + 117L * 0x100000000L, memory.storage(env), 8, ScalarByteWidth(8))
      assertEquals(run(memory), Right(()))
      assertEquals(LittleEndian.decode(memory.storage(nested), 0, ScalarByteWidth(4)), 72L)
    }
  }

  test("native aliases cannot shadow ELF, dispatch, kernarg, or live synthetic address ranges") {
    val value = kernel.copy(
      codeAddress = 0x1000L,
      textAddress = 0x1000L,
      text = code,
      readOnlyAddress = 0x2000L,
      readOnly = Array.ofDim[Byte](64),
      globalMemory = Array(0x3000L -> Array.ofDim[Byte](64))
    )
    for (
      address <- Seq(
        0x1000L,
        0x2000L,
        0x3000L,
        0x100000000000L,
        0x200000000000L,
        0x400000000000L,
        0x400000000000L + 117L * 0x100000000L,
        Long.MaxValue - 1L
      )
    ) {
      val memory = Memory(Vec(
        AllocationAddress(env, 32, Some(GuestAddress(address))),
        AllocationAddress(nested, 16, None)
      ))
      assert(run(memory, value).left.exists(_.isInstanceOf[ExecutionFailure.InvalidRequest]), s"alias $address")
      assert(memory.storage.values.forall(_.forall(_ == 0)))
    }
    val overlapping =
      Memory(Vec(
        AllocationAddress(env, 32, Some(GuestAddress(0x8000L))),
        AllocationAddress(nested, 16, Some(GuestAddress(0x8010L)))
      ))
    assert(run(overlapping).left.exists(_.isInstanceOf[ExecutionFailure.InvalidRequest]))
  }
}
