package net.kurobako.gemstone.isa.spirv.v1_6

import java.util.concurrent.atomic.AtomicReference
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.isa.spirv.v1_6 as spv
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*
class ExecutionMemorySuite extends munit.FunSuite {
  import execution.*

  test("dirty-page flush preserves sparse runs and writes crossing bitmap and page boundaries") {
    val offsets = Vec[Long >= 0](
      0L,
      8L,
      62L,
      66L,
      4094L,
      8191L
    )
    val parameters   = offsets.indices.map(index => operand("IdResult", 10 + index))
    val voidType     = operand("IdResult", 1)
    val u32          = operand("IdResult", 2)
    val pointerType  = operand("IdResult", 3)
    val functionType = operand("IdResult", 4)
    val function     = operand("IdResult", 5)
    val value        = operand("IdResult", 6)
    val module       = Module(
      Header(ISA.version, Word(0), Word(32), Word(0)),
      Vec[Instruction](
        Instruction.TypeVoid(voidType),
        Instruction.TypeInt(u32, operand("LiteralInteger", 32), operand("LiteralInteger", 0)),
        Instruction.TypePointer(pointerType, operand("StorageClass", spv.StorageClass.CrossWorkgroup), u32),
        Instruction.TypeFunction(functionType, voidType, Vec.fill(offsets.size)(pointerType)),
        Instruction.Constant(u32, value, operand("LiteralContextDependentNumber", 0x12345678)),
        Instruction.EntryPoint(
          operand("ExecutionModel", spv.ExecutionModel.Kernel),
          function,
          stringOperand("sparse"),
          Vec.empty
        ),
        Instruction.Function(voidType, function, operand("FunctionControl", spv.FunctionControl.None), functionType)
      ) ++ Vec.from(parameters.map(Instruction.FunctionParameter(pointerType, _))) ++
        Vec[Instruction](Instruction.Label(operand("IdResult", 20))) ++
        Vec.from(parameters.map(Instruction.Store(_, value, None))) ++
        Vec[Instruction](
          Instruction.Load(u32, operand("IdResult", 21), parameters(2), None),
          Instruction.Store(parameters(0), operand("IdRef", 21), None),
          Instruction.Load(u32, operand("IdResult", 22), parameters(4), None),
          Instruction.Store(parameters(1), operand("IdRef", 22), None),
          Instruction.Return(),
          Instruction.FunctionEnd()
        ),
      Endianness.Little
    )
    val allocation = AllocationId(200L)
    val writes     = AtomicReference(Vec.empty[(Long, Int)])
    val memory     = ArrayMemory(
      allocation,
      8200,
      written = (offset, size) => {
        writes.updateAndGet(_ :+ (offset -> size))
        ()
      }
    )
    val dispatch = KernelTask.validated(
      kernelName = "sparse",
      groupSize = Dimensions.One,
      groupCount = Dimensions.One,
      arguments = offsets.map(KernelTask.Argument.Memory(allocation, _)),
      globalSize = None
    ).toOption.get
    assertEquals(executeUnchecked(module, dispatch, memory), Right(()))
    assertEquals(writes.get(), Vec(0L -> 4, 8L -> 4, 62L -> 8, 4094L -> 2, 4096L -> 2, 8191L -> 1, 8192L -> 3))
    val expected = Array.ofDim[Byte](8200)
    offsets.foreach { offset =>
      val arrayOffset = offset.raw.toInt.unsafe[Int >= 0]
      LittleEndian.write(0x12345678L, expected, arrayOffset.raw, 4)
    }
    assertEquals(memory.bytes, Vec.from(expected))
  }
}
